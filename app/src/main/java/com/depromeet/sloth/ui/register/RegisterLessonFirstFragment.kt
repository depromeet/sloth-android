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
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO 진입시 categoryList, siteList 두 api 중 하나라도 request 요청 실패할 경우 인터넷 연결 실패 화면 visible
@AndroidEntryPoint
class RegisterLessonFirstFragment :
    BaseFragment<FragmentRegisterLessonFirstBinding>(R.layout.fragment_register_lesson_first) {

    private val registerLessonViewModel: RegisterLessonViewModel by hiltNavGraphViewModels(R.id.nav_register_lesson)

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            registerLessonViewModel.lessonCategoryList.value
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            registerLessonViewModel.lessonSiteList.value
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = registerLessonViewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tbLayout.tbRegisterLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {

            launch {
                fetchLessonCategoryListSuccess
                    .collect {
                        bindAdapter(
                            lessonCategoryAdapter,
                            binding.spnRegisterLessonCategory,
                            lessonCategorySelectedItemPosition.value
                        )
                    }
            }

            launch {
                fetchLessonSiteListSuccess
                    .collect {
                        bindAdapter(
                            lessonSiteAdapter,
                            binding.spnRegisterLessonSite,
                            lessonSiteSelectedItemPosition.value
                        )
                    }
            }

            launch {
                navigateToRegisterLessonSecondEvent
                    .collect {
                        val action =
                            RegisterLessonFirstFragmentDirections.actionRegisterLessonFirstToRegisterLessonSecond()
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                showForbiddenDialogEvent
                    .collect {
                        showForbiddenDialog(
                            requireContext(),
                            this@RegisterLessonFirstFragment
                        ) { deleteAuthToken() }
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        showToast(requireContext(), message)
                    }
            }
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
                    when (spinner.selectedItemPosition) {
                        0 -> {
                            when (spinner) {
                                spnRegisterLessonCategory -> {
                                    registerLessonViewModel.setLessonCategorySelectedItemPosition(
                                        spnRegisterLessonCategory.selectedItemPosition
                                    )
                                }

                                else -> {
                                    registerLessonViewModel.setLessonSiteItemPosition(
                                        spnRegisterLessonSite.selectedItemPosition
                                    )
                                }
                            }
                        }

                        else -> {
                            when (spinner) {
                                spnRegisterLessonCategory -> {
                                    registerLessonViewModel.setLessonCategoryId(
                                        registerLessonViewModel.lessonCategoryMap.value.filterValues
                                        { it == spnRegisterLessonCategory.selectedItem }.keys.first()
                                    )
                                    registerLessonViewModel.setLessonCategoryName(spinner.selectedItem.toString())
                                    registerLessonViewModel.setLessonCategorySelectedItemPosition(
                                        spnRegisterLessonCategory.selectedItemPosition
                                    )
                                }

                                else -> {
                                    registerLessonViewModel.setLessonSiteId(
                                        registerLessonViewModel.lessonSiteMap.value.filterValues
                                        { it == spnRegisterLessonSite.selectedItem }.keys.first()
                                    )
                                    registerLessonViewModel.setLessonSiteName(spinner.selectedItem.toString())
                                    registerLessonViewModel.setLessonSiteItemPosition(
                                        spnRegisterLessonSite.selectedItemPosition
                                    )
                                }
                            }
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
                registerLessonViewModel.setLessonName(text.toString())
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
                    registerLessonViewModel.setLessonTotalNumber(charSequence.toString().toInt())
                    result = registerLessonViewModel.lessonTotalNumber.value.toString()
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