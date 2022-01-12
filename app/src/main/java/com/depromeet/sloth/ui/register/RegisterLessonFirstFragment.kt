package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.manage.ManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterLessonFirstFragment : BaseFragment<FragmentRegisterLessonFirstBinding>() {

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: RegisterViewModel by activityViewModels()

    lateinit var accessToken: String
    lateinit var refreshToken: String

    override fun getViewBinding(): FragmentRegisterLessonFirstBinding =
        FragmentRegisterLessonFirstBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()
    }
}