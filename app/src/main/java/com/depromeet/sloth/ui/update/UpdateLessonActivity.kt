package com.depromeet.sloth.ui.update

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.*
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class UpdateLessonActivity : BaseActivity<ActivityUpdateLessonBinding>() {
    companion object {
        fun newIntent(activity: Activity, lessonId: String, lesson: LessonRegisterRequest) =
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

    lateinit var accessToken: String
    lateinit var refreshToken: String

    lateinit var lesson: LessonRegisterRequest
    lateinit var lessonId: String

    lateinit var lessonCategoryMap: HashMap<Int, String>
    private var lessonCategoryList = mutableListOf<String>()

    lateinit var lessonSiteMap: HashMap<Int, String>
    private var lessonSiteList = mutableListOf<String>()

    lateinit var lessonCount: Number
    lateinit var lessonPrice: Number

    lateinit var categoryAdapter: ArrayAdapter<String>
    lateinit var siteAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
            lesson = getParcelableExtra(LESSON)!!
        }
    }

    override fun onResume() {
        super.onResume()

        mainScope {
            initLessonCategory()
            initLessonSite()
        }
    }

    private suspend fun initLessonCategory() {
        viewModel.fetchLessonCategoryList(accessToken = accessToken).let {
            when (it) {
                is LessonState.Success<List<LessonCategoryResponse>> -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonCategoryList(it.data)
                }
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }

                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@UpdateLessonActivity))
                            }
                        }
                    dlg.start()
                }
                is LessonState.NotFound -> {
                    Log.d("Error", "NotFound")
                }
                is LessonState.Forbidden -> {
                    Log.d("Error", "Forbidden")
                }
            }
        }
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        lessonCategoryMap =
            data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>

        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "인강 카테고리를 선택해 주세요")
    }

    private suspend fun initLessonSite() {
        viewModel.fetchLessonSiteList(accessToken = accessToken).let {
            when (it) {
                is LessonState.Success -> {
                    Log.d("fetch Success", "${it.data}")
                    setLessonSiteList(it.data)

                    initViews()
                }
                is LessonState.Error -> {
                    Log.d("fetch Error", "${it.exception}")
                }

                is LessonState.Unauthorized -> {
                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                    dlg.onItemClickListener =
                        object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(this@UpdateLessonActivity))
                            }
                        }
                    dlg.start()
                }
                is LessonState.NotFound -> {
                    Log.d("Error", "NotFound")
                }
                is LessonState.Forbidden -> {
                    Log.d("Error", "Forbidden")
                }
            }
        }
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        lessonSiteMap = data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>

        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택 해주세요")
    }

    override fun initViews() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }

        initSpinner()

        focusInputForm(etUpdateLessonName, btnUpdateLesson)
        validateCountInputForm(etUpdateLessonCount, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonCategory, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonSite, btnUpdateLesson)
        validatePriceInputForm(etUpdateLessonPrice, btnUpdateLesson)

        initLessonInfo(lesson)

        btnUpdateLesson.setOnClickListener {
            mainScope {
                val updateLessonRequest = LessonUpdateInfoRequest(
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

                viewModel.updateLesson(
                    accessToken = accessToken,
                    lessonId = lessonId,
                    updateLessonRequest = updateLessonRequest
                ).let {
                    when (it) {
                        is LessonUpdateState.Success -> {
                            Log.d("Update Success", "${it.data}")
                            Toast.makeText(
                                this@UpdateLessonActivity,
                                "강의 정보가 수정되었어요",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        is LessonUpdateState.Unauthorized -> {
                            Log.d("Update Error", "${it.exception}")
                            val dlg = SlothDialog(this@UpdateLessonActivity, DialogState.FORBIDDEN)
                            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                                override fun onItemClicked() {
                                    preferenceManager.removeAuthToken()
                                    startActivity(LoginActivity.newIntent(this@UpdateLessonActivity))
                                }
                            }
                            dlg.start()
                        }
                        is LessonUpdateState.Error -> {
                            Log.d("Update Error", "${it.exception}")
                            Toast.makeText(
                                this@UpdateLessonActivity,
                                "강의 정보 수정을 실패했어요",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is LessonUpdateState.NoContent -> {
                            Log.d("Update Error", "NoContent")
                        }
                        is LessonUpdateState.Forbidden -> {
                            Log.d("Update Error", "Forbidden")
                        }
                    }
                }
            }
        }
    }

    private fun initSpinner() {
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
    private fun initLessonInfo(lesson: LessonRegisterRequest?) = with(binding) {
        etUpdateLessonName.setText(lesson!!.lessonName)

        lessonCount = lesson.totalNumber
        //etUpdateLessonCount.setText(lesson.totalNumber.toString())
        etUpdateLessonCount.hint = "${lessonCount}개"

        spnUpdateLessonCategory.setSelection(
            lessonCategoryList.indexOf(lessonCategoryMap[lesson.categoryId])
        )

        spnUpdateLessonSite.setSelection(
            lessonSiteList.indexOf(lessonSiteMap[lesson.siteId])
        )

        tvUpdateStartLessonDate.text = changeDateFormat(lesson.startDate)
        tvUpdateEndLessonDate.text = changeDateFormat(lesson.endDate)

        val df = DecimalFormat("#,###")

        lessonPrice = lesson.price
        val changedPriceFormat = df.format(lesson.price)

        etUpdateLessonPrice.hint = "${changedPriceFormat}원"
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
                            lockButton(button)
                        } else {
                            tvUpdateLessonCountInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                            unlockButton(button)
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
                        lockButton(button)
                    } else {
                        unlockButton(button)
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
                    setButton(editable, button)
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

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                setButton(editable, button)
            }
        })
        setEditTextFocus(editText)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun focusSpinnerForm(spinner: Spinner, button: AppCompatButton) = with(binding) {
        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyBoard()
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
                    lockButton(button)
                } else {
                    unlockButton(button)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                unlockButton(button)
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    private fun setEditTextFocus(editText: EditText) {
        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
        clearEditTextFocus(editText)
    }

    private fun clearEditTextFocus(editText: EditText) {
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editText.clearFocus()
            }
            false
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

    private fun setButton(editable: Editable?, button: AppCompatButton) {
        if (editable.isNullOrEmpty()) {
            lockButton(button)
        } else {
            unlockButton(button)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: AppCompatButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(
            this,
            R.drawable.bg_update_rounded_sloth
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: AppCompatButton) {
        button.isEnabled = false
        button.background = AppCompatResources.getDrawable(
            this,
            R.drawable.bg_update_rounded_gray
        )
    }

    private fun changeDateFormat(date: String): String {
        val dateArr = date.split(",")

        val yearOfDate = dateArr[0].replace("[", "")
        val monthOfDate = changeDate(dateArr[1])
        val dayOfDate = changeDate(dateArr[2]).replace("]", "")

        return "${yearOfDate}.${monthOfDate}.$dayOfDate"
    }

    private fun changeDate(data: String): String {
        var tmp = data
        if (tmp.length == 1) {
            tmp = "0$tmp"
        }
        return tmp
    }
}