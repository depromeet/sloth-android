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
import com.depromeet.sloth.data.network.login.LoginResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.ui.base.BaseActivity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.squareup.okhttp.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val viewModel: LoginViewModel
        get() = LoginViewModel()

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

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
                com.depromeet.sloth.R.id.google_login -> loginWithGoogle()
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
                    viewModel.getAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginResponse> -> Log.e("인증정보 수신 성공", it.data.toString())
                            is LoginState.Error -> Log.e("인증정보 수신 실패", it.exception.message ?: "Unsupported Exception")
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
                    viewModel.getAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginResponse> -> Log.e("인증정보 수신 성공", it.data.toString())
                            is LoginState.Error -> Log.e("인증정보 수신 실패", it.exception.message ?: "Unsupported Exception")
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
            val account = completedTask.getResult(ApiException::class.java)
            val serverAuthCode = account?.serverAuthCode.toString()
            val idToken = account?.idToken.toString()
            val email = account?.email.toString()
            val displayName = account?.displayName.toString()

            Log.d("serverAuthCode", serverAuthCode)
            Log.d("idToken", idToken)
            Log.d("email", email)
            Log.d("displayName", displayName)

            val client = com.squareup.okhttp.OkHttpClient()
            val requestBody = FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", BuildConfig.GOOGLE_CLIENT_ID)
                .add("client_secret", BuildConfig.GOOGLE_CLIENT_SECRET)
                .add("redirect_uri", "")
                .add("code", serverAuthCode)
                .build()
            val request: Request = Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(request: Request?, e: IOException) {
                    Log.e("error", e.toString())
                }

                @Throws(IOException::class)
                override fun onResponse(response: Response) {
                    try {
                        val jsonObject = JSONObject(response.body().string())
                        val message = jsonObject.toString(5)
                        Log.i("message", message)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("로그인 실패", "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun onStart() {
        super.onStart()
        val gsa = GoogleSignIn.getLastSignedInAccount(this)

        //기존 로그인 사용자 확인
        if (gsa != null) {
            //updateUI(account)
        }
    }
}