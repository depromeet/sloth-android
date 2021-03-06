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
import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.databinding.FragmentLoginBottomBinding
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
                Timber.tag("????????? ??????").d(error)
            } else if (token != null) {
                Timber.tag("????????? ?????? -> accessToken ").d(token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Timber.tag("???????????? ?????? ??????").d(it.data.toString())
                                if (it.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }

                            }
                            is LoginState.Error -> {
                                Timber.tag("???????????? ?????? ??????").d(it.exception)
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
            prompts = listOf(Prompt.LOGIN) //????????? ?????? ????????? ????????? ????????? ???????????? ????????? ????????? ??????
        ) { token, error ->
            if (error != null) {
                Timber.tag("????????? ??????").d(error.message.toString())
            } else if (token != null) {
                Timber.tag("????????? ?????? -> accessToken ").d(token.toString())
                mainScope {
                    loginViewModel.fetchSlothAuthInfo(
                        accessToken = token.accessToken,
                        socialType = "KAKAO"
                    ).let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Timber.tag("???????????? ?????? ??????").d(it.data.toString())
                                if (it.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }
                            }
                            is LoginState.Error -> {
                                Timber.tag("???????????? ?????? ??????").d(it.exception)
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
                                Timber.tag("Success").d("${it.data}")
                                accessToken = it.data.access_token
                            }

                            is LoginState.Error -> {
                                Timber.tag("Error").d(it.exception)
                                loginListener.onError()
                            }
                        }
                    }

                    loginViewModel.fetchSlothAuthInfo(accessToken, "GOOGLE").let {
                        when (it) {
                            is LoginState.Success<LoginSlothResponse> -> {
                                Timber.tag("Success").d("${it.data}")
                                if (it.data.isNewMember) {
                                    loginListener.onSuccessWithNewMember()
                                } else {
                                    loginListener.onSuccessWithRegisteredMember()
                                }
                            }
                            is LoginState.Error -> {
                                Timber.tag("Error").d(it.exception)
                                loginListener.onError()
                            }
                        }
                    }
                } ?: Timber.tag("?????? ?????? ?????? ??????").e("Authentication failed")
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.tag("????????? ??????").e("signInResult:failed code=%s", e.statusCode)
        }
    }

    companion object {
        const val TAG = "LoginBottomSheetFragment"
    }
}