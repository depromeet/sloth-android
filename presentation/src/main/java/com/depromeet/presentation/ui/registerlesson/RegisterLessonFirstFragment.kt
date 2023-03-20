package com.depromeet.presentation.ui.registerlesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.presentation.extensions.hideKeyBoard
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


//TODO view 에서 .value 로 접근하는 부분 수정
@AndroidEntryPoint
class RegisterLessonFirstFragment : BaseFragment<FragmentRegisterLessonFirstBinding>(R.layout.fragment_register_lesson_first) {

    private val viewModel: RegisterLessonViewModel by hiltNavGraphViewModels(R.id.nav_register_lesson)

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonCategoryList.value
            // viewModel.lessonCategoryList
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            viewModel.lessonSiteList.value
            // viewModel.lessonSiteList
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchLessonCategoryList()
        viewModel.fetchLessonSiteList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }

        val args: RegisterLessonFirstFragmentArgs by navArgs()
        if (args.fragmentId == R.id.on_boarding_today_lesson) {
            // 뒤로가기 버튼 없애기
            binding.tbLayout.tbRegisterLesson.navigationIcon = null
            // 온보딩에서 진입 했을 경우에만 시스템 백 버튼 비활성화
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                return@addCallback
            }
        }

        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() = with(binding) {
        focusInputForm(etRegisterLessonName)
        validateInputForm(etRegisterLessonTotalNumber)
        focusSpinnerForm(spnRegisterLessonCategory)
        focusSpinnerForm(spnRegisterLessonSite)
    }

    private fun initListener() {
        binding.tbLayout.tbRegisterLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.fetchLessonCategoryListSuccessEvent.collect {
                    bindAdapter(lessonCategoryAdapter, binding.spnRegisterLessonCategory)
                }
            }

            launch {
                viewModel.fetchLessonSiteListSuccessEvent.collect {
                    bindAdapter(lessonSiteAdapter, binding.spnRegisterLessonSite)
                }
            }

            launch {
                viewModel.navigateToRegisterLessonSecondEvent.collect {
                    val action = RegisterLessonFirstFragmentDirections.actionRegisterLessonFirstToRegisterLessonSecond()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.navigateToExpireDialogEvent.collect {
                    showExpireDialog()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindAdapter(arrayAdapter: ArrayAdapter<String>, spinner: Spinner) {
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.apply {
            adapter = arrayAdapter
            setSelection(0)
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
                            if (spinner == spnRegisterLessonCategory) {
                                viewModel.setLessonCategorySelectedItemPosition(
                                    spnRegisterLessonCategory.selectedItemPosition
                                )
                            } else {
                                viewModel.setLessonSiteItemPosition(
                                    spnRegisterLessonSite.selectedItemPosition
                                )
                            }
                        }

                        else -> {
                            if (spinner == spnRegisterLessonCategory) {
                                viewModel.setLessonCategoryId(spnRegisterLessonCategory.selectedItem.toString())
                                viewModel.setLessonCategoryName(spinner.selectedItem.toString())
                                viewModel.setLessonCategorySelectedItemPosition(
                                    spnRegisterLessonCategory.selectedItemPosition
                                )
                            } else {
                                viewModel.setLessonSiteId(spnRegisterLessonSite.selectedItem.toString())
                                viewModel.setLessonSiteName(spinner.selectedItem.toString())
                                viewModel.setLessonSiteItemPosition(
                                    spnRegisterLessonSite.selectedItemPosition
                                )
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
                viewModel.setLessonName(text.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
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
                    result = viewModel.lessonTotalNumber.value.toString()
                    if (result[0] == '0') {
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_error)
                    } else {
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
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
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }
    }

    private fun clearFocus(editText: EditText) = with(binding) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
        }
        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
    }
}