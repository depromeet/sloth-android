package com.depromeet.sloth.ui.update

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonUpdateInfoRequest
import com.depromeet.sloth.data.network.lesson.LessonUpdateState
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.ui.base.BaseActivity
import java.text.DecimalFormat

class UpdateLessonActivity : BaseActivity<UpdateLessonViewModel, ActivityUpdateLessonBinding>() {

    override val viewModel: UpdateLessonViewModel = UpdateLessonViewModel()

    override fun getViewBinding(): ActivityUpdateLessonBinding
        = ActivityUpdateLessonBinding.inflate(layoutInflater)

    private val pm = PreferenceManager(this)

    lateinit var accessToken: String
    lateinit var refreshToken: String

    //lateinit var siteArraySize: Number

    lateinit var lesson: LessonRegisterRequest
    lateinit var lessonId: String
    lateinit var startDate: ArrayList<String>
    lateinit var endDate: ArrayList<String>

    companion object {
        fun newIntent(activity: Activity,lessonId: String, lesson: LessonRegisterRequest) = Intent(activity, UpdateLessonActivity::class.java).apply {
            putExtra(LESSON_ID, lessonId)
            putExtra(LESSON, lesson)
        }

        private const val LESSON_ID = "lessonId"
        private const val LESSON = "lesson"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accessToken = pm.getAccessToken().toString()
        refreshToken = pm.getRefreshToken().toString()

        intent.apply {
            lessonId = getStringExtra(LESSON_ID).toString()
            lesson = getParcelableExtra(LESSON)!!
        }

        //siteArraySize = resources.getStringArray(R.array.site_array).size - 1

        initViews()
    }

    override fun initViews() = with(binding) {
        tbUpdateLesson.setNavigationOnClickListener { finish() }

        focusInputForm(etUpdateLessonName, btnUpdateLesson)
        focusInputForm(etUpdateLessonCount, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonCategory, btnUpdateLesson)
        focusSpinnerForm(spnUpdateLessonSite, btnUpdateLesson)

        initLessonInfo(lesson)

        btnUpdateLesson.setOnClickListener {
            mainScope {
                val updateLessonRequest = LessonUpdateInfoRequest(
                    //categoryId = spnUpdateLessonCategory.selectedItemPosition + siteArraySize as Int,
                    categoryId = spnUpdateLessonCategory.selectedItemPosition,
                    lessonName = etUpdateLessonName.text.toString(),
                    siteId = spnUpdateLessonSite.selectedItemPosition,
                    totalNumber = etUpdateLessonCount.text.toString().toInt()
                )

                viewModel.updateLesson(accessToken = accessToken, lessonId = lessonId, updateLessonRequest = updateLessonRequest).let {
                    when(it) {
                        is LessonUpdateState.Success -> {
                            Log.d("Update Success", "${it.data}")
                            Toast.makeText(this@UpdateLessonActivity, "강의 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is LessonUpdateState.Unauthorized -> {
                            viewModel.updateLesson(accessToken = refreshToken, lessonId = lessonId, updateLessonRequest = updateLessonRequest).let { updateLessonResponse ->
                                when (updateLessonResponse) {
                                    is LessonUpdateState.Success -> {
                                        Log.d("Update Success", "${updateLessonResponse.data}")
                                        Toast.makeText(this@UpdateLessonActivity, "강의 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }

                                    is LessonUpdateState.Error -> {
                                        Log.d("Delete Error", "${updateLessonResponse.exception}")
                                        Toast.makeText(this@UpdateLessonActivity, "강의 정보가 수정을 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> Unit
                                }
                            }
                        }

                        is LessonUpdateState.Error -> {
                            Log.d("Update Error", "${it.exception}")
                            Toast.makeText(this@UpdateLessonActivity, "강의 정보가 수정을 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun initLessonInfo(lesson: LessonRegisterRequest?) = with(binding) {
        etUpdateLessonName.setText(lesson!!.lessonName)
        etUpdateLessonCount.setText(lesson.totalNumber.toString())

        spnUpdateLessonCategory.setSelection(lesson.categoryId)
        spnUpdateLessonSite.setSelection(lesson.siteId)

        tvUpdateStartLessonDate.text = changeDateFormat(lesson.startDate)
        tvUpdateEndLessonDate.text = changeDateFormat(lesson.endDate)

        val df = DecimalFormat("#,###")
        val changedPriceFormat = df.format(lesson.price)

        tvUpdateLessonPriceInfo.text = "${changedPriceFormat}원"
        tvUpdateLessonMessageInfo.text = lesson.message
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

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button)
                } else {
                    unlockButton(button)
                }
            }
        })
    }

    private fun focusSpinnerForm(spinner: Spinner, button: AppCompatButton) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val spinnerId = spinner.selectedItemPosition
                if(spinnerId == 0) {
                    lockButton(button)
                }
                else {
                    unlockButton(button)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                unlockButton(button)
            }
        }
    }

    private fun changeDateFormat(date: String): String {
        Log.d("date", date)

        val dateArr = date.split(",")

        Log.d("dateArr", dateArr.toString())

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