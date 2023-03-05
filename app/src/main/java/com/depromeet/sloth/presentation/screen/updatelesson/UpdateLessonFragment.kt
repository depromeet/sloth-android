package com.depromeet.sloth.presentation.screen.updatelesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

//TODO view 에서 .value 로 접근하고 있음
@AndroidEntryPoint
class UpdateLessonFragment :
    BaseFragment<FragmentUpdateLessonBinding>(R.layout.fragment_update_lesson) {

    private val viewModel: UpdateLessonViewModel by viewModels()

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
        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() = with(binding) {
        focusInputForm(etUpdateLessonName)
        validateCountInputForm(etUpdateLessonTotalNumber)
        focusSpinnerForm(spnUpdateLessonCategory)
        focusSpinnerForm(spnUpdateLessonSite)
        validatePriceInputForm(etUpdateLessonPrice)
    }

    private fun initListener() {
        binding.tbUpdateLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.updateLessonSuccessEvent.collect {
                    navigateToLessonDetail()
                }
            }

            launch {
                viewModel.fetchLessonCategoryListSuccessEvent.collect {
                    bindAdapter(
                        lessonCategoryAdapter,
                        binding.spnUpdateLessonCategory,
                        viewModel.lessonCategorySelectedItemPosition.value
                    )
                }
            }

            launch {
                viewModel.fetchLessonSiteListSuccessEvent.collect {
                    bindAdapter(
                        lessonSiteAdapter,
                        binding.spnUpdateLessonSite,
                        viewModel.lessonSiteSelectedItemPosition.value
                    )
                }
            }

            launch {
                viewModel.lessonTotalNumberValidation.collect { isEnable ->
                    when (isEnable) {
                        false -> {
                            Toast.makeText(
                                requireContext(), getString(R.string.lesson_number_validation_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
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

    private fun navigateToLessonDetail() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    private fun bindAdapter(arrayAdapter: ArrayAdapter<String>, spinner: Spinner, position: Int) {
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.apply {
            adapter = arrayAdapter
            setSelection(position)
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
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }
    }


    private fun validateCountInputForm(editText: EditText) = with(binding) {
        var result = DEFAULT_STRING_VALUE

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int,
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    viewModel.setLessonTotalNumber(charSequence.toString().toInt())
                    result = viewModel.lessonTotalNumber.value.toString()
                    if (result[0] == '0') {
                        tvUpdateLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_error)
                    } else {
                        tvUpdateLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                    }
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvUpdateLessonTotalNumberInfo.apply {
                        text = getString(R.string.input_lesson_count, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = DEFAULT_STRING_VALUE
                    editText.setText(result)
                    tvUpdateLessonTotalNumberInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        setValidateEditTextFocus(editText, tvUpdateLessonTotalNumberInfo)
    }

    private fun validatePriceInputForm(editText: EditText) = with(binding) {
        var result = DEFAULT_STRING_VALUE
        val decimalFormat = DecimalFormat(com.depromeet.sloth.util.DECIMAL_FORMAT_PATTERN)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int,
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence!!.toString()) && charSequence.toString() != result) {
                    viewModel.setLessonPrice(
                        charSequence.toString().replace(",", "").toInt()
                    )
                    result =
                        decimalFormat.format(
                            charSequence.toString().replace(",", "")
                                .toDouble()
                        )
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvUpdateLessonPriceInfo.apply {
                        text = getString(R.string.input_lesson_price, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = DEFAULT_STRING_VALUE
                    editText.setText(result)
                    tvUpdateLessonPriceInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        setValidateEditTextFocus(editText, tvUpdateLessonPriceInfo)
    }

    private fun setValidateEditTextFocus(editText: EditText, textView: TextView) {
        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                textView.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
                textView.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }

        clearEditTextFocus(requireActivity(), editText)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun focusSpinnerForm(spinner: Spinner): Unit = with(binding) {
        spinner.apply {
            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(requireActivity())
                }
                false
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (selectedItemPosition) {
                        0 -> {
                            when (spinner) {
                                spnUpdateLessonCategory -> {
                                    viewModel.setLessonCategorySelectedItemPosition(
                                        spnUpdateLessonCategory.selectedItemPosition
                                    )
                                }

                                else -> {
                                    viewModel.setLessonSiteSelectedItemPosition(
                                        spnUpdateLessonSite.selectedItemPosition
                                    )
                                }
                            }
                        }

                        else -> {
                            when (spinner) {
                                spnUpdateLessonCategory -> {
                                    viewModel.setLessonCategoryId(
                                        viewModel.lessonCategoryMap.value.filterValues
                                        { it == spnUpdateLessonCategory.selectedItem }.keys.first()
                                    )
                                    viewModel.setLessonCategorySelectedItemPosition(
                                        spnUpdateLessonCategory.selectedItemPosition
                                    )
                                }

                                else -> {
                                    viewModel.setLessonSiteId(
                                        viewModel.lessonSiteMap.value.filterValues
                                        { it == spnUpdateLessonSite.selectedItem }.keys.first()
                                    )
                                    viewModel.setLessonSiteSelectedItemPosition(
                                        spnUpdateLessonSite.selectedItemPosition
                                    )
                                }
                            }
                        }
                    }
                    clUpdateLesson.clearFocus()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}