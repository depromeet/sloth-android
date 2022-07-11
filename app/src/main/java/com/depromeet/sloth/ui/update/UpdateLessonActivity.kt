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
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.model.LessonUpdate
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
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
            viewModel.lessonCategoryList.value?: listOf()
        )
    }

    private val lessonSiteAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            viewModel.lessonSiteList.value?: listOf()
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
    }

    private fun initObserver() {
        viewModel.apply {
            lessonUpdateState.observe(
                this@UpdateLessonActivity,
                EventObserver { lessonUpdateState ->
                    when (lessonUpdateState) {
                        is LessonState.Loading ->
                            handleLoadingState(this@UpdateLessonActivity)

                        is LessonState.Success<LessonUpdate> -> {
                            Timber.tag("Update Success").d("${lessonUpdateState.data}")
                            showToast("강의 정보가 수정되었어요")
                            finish()
                        }

                        is LessonState.Unauthorized -> {
                            showLogoutDialog(this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                        }

                        is LessonState.Error -> {
                            Timber.tag("fetch Error").d(lessonUpdateState.throwable)
                            showToast("강의를 수정하지 못했어요")
                        }
                    }
                    hideProgress()

                })

            lessonCategoryListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonCategory>> -> {
                        viewModel.setLessonCategoryList(lessonState.data)
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast("강의 카테고리를 가져오지 못했어요")
                    }
                    else -> Unit
                }
                hideProgress()
            }

            lessonSiteListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonSite>> -> {
                        viewModel.setLessonSiteList(lessonState.data)
                        viewModel.setLessonUpdateInfo()
                        initViews()
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(
                            this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast("강의 사이트를 가져오지 못했어요")
                    }
                    else -> Unit
                }
                hideProgress()
            }

            isEnabledLessonUpdateButton.observe(this@UpdateLessonActivity) { isEnable ->
                when (isEnable) {
                    false -> {
                        lockButton(binding.btnUpdateLesson, this@UpdateLessonActivity)
                    }

                    true -> {
                        unlockButton(binding.btnUpdateLesson, this@UpdateLessonActivity)
                    }
                }
            }

            lessonNumberValidation.observe(this@UpdateLessonActivity) { isEnable ->
                when (isEnable) {
                    false -> {
                        showToast("강의 개수가 수강한 강의 개수보다 적어요")
                        lockButton(binding.btnUpdateLesson, this@UpdateLessonActivity)
                    }

                    true -> {
                        unlockButton(binding.btnUpdateLesson, this@UpdateLessonActivity)
                    }
                }
            }
        }
    }

    private fun initNavigation() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }
    }


    override fun initViews() = with(binding) {
        bindAdapter()

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

    //간헐적으로 clearFocus 가 되지 않는 문제
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

    private fun validateCountInputForm(editText: EditText) =
        with(binding) {
            var result = ""

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

    private fun validatePriceInputForm(editText: EditText) =
        with(binding) {
            var result = ""
            val decimalFormat = DecimalFormat("#,###")

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
        clearEditTextFocus(editText)
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
                    clearFocus(etUpdateLessonName)
                    clearFocus(etUpdateLessonCount)
                    clearFocus(etUpdateLessonPrice)

                    if (spinner.selectedItemPosition == 0) {
                        if (spinner == spnUpdateLessonCategory) {
                            viewModel.setLessonCategoryItemPosition(spnUpdateLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setLessonSiteItemPosition(spnUpdateLessonSite.selectedItemPosition)
                        }
                    } else {
                        if (spinner == spnUpdateLessonCategory) {
                            viewModel.setCategoryId(viewModel.lessonCategoryMap.value!!.filterValues
                            { it == spnUpdateLessonCategory.selectedItem }.keys.first())
                            viewModel.setCategoryName(spinner.selectedItem.toString())
                            viewModel.setLessonCategoryItemPosition(spnUpdateLessonCategory.selectedItemPosition)
                        } else {
                            viewModel.setSiteId(viewModel.lessonSiteMap.value!!.filterValues
                            { it == spnUpdateLessonSite.selectedItem }.keys.first())
                            viewModel.setSiteName(spinner.selectedItem.toString())
                            viewModel.setLessonSiteItemPosition(spnUpdateLessonSite.selectedItemPosition)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun clearFocus(editText: EditText) = with(binding) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
        tvUpdateLessonPriceInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
    }
}