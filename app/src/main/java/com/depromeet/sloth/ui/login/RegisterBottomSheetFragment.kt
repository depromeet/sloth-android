package com.depromeet.sloth.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterBottomBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseBottomSheetFragment
import com.depromeet.sloth.ui.home.HomeActivity
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterBottomSheetFragment : BaseBottomSheetFragment<FragmentRegisterBottomBinding>(R.layout.fragment_register_bottom) {

    // private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_login)
    private val loginViewModel: LoginViewModel by activityViewModels()

    private val deviceId: String by lazy {
        Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = loginViewModel
        }

        initObserver()
    }

    private fun initObserver() = with(loginViewModel) {
        repeatOnStarted {
            launch {
                navigateToPrivatePolicyEvent.collect { tag ->
                    showSlothPolicyWebview(tag)
                }
            }

            launch {
                registerAgreeEvent.collect {
                    createAndRegisterNotificationToken(deviceId)
                }
            }

            launch {
                registerCancelEvent.collect {
                    val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomToLogin()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                registerNotificationTokenEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> {
                                closeRegisterBottomSheet()

                                startActivity(
                                    Intent(requireContext(), HomeActivity::class.java)
                                )
                                requireActivity().finish()

                                // TODO navigateToLessonTodayFragment
                                // findNavController().navigate(R.id.action_nav_login_to_today_lesson)
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
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

    private fun closeRegisterBottomSheet() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomToLogin()
        findNavController().safeNavigate(action)
    }

    //TODO 웹뷰 액티비티를 제거
    private fun showSlothPolicyWebview(tag: String) {
        startActivity(
            Intent(requireContext(), SlothPolicyWebViewActivity::class.java)
        )
//        val action =
//            RegisterBottomSheetFragmentDirections.actionRegisterBottomToSlothPolicyWebview(tag)
//        findNavController().safeNavigate(action)
    }
}