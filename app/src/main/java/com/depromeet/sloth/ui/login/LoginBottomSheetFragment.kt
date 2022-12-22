package com.depromeet.sloth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.databinding.FragmentLoginBottomBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseBottomSheetFragment
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
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginBottomSheetFragment : BaseBottomSheetFragment<FragmentLoginBottomBinding>(R.layout.fragment_login_bottom) {

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_home)

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var loginListener: LoginListener

    private val deviceId: String by lazy {
        Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = loginViewModel
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
    }

    private fun initObserver() = with(loginViewModel) {
        repeatOnStarted {
            launch {
                googleLoginClickEvent
                    .collect { loginWithGoogle() }
            }

            launch {
                kakaoLoginClickEvent
                    .collect { loginWithKakao() }
            }
            launch {
                googleLoginEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
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
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<LoginSlothResponse> -> {
                                if (result.data.isNewMember) {
                                    showRegisterBottom()
                                } else {
                                    createAndRegisterNotificationToken(deviceId)
                                }
                            }
                            is Result.Error -> {
                                Timber.tag("Login Fail").d(result.throwable)
                                loginListener.onError()
                            }
                        }
                    }
            }

            launch {
                // 로그인이 성공하면 토큰을 서버에 전달해주는 방식으로 로직 변경
                // 토큰을 전달한 다음 홈 화면으로 이동
                registerNotificationTokenEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> {
                                closeLoginBottomSheet()
                                navigateToTodayLesson()
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext(), this@LoginBottomSheetFragment) {
                                        removeAuthToken()
                                    }
                                    else -> Timber.tag("Register Error").d(result.throwable)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun navigateToTodayLesson() {
        val action = LoginBottomSheetFragmentDirections.actionLoginBottomToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun showRegisterBottom() {
        val action = LoginBottomSheetFragmentDirections.actionLoginBottomToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun closeLoginBottomSheet() {
        val action = LoginBottomSheetFragmentDirections.actionLoginBottomToLogin()
        findNavController().safeNavigate(action)
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

    companion object {
        const val TAG = "LoginBottomSheetFragment"
    }
}