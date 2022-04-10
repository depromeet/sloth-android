package com.depromeet.sloth.ui.update

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.Lesson
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class UpdateLessonActivity : BaseActivity<ActivityUpdateLessonBinding>() {
    companion object {
        fun newIntent(activity: Activity, lessonId: String, lesson: Lesson) =
            Intent(activity, UpdateLessonActivity::class.java).apply {
                putExtra(LESSON_ID, lessonId)
                putExtra(LESSON, lesson)
            }

        private const val LESSON_ID = "lessonId"
        private const val LESSON = "lesson"
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: UpdateLessonViewModel by viewModels()

    override fun getActivityBinding(): ActivityUpdateLessonBinding =
        ActivityUpdateLessonBinding.inflate(layoutInflater)

    lateinit var lesson: Lesson
    lateinit var lessonId: String

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
            lessonId = getStringExtra(LESSON_ID).toString()
            lesson = getParcelableExtra(LESSON)!!
        }

        viewModel.apply {
            lessonUpdateState.observe(this@UpdateLessonActivity) { lessonUpdateState ->
                when(lessonUpdateState) {
                    is LessonUpdateState.Loading ->
                        handleLoadingState(this@UpdateLessonActivity)

                    is LessonUpdateState.Success<LessonUpdateResponse> -> {
                        //handleSuccessState(lessonUpdateState.data)
                        Log.d("Update Success", "${lessonUpdateState.data}")
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의 정보가 수정되었어요",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    is LessonUpdateState.Unauthorized ->
                        showLogoutDialog()

                    is LessonUpdateState.NoContent, LessonUpdateState.Forbidden ->
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의를 수정하지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()

                    is LessonUpdateState.Error -> {
                        Log.d("fetch Error", "${lessonUpdateState.exception}")
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의를 수정하지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                hideProgress()
            }

            lessonCategoryListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonCategoryResponse>> -> {
                        Log.d("lessonCategoryState", "LessonState.Success 호출")
                        //handleSuccessState(lessonState.data)
                        setLessonCategoryList(lessonState.data)
                    }

                    is LessonState.Unauthorized -> showLogoutDialog()

                    is LessonState.Forbidden, LessonState.NotFound ->
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의 카테고리를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()

                    is LessonState.Error -> {
                        Log.d("fetch Error", "${lessonState.exception}")
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의 카테고리를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                hideProgress()
            }

            lessonSiteListState.observe(this@UpdateLessonActivity) { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(this@UpdateLessonActivity)

                    is LessonState.Success<List<LessonSiteResponse>> -> {
                        Log.d("lessonSiteState", "LessonState.Success 호출")
                        //handleSuccessState(lessonState.data)
                        setLessonSiteList(lessonState.data)

                        initViews()
                    }

                    is LessonState.Unauthorized -> showLogoutDialog()

                    is LessonState.Forbidden, LessonState.NotFound ->
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의 카테고리를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()

                    is LessonState.Error -> {
                        Log.d("fetch Error", "${lessonState.exception}")
                        Toast.makeText(this@UpdateLessonActivity,
                            "강의 카테고리를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                hideProgress()
            }
        }
    }

//    private fun <T> handleSuccessState(data: T) {
//        if (data is LessonUpdateResponse) {
//            Log.d("Update Success", "$data")
//            Toast.makeText(this@UpdateLessonActivity, "강의 정보가 수정되었어요", Toast.LENGTH_SHORT).show()
//            finish()
//        } else if (data is List<LessonCategoryResponse>) {
//            Log.d("UpdateLessonActivity", "LessonCategoryResponse: $data")
//            setLessonCategoryList(data)
//        } else if (data is List<LessonSiteResponse>) {
//            Log.d("UpdateLessonActivity", "LessonSiteResponse: $data")
//            setLessonSiteList(data)
//        }
//    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(this, DialogState.FORBIDDEN)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                logout()
            }
        }
        dlg.start()
    }

    private fun logout() {
        viewModel.removeAuthToken()
        Toast.makeText(this, "로그아웃 되었어요", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(this))
    }

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

        setLessonInfo(lesson)

        btnUpdateLesson.setOnClickListener {
            viewModel.updateLesson(
                lessonId,
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

    @SuppressLint("SetTextI18n")
    private fun setLessonInfo(lesson: Lesson) = with(binding) {
        //강의 이름
        etUpdateLessonName.setText(lesson.lessonName)

        //강의 개수
        lessonCount = lesson.totalNumber
        //etUpdateLessonCount.setText(lesson.totalNumber.toString())
        etUpdateLessonCount.hint = "${lessonCount}개"

        bindAdapter()

        val categoryId = lessonCategoryMap.filterValues { it == lesson.categoryName }.keys.first()

        //강의 카테고리
        spnUpdateLessonCategory.setSelection(
            //lessonCategoryList.indexOf(lessonCategoryMap[lesson.categoryId])
            lessonCategoryList.indexOf(lessonCategoryMap[categoryId])
        )

        val siteId = lessonSiteMap.filterValues { it == lesson.siteName }.keys.first()

        //강의 사이트
        spnUpdateLessonSite.setSelection(
           // lessonSiteList.indexOf(lessonSiteMap[lesson.siteId])
            lessonSiteList.indexOf(lessonSiteMap[siteId])
        )

        //강의 시작일
        tvUpdateStartLessonDate.text = changeDateFormatArrayToDot(lesson.startDate)

        //강의 완료일
        tvUpdateEndLessonDate.text = changeDateFormatArrayToDot(lesson.endDate)

        //강의 금액
        lessonPrice = lesson.price
        etUpdateLessonPrice.hint = changeDecimalFormat(lessonPrice as Int)

        //각오 한마디
        tvUpdateLessonMessageInfo.text = lesson.message
    }

    private fun validateCountInputForm(editText: EditText, button: AppCompatButton) =
        with(binding) {
            var result = ""

            editText.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.M)
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

                @RequiresApi(Build.VERSION_CODES.M)
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
                @RequiresApi(Build.VERSION_CODES.M)
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

                @RequiresApi(Build.VERSION_CODES.M)
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