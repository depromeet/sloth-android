package com.depromeet.presentation.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.BuildConfig
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentLoginBottomDialogBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseBottomSheetFragment
import com.depromeet.presentation.util.KAKAO
import com.depromeet.presentation.util.loginWithKakao
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
class LoginBottomSheetFragment :
    BaseBottomSheetFragment<FragmentLoginBottomDialogBinding>(R.layout.fragment_login_bottom_dialog) {

    private val viewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_main)

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initObserver()
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.googleLoginEvent.collect {
                    loginWithGoogle()
                }
            }

            launch {
                viewModel.kakaoLoginEvent.collect {
                    loginWithKakao()
                }
            }

            launch {
                viewModel.navigateToRegisterBottomSheetEvent.collect {
                    showRegisterBottomSheet()
                }
            }

            launch {
                viewModel.registerNotificationTokenSuccessEvent.collect {
                    navigateToTodayLesson()
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToTodayLesson() {
        val action = LoginBottomSheetFragmentDirections.actionLoginBottomDialogToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun showRegisterBottomSheet() {
        val action = LoginBottomSheetFragmentDirections.actionLoginBottomDialogToRegisterBottomDialog()
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
                viewModel.googleLogin(this)
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
            viewModel.slothLogin(oAuthToken.accessToken, KAKAO)
        } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                Timber.d("사용자가 명시적으로 취소")
            } else {
                Timber.e(error, "인증 에러 발생")
            }
        }
    }
}