package com.depromeet.sloth.ui.manage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.databinding.FragmentUpdateMemberBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.setEditTextFocus
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.ui.base.BaseDialogFragment
import com.depromeet.sloth.util.DebounceEditTextListener
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class UpdateMemberFragment :
    BaseDialogFragment<FragmentUpdateMemberBinding>(R.layout.fragment_update_member) {

    private val manageViewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_home)

    private val updateMemberTextChangeListener by lazy {
        DebounceEditTextListener(
            scope = manageViewModel.viewModelScope,
            onDebounceEditTextChange = manageViewModel::setMemberName,
            debouncePeriod = 0L
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = manageViewModel
        }
        initViews()
        initObserver()
    }

    override fun initViews() = with(binding) {
        super.initViews()
        focusInputForm(etMemberName)
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                updateMemberInfoEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<MemberUpdateResponse> -> {
                                showToast(
                                    requireContext(),
                                    getString(R.string.member_update_success)
                                )
                                setMemberName(result.data.memberName)
                                setPreviousMemberName(result.data.memberName)
                                Timber.d("${memberName.value} ${previousMemberName.value}")
                                closeProfileUpdateDialog()
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@UpdateMemberFragment
                                    ) {
                                        removeAuthToken()
                                    }

                                    else -> {
                                        Timber.tag("Update Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.member_update_fail)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun focusInputForm(editText: EditText) = with(manageViewModel) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                Timber.d(memberName.value)
                if (memberName.value.isEmpty() || memberName.value == previousMemberName.value) {
                // if (memberName.value.isEmpty()) {
                    setUpdateMemberValidation(false)
                    Timber.d("${memberName.value} == ${previousMemberName.value} setUpdateMemberValidation False")
                } else {
                    setUpdateMemberValidation(true)
                }
            }
        })
        setEditTextFocus(editText)
    }

    private fun closeProfileUpdateDialog() {
        val action =
            UpdateMemberFragmentDirections.actionUpdateMemberFragmentToManage()
        findNavController().safeNavigate(action)
    }

    override fun onResume() {
        super.onResume()
        binding.etMemberName.addTextChangedListener(updateMemberTextChangeListener)
    }

    override fun onPause() {
        binding.etMemberName.removeTextChangedListener(updateMemberTextChangeListener)
        super.onPause()
    }

    override fun onDestroyView() {
        binding.etMemberName.setOnEditorActionListener(null)
        super.onDestroyView()
    }

}
