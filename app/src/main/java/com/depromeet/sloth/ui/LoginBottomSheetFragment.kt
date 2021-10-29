package com.depromeet.sloth.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.databinding.FragmentLoginBottomBinding
import com.depromeet.sloth.ui.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private val loginViewModel: LoginViewModel = LoginViewModel()

    private lateinit var binding: FragmentLoginBottomBinding
    private var _binding: FragmentLoginBottomBinding? = null

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var loginListener: LoginListener

    private val pm: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    fun setLoginListener(loginListener: LoginListener) {
        this.loginListener = loginListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBottomBinding.inflate(inflater, container, false)
        binding = _binding!!

        val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestIdToken(googleClientId)
            .requestServerAuthCode(googleClientId)
            .requestEmail()
            .build()

        loginLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.clLoginButtonKakao.setOnClickListener {
            when (UserApiClient.instance.isKakaoTalkLoginAvailable(requireActivity())) {
                true -> loginWithKakaoTalk()
                false -> loginWithKakaoAccount()
            }
        }

        binding.clLoginButtonGoogle.setOnClickListener { view ->
            when (view.id) {
                R.id.cl_login_button_google -> loginWithGoogle()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(requireActivity()) { token: OAuthToken?, error: Throwable? ->
            if (error != null) {
                Log.d("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.d("로그인 성공 -> accessToken ", token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Log.d("인증정보 수신 성공", it.data.toString())
                                loginListener.onSuccess()
                            }
                            is LoginState.Error -> {
                                Log.d("인증정보 수신 실패", it.exception.message ?: "Unsupported Exception")
                                loginListener.onError()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(
            context = requireActivity(),
            prompts = listOf(Prompt.LOGIN) //보안을 위해 기존의 로그인 여부와 상관없이 재인증 요청시 필요
        ) { token, error ->
            if (error != null) {
                Log.d("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.d("로그인 성공 -> accessToken ", token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Log.d("인증정보 수신 성공", it.data.toString())
                                loginListener.onSuccess()
                            }
                            is LoginState.Error -> {
                                Log.d("인증정보 수신 실패", it.exception.message ?: "Unsupported Exception")
                                loginListener.onError()
                            }
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
                    var accessToken = "userToken"
                    loginViewModel.fetchGoogleAuthInfo(this).let {
                        when (it) {
                            is LoginState.Success<LoginGoogleResponse> -> {
                                Log.d("Success", "${it.data}")
                                accessToken = it.data.access_token
                            }

                            is LoginState.Error -> {
                                Log.d("Error", "${it.exception}")
                                loginListener.onError()
                            }
                        }
                    }

                    loginViewModel.fetchSlothAuthInfo(accessToken, "GOOGLE").let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Log.d("Success", "${it.data}")
                                loginViewModel.saveAuthToken(pm, it.data.accessToken, it.data.refreshToken)
                                loginListener.onSuccess()
                            }
                            is LoginState.Error -> {
                                Log.d("Error", "${it.exception}")
                                loginListener.onError()
                            }
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

    companion object {
        const val TAG = "LoginBottomSheetFragment"
    }
}