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
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.databinding.FragmentLoginBottomBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.util.GOOGLE
import com.depromeet.sloth.util.KAKAO
import com.depromeet.sloth.util.Result
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginBottomSheetFragment : BottomSheetDialogFragment() {

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

        binding.apply {
            vm = loginViewModel
            lifecycleOwner = viewLifecycleOwner
        }

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

        initObserver()

        return binding.root
    }

    private fun initObserver() = with(loginViewModel) {
        repeatOnStarted {
            launch {
                googleLoginClickEvent
                    .collect {
                        loginWithGoogle()
                    }
            }

            launch {
                kakaoLoginClickEvent
                    .collect {
                        loginWithKakao()
                    }
            }
            launch {
                googleLoginEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> (activity as LoginActivity).showProgress()
                            is Result.UnLoading -> (activity as LoginActivity).hideProgress()
                            is Result.Success<LoginGoogleResponse> -> {
                                fetchSlothAuthInfo(result.data.accessToken, GOOGLE)
                            }

                            is Result.Error -> {
                                Timber.tag("Google Login Fail").d(result.throwable)
                                loginListener.onError()
                            }
                        }
                    }
            }

            launch {
                slothLoginEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> (activity as LoginActivity).showProgress()
                            is Result.UnLoading -> (activity as LoginActivity).hideProgress()
                            is Result.Success<LoginSlothResponse> -> {
                                if (result.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }
                            }

                            is Result.Error -> {
                                Timber.tag("Login Fail").d(result.throwable)
                                loginListener.onError()
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
            authCode?.run {
                loginViewModel.fetchGoogleAuthInfo(this)
            } ?: Timber.tag("구글 서버 인증 실패").e("Authentication failed")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.tag("로그인 실패").e("signInResult:failed code=%s", e.statusCode)
        }
    }

    private suspend fun loginWithKakao() {
        try {
            val oAuthToken = UserApiClient.loginWithKakao(requireContext())
            loginViewModel.fetchSlothAuthInfo(oAuthToken.accessToken, KAKAO)
        } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                Timber.tag(TAG).d("사용자가 명시적으로 취소")
            } else {
                Timber.tag(TAG).e(error, "인증 에러 발생")
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "LoginBottomSheetFragment"
    }
}