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
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.UiState
import com.depromeet.sloth.util.DECIMAL_FORMAT_PATTERN
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat


@AndroidEntryPoint
class UpdateLessonActivity :
    BaseActivity<ActivityUpdateLessonBinding>(R.layout.activity_update_lesson) {

    private val viewModel: UpdateLessonViewModel by viewModels()

    private val lessonCategoryAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            viewModel.lessonCategoryList
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            viewModel.lessonSiteList
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            lessonDetail = viewModel.lessonDetail
            vm = viewModel
        }

        initObserver()
        initNavigation()
        initViews()
    }

    private fun initObserver() {
        viewModel.apply {
            repeatOnStarted {
                updateLessonState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading ->
                            showProgress(this@UpdateLessonActivity)

                        is UiState.Success<LessonUpdateResponse> -> {
                            Timber.tag("Update Success").d("${uiState.data}")
                            showToast(getString(R.string.lesson_info_update_complete))
                            finish()
                        }

                        is UiState.Unauthorized -> {
                            showLogoutDialog(this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                        }

                        is UiState.Error -> {
                            Timber.tag("fetch Error").d(uiState.throwable)
                            showToast(getString(R.string.lesson_info_update_fail))
                        }

                        else -> {}
                    }
                    hideProgress()
                }
            }

            lessonCategoryListState.observe(this@UpdateLessonActivity) { uiState ->
                when (uiState) {
                    is UiState.Loading -> showProgress(this@UpdateLessonActivity)

                    is UiState.Success<List<LessonCategory>> -> {
                        viewModel.setLessonCategoryList(uiState.data)
//                        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        binding.spnUpdateLessonCategory.apply {
//                            adapter = lessonCategoryAdapter
//                            setSelection(viewModel.lessonCategorySelectedItemPosition.value!!)
//                        }
                    }

                    is UiState.Unauthorized -> {
                        showLogoutDialog(this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is UiState.Error -> {
                        Timber.tag("fetch Error").d(uiState.throwable)
                        showToast(getString(R.string.lesson_category_fetch_fail))
                    }

                    else -> Unit
                }
                hideProgress()
            }

            lessonSiteListState.observe(this@UpdateLessonActivity) { uiState ->
                when (uiState) {
                    is UiState.Loading -> showProgress(this@UpdateLessonActivity)

                    is UiState.Success<List<LessonSite>> -> {
                        viewModel.setLessonSiteList(uiState.data)
//                        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        binding.spnUpdateLessonSite.apply {
//                            adapter = lessonSiteAdapter
//                            setSelection(viewModel.lessonSiteSelectedItemPosition.value!!)
//                        }

                        viewModel.setLessonUpdateInfo()
                        bindAdapter()
                    }

                    is UiState.Unauthorized -> {
                        showLogoutDialog(
                            this@UpdateLessonActivity
                        ) { viewModel.removeAuthToken() }
                    }

                    is UiState.Error -> {
                        Timber.tag("fetch Error").d(uiState.throwable)
                        showToast(getString(R.string.lesson_site_fetch_fail))
                    }

                    else -> Unit
                }
                hideProgress()
            }

            lessonTotalNumberValidation.observe(this@UpdateLessonActivity) { isEnable ->
                when (isEnable) {
                    false -> {
                        showToast(getString(R.string.lesson_number_validation_error))
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initNavigation() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }
    }

    override fun initViews() = with(binding) {
        focusInputForm(etUpdateLessonName)
        validateCountInputForm(etUpdateLessonCount)
        focusSpinnerForm(spnUpdateLessonCategory)
        focusSpinnerForm(spnUpdateLessonSite)
        validatePriceInputForm(etUpdateLessonPrice)
    }

    private fun bindAdapter() = with(binding) {
        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnUpdateLessonCategory.adapter = lessonCategoryAdapter
        spnUpdateLessonCategory.setSelection(
            viewModel.lessonCategorySelectedItemPosition.value!!
        )

        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnUpdateLessonSite.adapter = lessonSiteAdapter
        spnUpdateLessonSite.setSelection(
            viewModel.lessonSiteSelectedItemPosition.value!!
        )
    }

    // LessonName clearFocus 가 되지 않는 문제
    private fun focusInputForm(editText: EditText) {
        editText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                }

                override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                    viewModel.setLessonName(text.toString())
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
            var result = ""

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    i1: Int,
                    i2: Int,
                    i3: Int,
                ) {}

                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                    if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                        viewModel.setLessonTotalNumber(charSequence.toString().toInt())
                        result = viewModel.lessonTotalNumber.value!!.toString()
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
                        result = ""
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
            var result = ""
            val decimalFormat = DecimalFormat(DECIMAL_FORMAT_PATTERN)

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    i1: Int,
                    i2: Int,
                    i3: Int,
                ) {}

                override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                    if (!TextUtils.isEmpty(charSequence!!.toString()) && charSequence.toString() != result) {
                        viewModel.setLessonPrice(charSequence.toString().replace(",", "").toInt())
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
                        result = ""
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
                            viewModel.setLessonCategoryItemPosition(spnUpdateLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setLessonSiteItemPosition(spnUpdateLessonSite.selectedItemPosition)
                        }
                    } else {
                        if (spinner == spnUpdateLessonCategory) {
                            viewModel.setLessonCategoryId(
                                viewModel.lessonCategoryMap.filterValues
                                { it == spnUpdateLessonCategory.selectedItem }.keys.first()
                            )
                            viewModel.setLessonCategoryName(spinner.selectedItem.toString())
                            viewModel.setLessonCategoryItemPosition(spnUpdateLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setLessonSiteId(
                                viewModel.lessonSiteMap.filterValues
                                { it == spnUpdateLessonSite.selectedItem }.keys.first()
                            )
                            viewModel.setLessonSiteName(spinner.selectedItem.toString())
                            viewModel.setLessonSiteItemPosition(spnUpdateLessonSite.selectedItemPosition)
                        }
                    }
                    clUpdateLesson.clearFocus()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}