package com.depromeet.sloth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.databinding.FragmentLoginBottomBinding
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.util.GOOGLE
import com.depromeet.sloth.util.KAKAO
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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginBottomSheetFragment : BottomSheetDialogFragment() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBottomBinding
    private var _binding: FragmentLoginBottomBinding? = null

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var loginListener: LoginListener

    fun setLoginListener(loginListener: LoginListener) {
        this.loginListener = loginListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                Timber.tag("로그인 실패").d(error)
            } else if (token != null) {
                Timber.tag("로그인 성공 -> accessToken ").d(token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = KAKAO
                    ).let { result ->
                        when (result) {
                            is Result.Success<LoginSlothResponse> -> {
                                Timber.tag("인증 정보 수신 성공").d(result.data.toString())
                                if (result.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }

                            }
                            is Result.Error -> {
                                Timber.tag("인증 정보 수신 실패").d(result.throwable)
                                loginListener.onError()
                            }
                            else -> {}
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
                Timber.tag("로그인 실패").d(error.message.toString())
            } else if (token != null) {
                Timber.tag("로그인 성공 -> accessToken ").d(token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = KAKAO
                    ).let { result ->
                        when (result) {
                            is Result.Success<LoginSlothResponse> -> {
                                Timber.tag("인증정보 수신 성공").d(result.data.toString())
                                if (result.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }
                            }
                            is Result.Error -> {
                                Timber.tag("인증정보 수신 실패").d(result.throwable)
                                loginListener.onError()
                            }
                            else -> {}
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
                    loginViewModel.fetchGoogleAuthInfo(this).let { result ->
                        when (result) {
                            is Result.Success<LoginGoogleResponse> -> {
                                Timber.tag("Success").d("${result.data}")
                                accessToken = result.data.accessToken
                            }
                            is Result.Error -> {
                                Timber.tag("Error").d(result.throwable)
                                loginListener.onError()
                            }
                            else -> {}
                        }
                    }

                    loginViewModel.fetchSlothAuthInfo(accessToken, GOOGLE).let { result ->
                        when (result) {
                            is Result.Success<LoginSlothResponse> -> {
                                Timber.tag("Success").d("${result.data}")
                                if (result.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }
                            }
                            is Result.Error -> {
                                Timber.tag("Error").d(result.throwable)
                                loginListener.onError()
                            }
                            else -> {}
                        }
                    }
                } ?: Timber.tag("구글 서버 인증 실패").e("Authentication failed")
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.tag("로그인 실패").e("signInResult:failed code=%s", e.statusCode)
        }
    }

    companion object {
        const val TAG = "LoginBottomSheetFragment"
    }
}