package com.depromeet.sloth.ui.update

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class UpdateLessonActivity : BaseActivity<ActivityUpdateLessonBinding>(R.layout.activity_update_lesson) {

    private val viewModel: UpdateLessonViewModel by viewModels()

    lateinit var lessonDetailInfo: LessonDetail

    lateinit var lessonCount: Number
    lateinit var lessonPrice: Number

    private lateinit var lessonCategoryMap: HashMap<Int, String>
    private var lessonCategoryList = mutableListOf<String>()

    private lateinit var lessonSiteMap: HashMap<Int, String>
    private var lessonSiteList = mutableListOf<String>()

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var siteAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.apply {
            lessonDetailInfo = getParcelableExtra("lessonDetail")!!
        }

        viewModel.apply {
            lessonUpdateState.observe(this@UpdateLessonActivity) { lessonUpdateState ->
                when (lessonUpdateState) {
                    is LessonUpdateState.Loading ->
                        handleLoadingState(this@UpdateLessonActivity)

                    is LessonUpdateState.Success<LessonUpdateResponse> -> {
                        //handleSuccessState(lessonUpdateState.data)
                        Log.d("Update Success", "${lessonUpdateState.data}")
                        showToast("강의 정보가 수정되었어요")
                        finish()
                    }

                    is LessonUpdateState.Unauthorized -> {
                        showLogoutDialog(this@UpdateLessonActivity,
                            this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonUpdateState.Error -> {
                        Log.d("fetch Error", "${lessonUpdateState.exception}")
                        showToast("강의를 수정하지 못했어요")
                    }
                }
                hideProgress()
            }

            lessonCategoryListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonCategoryResponse>> -> {
                        //handleSuccessState(lessonState.data)
                        setLessonCategoryList(lessonState.data)
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(this@UpdateLessonActivity,
                            this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Log.d("fetch Error", "${lessonState.exception}")
                        showToast("강의 카테고리를 가져오지 못했어요")
                    }
                }
                hideProgress()
            }

            lessonSiteListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonSiteResponse>> -> {
                        //handleSuccessState(lessonState.data)
                        setLessonSiteList(lessonState.data)

                        initViews()
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(this@UpdateLessonActivity,
                            this@UpdateLessonActivity) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Log.d("fetch Error", "${lessonState.exception}")
                        showToast("강의 사이트를 가져오지 못했어요")
                    }
                }
                hideProgress()
            }
        }

        viewModel.lessonDetail.observe(this@UpdateLessonActivity) { lessonDetail ->
            binding.lessonDetail = lessonDetail
        }
    }

