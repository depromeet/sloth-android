package com.depromeet.sloth.presentation.update_lesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class UpdateLessonFragment: BaseFragment<FragmentUpdateLessonBinding>(R.layout.fragment_update_lesson) {

    private val updateLessonViewModel: UpdateLessonViewModel by viewModels()

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            updateLessonViewModel.lessonCategoryList.value
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            updateLessonViewModel.lessonSiteList.value
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = updateLessonViewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() = with(binding) {
        focusInputForm(etUpdateLessonName)
        validateCountInputForm(etUpdateLessonCount)
        focusSpinnerForm(spnUpdateLessonCategory)
        focusSpinnerForm(spnUpdateLessonSite)
        validatePriceInputForm(etUpdateLessonPrice)
    }

    private fun initListener() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(updateLessonViewModel) {
        repeatOnStarted {

            launch {
                updateLessonSuccessEvent
                    .collect {
                        navigateToLessonDetail()
                    }
            }

            launch {
                fetchLessonCategoryListSuccessEvent
                    .collect {
                        bindAdapter(
                            lessonCategoryAdapter,
                            binding.spnUpdateLessonCategory,
                            lessonCategorySelectedItemPosition.value
                        )
                    }
            }

            launch {
                fetchLessonSiteListSuccessEvent
                    .collect {
                        bindAdapter(
                            lessonSiteAdapter,
                            binding.spnUpdateLessonSite,
                            lessonSiteSelectedItemPosition.value
                        )
                    }
            }

            launch {
                lessonTotalNumberValidation
                    .collect { isEnable ->
                        when (isEnable) {
                            false -> showToast(requireContext(), getString(R.string.lesson_number_validation_error))
                            else -> Unit
                        }
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
                navigateToExpireDialog
                    .collect {
                        showExpireDialog(this@UpdateLessonFragment)
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
        editText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    text: CharSequence?,
                    i1: Int,
                    i2: Int,
                    i3: Int
                ) {
                }

                override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                    updateLessonViewModel.setLessonName(text.toString())
                }

                override fun afterTextChanged(editable: Editable?) {}
            })

            setOnFocusChangeListener { _, gainFocus ->
                if (gainFocus) {
                    editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                } else {
                    editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
                }
            }
        }
    }

    private fun validateCountInputForm(editText: EditText) = with(binding) {
        var result = com.depromeet.sloth.util.DEFAULT_STRING_VALUE

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int,
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!android.text.TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    updateLessonViewModel.setLessonTotalNumber(charSequence.toString().toInt())
                    result = updateLessonViewModel.lessonTotalNumber.value.toString()
                    if (result[0] == '0') {
                        tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_error)
                    } else {
                        tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                    }
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvUpdateLessonCountInfo.apply {
                        text = getString(R.string.input_lesson_count, result)
                        visibility = View.VISIBLE
                    }
                }

                if (android.text.TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = com.depromeet.sloth.util.DEFAULT_STRING_VALUE
                    editText.setText(result)
                    tvUpdateLessonCountInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        setValidateEditTextFocus(editText, tvUpdateLessonCountInfo)
    }

    private fun validatePriceInputForm(editText: EditText) = with(binding) {
        var result = com.depromeet.sloth.util.DEFAULT_STRING_VALUE
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
                if (!android.text.TextUtils.isEmpty(charSequence!!.toString()) && charSequence.toString() != result) {
                    updateLessonViewModel.setLessonPrice(
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

                if (android.text.TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = com.depromeet.sloth.util.DEFAULT_STRING_VALUE
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
                textView.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }

        clearEditTextFocus(editText)
        binding.clUpdateLesson.clearFocus()
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
                    when(selectedItemPosition) {
                        0 -> {
                            when(spinner) {
                                spnUpdateLessonCategory -> {
                                    updateLessonViewModel.setLessonCategorySelectedItemPosition(
                                        spnUpdateLessonCategory.selectedItemPosition
                                    )
                                }
                                else -> {
                                    updateLessonViewModel.setLessonSiteSelectedItemPosition(
                                        spnUpdateLessonSite.selectedItemPosition
                                    )
                                }
                            }
                        }
                        else -> {
                            when(spinner) {
                                spnUpdateLessonCategory -> {
                                    updateLessonViewModel.setLessonCategoryId(
                                        updateLessonViewModel.lessonCategoryMap.value.filterValues
                                        { it == spnUpdateLessonCategory.selectedItem }.keys.first()
                                    )
                                    updateLessonViewModel.setLessonCategorySelectedItemPosition(
                                        spnUpdateLessonCategory.selectedItemPosition
                                    )
                                }
                                else -> {
                                    updateLessonViewModel.setLessonSiteId(
                                        updateLessonViewModel.lessonSiteMap.value.filterValues
                                        { it == spnUpdateLessonSite.selectedItem }.keys.first()
                                    )
                                    updateLessonViewModel.setLessonSiteSelectedItemPosition(
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