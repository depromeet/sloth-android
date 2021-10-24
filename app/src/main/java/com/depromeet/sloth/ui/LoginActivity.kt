package com.depromeet.sloth.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.depromeet.sloth.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.ui.base.BaseActivity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.login.LoginGoogleResponse

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val viewModel: LoginViewModel = LoginViewModel()

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    private lateinit var accessToken: String

    private lateinit var refreshToken: String

    private val pm: PreferenceManager = PreferenceManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestIdToken(googleClientId)
            .requestServerAuthCode(googleClientId)
            .requestEmail()
            .build()

        loginLauncher = registerForActivityResult(
            StartActivityForResult()
        )
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.kakaoLogin.setOnClickListener {
            when (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                true -> loginWithKakaoTalk()
                false -> loginWithKakaoAccount()
            }
        }

        binding.googleLogin.setOnClickListener { view ->
            when (view.id) {
                R.id.google_login -> loginWithGoogle()
            }
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(
            context = this,
        ) { token: OAuthToken?, error: Throwable? ->
            if (error != null) {
                Log.e("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.e("로그인 성공 -> accessToken ", token.toString())
                mainScope {
                    viewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> Log.e(
                                "인증정보 수신 성공",
                                it.data.toString()
                            )
                            is LoginState.Error -> Log.e(
                                "인증정보 수신 실패",
                                it.exception.message ?: "Unsupported Exception"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(
            context = this,
            prompts = listOf(Prompt.LOGIN) //보안을 위해 기존의 로그인 여부와 상관없이 재인증 요청시 필요
        ) { token, error ->
            if (error != null) {
                Log.e("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.e("로그인 성공 -> accessToken ", token.toString())
                mainScope {
                    viewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> Log.e(
                                "인증정보 수신 성공",
                                it.data.toString()
                            )
                            is LoginState.Error -> Log.e(
                                "인증정보 수신 실패",
                                it.exception.message ?: "Unsupported Exception"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loginWithGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        loginLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val authCode = completedTask.getResult(ApiException::class.java)?.serverAuthCode

            mainScope {
                authCode?.run {
                    viewModel.fetchGoogleAuthInfo(this).let {
                        when (it) {
                            is LoginState.Success<LoginGoogleResponse> -> {
                                Log.d("Success", "${it.data}")
                                accessToken = it.data.access_token
                                Log.d("accessToken", accessToken)
                            }

                            is LoginState.Error ->
                                Log.d("Error", "${it.exception}")
                        }
                    }

                    viewModel.fetchSlothAuthInfo(accessToken, "GOOGLE").let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Log.d("Success", "${it.data}")
                                accessToken = it.data.accessToken
                                refreshToken = it.data.refreshToken

                                Log.d("accessToken", accessToken)
                                Log.d("refreshToken", refreshToken)

                                viewModel.saveAuthToken(pm, accessToken, refreshToken)
                            }
                            is LoginState.Error ->
                                Log.d("Error", "${it.exception}")
                        }
                    }
                } ?: Log.e("구글 서버 인증 실패", "Authentication failed")
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("로그인 실패", "signInResult:failed code=" + e.statusCode)
        }
    }
}