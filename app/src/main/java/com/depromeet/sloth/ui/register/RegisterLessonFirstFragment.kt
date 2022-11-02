package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.UiState
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterLessonFirstFragment :
    BaseFragment<FragmentRegisterLessonFirstBinding>(R.layout.fragment_register_lesson_first) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonCategoryList.value
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonSiteList.value
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }

        initObserver()
        initNavigation()
        initViews()
    }

    private fun initObserver() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                lessonCategoryListState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                viewModel.setLessonCategoryList(uiState.data)
                                bindAdapter(
                                    lessonCategoryAdapter,
                                    binding.spnRegisterLessonCategory,
                                    viewModel.lessonCategorySelectedItemPosition.value!!
                                )
                            }
                            is UiState.Unauthorized -> {
                                showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                                hideProgress()
                            }
                            is UiState.Error -> showToast(getString(R.string.cannot_get_lesson_category))
                        }
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                lessonSiteListState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                //TODO UDF 에 위반 -> 코드 개선
                                viewModel.setLessonSiteList(uiState.data)
                                bindAdapter(
                                    lessonSiteAdapter,
                                    binding.spnRegisterLessonSite,
                                    viewModel.lessonSiteSelectedItemPosition.value!!
                                )
                                hideProgress()
                            }
                            // TODO Error 내부로 이동
                            is UiState.Unauthorized -> showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                            is UiState.Error -> {
                                showToast(getString(R.string.cannot_get_lesson_category))
                                hideProgress()
                            }
                        }
                    }
            }
        }
    }

    private fun initNavigation() {
        repeatOnStarted {
            viewModel.navigateToRegisterLessonSecond.collect { navigateToRegisterLessonSecond() }
        }
    }

    override fun initViews() = with(binding) {
        focusInputForm(etRegisterLessonName)
        validateInputForm(etRegisterLessonTotalNumber)
        focusSpinnerForm(spnRegisterLessonCategory)
        focusSpinnerForm(spnRegisterLessonSite)
    }

    private fun bindAdapter(arrayAdapter: ArrayAdapter<String>, spinner: Spinner, position: Int) {
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.apply {
            adapter = arrayAdapter
            setSelection(position)
        }
    }

    private fun navigateToRegisterLessonSecond() {
        findNavController().navigate(R.id.action_register_lesson_first_to_register_lesson_second)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun focusSpinnerForm(spinner: Spinner): Unit = with(binding) {
        spinner.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(requireActivity())
                }
                false
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (spinner.selectedItemPosition == 0) {
                        if (spinner == spnRegisterLessonCategory) {
                            viewModel.setLessonCategoryItemPosition(spnRegisterLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
                        }
                    } else {
                        if (spinner == spnRegisterLessonCategory) {
                            viewModel.setCategoryId(
                                viewModel.lessonCategoryMap.value.filterValues
                                { it == spnRegisterLessonCategory.selectedItem }.keys.first()
                            )
                            viewModel.setCategoryName(spinner.selectedItem.toString())
                            viewModel.setLessonCategoryItemPosition(spnRegisterLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setSiteId(
                                viewModel.lessonSiteMap.value.filterValues
                                { it == spnRegisterLessonSite.selectedItem }.keys.first()
                            )
                            viewModel.setSiteName(spinner.selectedItem.toString())
                            viewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
                        }
                    }
                    clearFocus(etRegisterLessonTotalNumber)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun focusInputForm(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                viewModel.setLessonName(text.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        var result = DEFAULT_STRING_VALUE

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    viewModel.setLessonTotalNumber(charSequence.toString().toInt())
                    result = viewModel.lessonTotalNumber.value!!.toString()
                    if (result[0] == '0') {
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                    } else {
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                    }
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvRegisterLessonTotalNumberInfo.apply {
                        text = getString(R.string.input_lesson_count, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = DEFAULT_STRING_VALUE
                    editText.setText(result)
                    tvRegisterLessonTotalNumberInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun clearFocus(editText: EditText) = with(binding) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
    }
}