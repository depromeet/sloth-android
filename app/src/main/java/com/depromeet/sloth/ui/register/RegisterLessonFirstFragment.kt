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
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO 키보드가 올라왔을 때  다음 버튼이 굳이 키보드 위로 올라올 필요가 없음
// 강의 이름 적고 바로 강의 수 텍스트필드 입력칸으로 이동하는게 뭔가 부자연스러움
// 툴바를 include 로 추가했을 때 이슈
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
        binding.tbRegisterLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {
            launch {
                fetchLessonCategoryListEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                registerLessonViewModel.setLessonCategoryList(result.data)
                                bindAdapter(
                                    lessonCategoryAdapter,
                                    binding.spnRegisterLessonCategory,
                                    registerLessonViewModel.lessonCategorySelectedItemPosition.value
                                )
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        registerLessonViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(getString(R.string.cannot_get_lesson_category))
                                    }
                                }

                            }
                        }
                    }
            }

            launch {
                fetchLessonSiteListEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                //TODO UDF 에 위반 -> 코드 개선
                                registerLessonViewModel.setLessonSiteList(result.data)
                                bindAdapter(
                                    lessonSiteAdapter,
                                    binding.spnRegisterLessonSite,
                                    registerLessonViewModel.lessonSiteSelectedItemPosition.value
                                )
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        registerLessonViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(getString(R.string.cannot_get_lesson_category))
                                    }
                                }
                            }
                        }
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
                    if (spinner.selectedItemPosition == 0) {
                        if (spinner == spnRegisterLessonCategory) {
                            registerLessonViewModel.setLessonCategorySelectedItemPosition(
                                spnRegisterLessonCategory.selectedItemPosition
                            )
                        } else {
                            registerLessonViewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
                        }
                    } else {
                        if (spinner == spnRegisterLessonCategory) {
                            registerLessonViewModel.setLessonCategoryId(
                                registerLessonViewModel.lessonCategoryMap.value.filterValues
                                { it == spnRegisterLessonCategory.selectedItem }.keys.first()
                            )
                            registerLessonViewModel.setLessonCategoryName(spinner.selectedItem.toString())
                            registerLessonViewModel.setLessonCategorySelectedItemPosition(
                                spnRegisterLessonCategory.selectedItemPosition
                            )
                        } else {
                            registerLessonViewModel.setLessonSiteId(
                                registerLessonViewModel.lessonSiteMap.value.filterValues
                                { it == spnRegisterLessonSite.selectedItem }.keys.first()
                            )
                            registerLessonViewModel.setLessonSiteName(spinner.selectedItem.toString())
                            registerLessonViewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)
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