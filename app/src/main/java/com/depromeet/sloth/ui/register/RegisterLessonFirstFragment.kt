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
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.DEFAULT_STRING_VALUE
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegisterLessonFirstFragment :
    BaseFragment<FragmentRegisterLessonFirstBinding>(R.layout.fragment_register_lesson_first) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonCategoryList.value!!
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonSiteList.value!!
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
            lessonCategoryListState.observe(viewLifecycleOwner) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(requireContext())

                    is LessonState.Success<List<LessonCategory>> -> {
                        viewModel.setLessonCategoryList(lessonState.data)
                        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spnRegisterLessonCategory.apply {
                            adapter = lessonCategoryAdapter
                            setSelection(viewModel.lessonCategorySelectedItemPosition.value!!)
                        }
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast(getString(R.string.cannot_get_lesson_category))
                    }
                    else -> Unit
                }
                hideProgress()
            }

            lessonSiteListState.observe(viewLifecycleOwner) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(requireContext())

                    is LessonState.Success<List<LessonSite>> -> {
                        viewModel.setLessonSiteList(lessonState.data)
                        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spnRegisterLessonSite.apply {
                            adapter = lessonSiteAdapter
                            setSelection(viewModel.lessonSiteSelectedItemPosition.value!!)
                        }
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast(getString(R.string.cannot_get_lesson_site))
                    }
                    else -> Unit
                }
                hideProgress()
            }

            isEnabledMoveLessonSecondButton.observe(viewLifecycleOwner) { isEnable ->
                when (isEnable) {
                    false -> {
                        lockButton(binding.btnRegisterLesson, requireContext())
                    }

                    true -> {
                        unlockButton(binding.btnRegisterLesson, requireContext())
                    }
                }
            }
        }
    }

    private fun initNavigation() {
        viewModel.moveRegisterLessonSecondEvent.observe(viewLifecycleOwner, EventObserver {
            moveRegisterLessonSecond()
        })
    }

    override fun initViews() = with(binding) {
        focusInputForm(etRegisterLessonName)
        validateInputForm(etRegisterLessonTotalNumber)
        focusSpinnerForm(spnRegisterLessonCategory)
        focusSpinnerForm(spnRegisterLessonSite)
    }

    private fun moveRegisterLessonSecond() {
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
                    clearFocus(etRegisterLessonTotalNumber)
                    clearFocus(etRegisterLessonName)

                    if (spinner.selectedItemPosition == 0) {
                        if (spinner == spnRegisterLessonCategory) {
                            viewModel.setLessonCategoryItemPosition(spnRegisterLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
                        }
                    } else {
                        if (spinner == spnRegisterLessonCategory) {
                            viewModel.setCategoryId(viewModel.lessonCategoryMap.value!!.filterValues
                            { it == spnRegisterLessonCategory.selectedItem }.keys.first()
                            )
                            viewModel.setCategoryName(spinner.selectedItem.toString())
                            viewModel.setLessonCategoryItemPosition(spnRegisterLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setSiteId(viewModel.lessonSiteMap.value!!.filterValues
                            { it == spnRegisterLessonSite.selectedItem }.keys.first()
                            )
                            viewModel.setSiteName(spinner.selectedItem.toString())
                            viewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun focusInputForm(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

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

    //다시 돌아왔을때 "개" 가 보이지 않음
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