//    private fun <T> handleSuccessState(data: T) {
//        if (data is LessonUpdateResponse) {
//            Log.d("Update Success", "$data")
//            showToast("강의 정보가 수정되었어요")
//            finish()
//        } else if (data is List<LessonCategoryResponse>) {
//            Log.d("UpdateLessonActivity", "LessonCategoryResponse: $data")
//            setLessonCategoryList(data)
//        } else if (data is List<LessonSiteResponse>) {
//            Log.d("UpdateLessonActivity", "LessonSiteResponse: $data")
//            setLessonSiteList(data)
//        }
//    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        lessonCategoryMap =
                //data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
            data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>

        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "인강 카테고리를 선택해 주세요")
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        lessonSiteMap =
                //data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
            data.associate { it.siteId to it.siteName } as HashMap<Int, String>

        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택 해주세요")
    }

    override fun initViews() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }

        focusInputForm(etUpdateLessonName, btnUpdateLesson, this@UpdateLessonActivity)
        validateCountInputForm(etUpdateLessonCount, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonCategory, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonSite, btnUpdateLesson)
        validatePriceInputForm(etUpdateLessonPrice, btnUpdateLesson)

        viewModel.setLessonUpdateInfo(lessonDetailInfo)
        setLessonUpdateInfo(lessonDetailInfo)

        btnUpdateLesson.setOnClickListener {
            if ((lessonCount as Int) == 0) {
                showToast("강의 개수가 올바르지 않아요")
                return@setOnClickListener
            }

            if ((lessonCount as Int) < lessonDetailInfo.presentNumber) {
                showToast("강의 개수가 들은 강의 개수보다 적어요")
                return@setOnClickListener
            }

            if(spnUpdateLessonCategory.selectedItemPosition == 0) {
                showToast("강의 카테고리를 선택해 주세요")
                return@setOnClickListener
            }

            if(spnUpdateLessonSite.selectedItemPosition == 0) {
                showToast("강의 사이트를 선택해 주세요")
                return@setOnClickListener
            }

            updateLesson()
        }
    }

    private fun updateLesson() = with(binding) {
        viewModel.updateLesson(
            lessonDetailInfo.lessonId.toString(),
            LessonUpdateRequest(
                categoryId =
                lessonCategoryMap.filterValues
                { it == lessonCategoryList[spnUpdateLessonCategory.selectedItemPosition] }.keys.first(),
                // == lessonCategoryMap.entries.find {it.value == lessonCategoryList[spnUpdateLessonCategory.selectedItemPosition]}?.key,
                lessonName = etUpdateLessonName.text.toString(),
                price = lessonPrice as Int,
                siteId = lessonSiteMap.filterValues
                { it == lessonSiteList[spnUpdateLessonSite.selectedItemPosition] }.keys.first(),
                // == lessonSiteMap.entries.find {it.value == lessonSiteList[spnUpdateLessonSite.selectedItemPosition]}?.key,
                //totalNumber = etUpdateLessonCount.text.toString().toInt()
                totalNumber = lessonCount.toInt()
            )
        )
    }

    private fun bindAdapter() {
        categoryAdapter = ArrayAdapter<String>(
            this@UpdateLessonActivity,
            R.layout.item_spinner,
            lessonCategoryList
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnUpdateLessonCategory.adapter = categoryAdapter

        siteAdapter = ArrayAdapter<String>(
            this@UpdateLessonActivity,
            R.layout.item_spinner,
            lessonSiteList
        )
        siteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnUpdateLessonSite.adapter = siteAdapter
    }

    private fun setLessonUpdateInfo(lessonDetail: LessonDetail) = with(lessonDetail) {
        lessonCount = totalNumber
        lessonPrice = price

        bindAdapter()
        setSpinner(categoryName, siteName)
    }

    private fun setSpinner(categoryName: String, siteName: String) = with(binding) {
        val categoryId = lessonCategoryMap.filterValues { it == categoryName }.keys.first()

        spnUpdateLessonCategory.setSelection(
            lessonCategoryList.indexOf(lessonCategoryMap[categoryId])
        )

        val siteId = lessonSiteMap.filterValues { it == siteName }.keys.first()

        spnUpdateLessonSite.setSelection(
            lessonSiteList.indexOf(lessonSiteMap[siteId])
        )
    }

    private fun validateCountInputForm(editText: EditText, button: AppCompatButton) =
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
                        lessonCount = charSequence.toString().toInt()
                        result = lessonCount.toString()
                        if (result[0] == '0') {
                            tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                            lockButton(button, this@UpdateLessonActivity)
                        } else {
                            tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                            unlockButton(button, this@UpdateLessonActivity)
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

                override fun afterTextChanged(editable: Editable?) {
                    if (editable.isNullOrEmpty() || editable[0] == '0') {
                        lockButton(button, this@UpdateLessonActivity)
                    } else {
                        unlockButton(button, this@UpdateLessonActivity)
                    }
                }
            })
            setValidateEditTextFocus(editText, tvUpdateLessonCountInfo)
        }

    private fun validatePriceInputForm(editText: EditText, button: AppCompatButton) =
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
                        lessonPrice = charSequence.toString().replace(",", "").toInt()
                        result =
                            decimalFormat.format(charSequence.toString().replace(",", "")
                                .toDouble())
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

                override fun afterTextChanged(editable: Editable?) {
                    setButton(editable, button, this@UpdateLessonActivity)
                }
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
    private fun focusSpinnerForm(spinner: Spinner, button: AppCompatButton) = with(binding) {
        spinner.setOnTouchListener { _, event ->
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

                val spinnerId = spinner.selectedItemPosition
                if (spinnerId == 0) {
                    lockButton(button, this@UpdateLessonActivity)
                } else {
                    unlockButton(button, this@UpdateLessonActivity)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                unlockButton(button, this@UpdateLessonActivity)
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