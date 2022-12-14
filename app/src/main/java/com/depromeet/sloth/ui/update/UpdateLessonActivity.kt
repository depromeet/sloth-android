package com.depromeet.sloth.ui.update

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.DECIMAL_FORMAT_PATTERN
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat


@AndroidEntryPoint
class UpdateLessonActivity :
    BaseActivity<ActivityUpdateLessonBinding>(R.layout.activity_update_lesson) {

    private val updateLessonViewModel: UpdateLessonViewModel by viewModels()

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            updateLessonViewModel.lessonCategoryList.value
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            updateLessonViewModel.lessonSiteList.value
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = updateLessonViewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    private fun initObserver() = with(updateLessonViewModel) {
        repeatOnStarted {
            launch {
                updateLessonState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<LessonUpdateResponse> -> {
                                showToast(getString(R.string.lesson_info_update_complete))
                                finish()
                            }
                            is Result.Error -> {
                                when(result.statusCode) {
                                    401 -> showForbiddenDialog(this@UpdateLessonActivity) {
                                        updateLessonViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(getString(R.string.lesson_info_update_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                lessonCategoryListState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                setLessonCategoryInfo(result.data)
                                bindLessonCategoryAdapter()
                            }
                            is Result.Error -> {
                                when(result.statusCode) {
                                    401 ->  showForbiddenDialog(this@UpdateLessonActivity) {
                                        updateLessonViewModel.removeAuthToken()
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
                lessonSiteListState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                //TODO UDF 에 위반 코드 개선
                                setLessonSiteInfo(result.data)
                                bindLessonSiteAdapter()
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(this@UpdateLessonActivity) {
                                        updateLessonViewModel.removeAuthToken()
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
                lessonTotalNumberValidation
                    .collect { isEnable ->
                        when (isEnable) {
                            false -> showToast(getString(R.string.lesson_number_validation_error))
                            else -> Unit
                        }
                    }
            }
        }
    }

    private fun initListener() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }
    }

    override fun initViews() = with(binding) {
        focusInputForm(etUpdateLessonName)
        validateCountInputForm(etUpdateLessonCount)
        focusSpinnerForm(spnUpdateLessonCategory)
        focusSpinnerForm(spnUpdateLessonSite)
        validatePriceInputForm(etUpdateLessonPrice)
    }

    private fun bindLessonCategoryAdapter() = with(binding) {
        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnUpdateLessonCategory.adapter = lessonCategoryAdapter
        spnUpdateLessonCategory.setSelection(updateLessonViewModel.lessonCategorySelectedItemPosition.value)

    }

    private fun bindLessonSiteAdapter() = with(binding) {
        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnUpdateLessonSite.adapter = lessonSiteAdapter
        spnUpdateLessonSite.setSelection(updateLessonViewModel.lessonSiteSelectedItemPosition.value)
    }

    // LessonName clearFocus 가 되지 않는 문제
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
                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                } else {
                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
                }
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
                    updateLessonViewModel.setLessonTotalNumber(charSequence.toString().toInt())
                    result = updateLessonViewModel.lessonTotalNumber.value.toString()
                    if (result[0] == '0') {
                        tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                    } else {
                        tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                    }
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvUpdateLessonCountInfo.apply {
                        text = getString(R.string.input_lesson_count, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = DEFAULT_STRING_VALUE
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
        var result = DEFAULT_STRING_VALUE
        val decimalFormat = DecimalFormat(DECIMAL_FORMAT_PATTERN)

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
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                textView.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                textView.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }

        // 이 함수에 with(binding)을 호출하면 initViews 함수에 에러 발생 - 이유 확인
        clearEditTextFocus(editText)
        binding.clUpdateLesson.clearFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun focusSpinnerForm(spinner: Spinner): Unit = with(binding) {
        spinner.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(this@UpdateLessonActivity)
                }
                false
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (spinner.selectedItemPosition == 0) {
                        if (spinner == spnUpdateLessonCategory) {
                            updateLessonViewModel.setLessonCategorySelectedItemPosition(
                                spnUpdateLessonCategory.selectedItemPosition
                            )
                        } else {
                            updateLessonViewModel.setLessonSiteSelectedItemPosition(
                                spnUpdateLessonSite.selectedItemPosition
                            )
                        }
                    } else {
                        if (spinner == spnUpdateLessonCategory) {
                            updateLessonViewModel.setLessonCategoryId(
                                updateLessonViewModel.lessonCategoryMap.value.filterValues
                                { it == spnUpdateLessonCategory.selectedItem }.keys.first()
                            )
                            updateLessonViewModel.setLessonCategorySelectedItemPosition(
                                spnUpdateLessonCategory.selectedItemPosition
                            )
                        } else {
                            updateLessonViewModel.setLessonSiteId(
                                updateLessonViewModel.lessonSiteMap.value.filterValues
                                { it == spnUpdateLessonSite.selectedItem }.keys.first()
                            )
                            updateLessonViewModel.setLessonSiteSelectedItemPosition(
                                spnUpdateLessonSite.selectedItemPosition
                            )
                        }
                    }
                    clUpdateLesson.clearFocus()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}