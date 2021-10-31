package com.depromeet.sloth.ui

import android.animation.ObjectAnimator
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityRegisterLesson1Binding
import com.depromeet.sloth.ui.base.BaseActivity
import kotlin.math.ceil
import java.util.*


class RegisterLesson1Activity : BaseActivity<RegisterViewModel, ActivityRegisterLesson1Binding>() {

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterLesson1Binding =
        ActivityRegisterLesson1Binding.inflate(layoutInflater)

    private var flag = 0

    private var currentSiteId: Int? = null

    lateinit var categoryId: Number
    lateinit var siteId: Number

    lateinit var siteArraySize: Number

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() = with(binding) {
        tbRegisterLesson.setNavigationOnClickListener { finish() }

        Log.d("currentSiteId", currentSiteId.toString())

        /*val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300*/

        val aniSlide = AnimationUtils.loadAnimation(this@RegisterLesson1Activity, R.anim.slide_down)

        if (flag == 0) {
            lockButton(btnRegisterLesson)

            focusInputForm(etRegisterLessonName, btnRegisterLesson)

            btnRegisterLesson.setOnClickListener {
                startAnimation(aniSlide, tvRegisterLessonCount, etRegisterLessonCount)

                flag += 1

                fillProgressbar(flag, 0)

                hideKeyboard()

                if (flag == 1) {
                    lockButton(btnRegisterLesson)

                    focusInputForm(etRegisterLessonCount, btnRegisterLesson)

                    btnRegisterLesson.setOnClickListener {

                        startAnimation(aniSlide, tvRegisterLessonCategory, spnRegisterLessonCategory)

                        /*첫번째 값을 디폴트 값으로 하여 바로 선택이 되는 것을 막음*/
                        spnRegisterLessonCategory.setSelection(0, false)

                        flag += 1

                        fillProgressbar(flag, 0)

                        hideKeyboard()

                        if (flag == 2) {
                            lockButton(btnRegisterLesson)

                            spnRegisterLessonCategory.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    categoryId = spnRegisterLessonCategory.selectedItemPosition

                                    if (!tvRegisterLessonSite.isVisible) {

                                        startAnimation(aniSlide, tvRegisterLessonSite, spnRegisterLessonSite)
                                    }

                                    /* 강의 사이트 스피너에서 다시 돌아왔을 경우 progressbar 가 계속 차오르는 경우의 대한 예외 처리*/
                                    if (flag < 3) {

                                        flag += 1

                                        fillProgressbar(flag, 0)

                                        hideKeyboard()
                                    }

                                    if (currentSiteId != null) {
                                        spnRegisterLessonSite.setSelection(currentSiteId!!)

                                        if (categoryId != 0 && siteId != 0) {
                                            unlockButton(btnRegisterLesson)
                                        }
                                    } else {
                                        /*첫번째 값을 디폴트 값으로 하여 바로 선택이 되는 것을 막음*/
                                        spnRegisterLessonSite.setSelection(0, false)
                                    }

                                    if (categoryId == 0) {
                                        lockButton(btnRegisterLesson)
                                    } else {
                                        if (flag == 3) {
                                            spnRegisterLessonSite.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        p0: AdapterView<*>?,
                                                        p1: View?,
                                                        p2: Int,
                                                        p3: Long
                                                    ) {
                                                        if (currentSiteId != null) {
                                                            if (currentSiteId != spnRegisterLessonSite.selectedItemPosition) {
                                                                currentSiteId =
                                                                    spnRegisterLessonSite.selectedItemPosition
                                                                siteId = currentSiteId!!.toInt()
                                                                Log.d(
                                                                    "siteId changed to",
                                                                    currentSiteId.toString()
                                                                )
                                                            } else {
                                                                siteId = currentSiteId!!.toInt()
                                                            }
                                                        } else {
                                                            siteId =
                                                                spnRegisterLessonSite.selectedItemPosition
                                                        }
                                                        Log.d("categoryId: ", categoryId.toString())
                                                        Log.d("siteId", siteId.toString())

                                                        if (flag < 4) {

                                                            flag += 1

                                                            fillProgressbar(flag, 0)

                                                            hideKeyboard()
                                                        }
                                                        if (siteId == 0) {
                                                            lockButton(btnRegisterLesson)
                                                        }

                                                        if (categoryId != 0 && siteId != 0) {
                                                            currentSiteId = siteId.toInt()
                                                            unlockButton(btnRegisterLesson)
                                                        }

                                                        btnRegisterLesson.setOnClickListener {
                                                            Log.d(
                                                                "LessonName: ",
                                                                etRegisterLessonName.text.toString()
                                                            )

                                                            Log.d(
                                                                "totalNumber: ",
                                                                //totalNumber.toString()
                                                                etRegisterLessonCount.text.toString()
                                                            )
                                                            Log.d(
                                                                "categoryId: ",
                                                                categoryId.toString()
                                                            )
                                                            Log.d(
                                                                "siteId: ",
                                                                siteId.toString()
                                                            )

                                                            siteArraySize =
                                                                resources.getStringArray(R.array.site_array).size - 1
                                                            Log.d(
                                                                "siteArraySize: ",
                                                                siteArraySize.toString()
                                                            )

                                                            startActivity(
                                                                RegisterLesson2Activity.newIntent(
                                                                    this@RegisterLesson1Activity,
                                                                    etRegisterLessonName.text.toString(),
                                                                    etRegisterLessonCount.text.toString().toInt(),
                                                                    categoryId as Int + siteArraySize as Int,
                                                                    siteId as Int
                                                                )
                                                            )
                                                            overridePendingTransition(
                                                                R.anim.slide_right_enter,
                                                                R.anim.slide_right_exit
                                                            )
                                                        }
                                                    }

                                                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                                                }
                                        }
                                    }

                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {}
                            }
                        }
                    }
                }
                return@setOnClickListener
            }
        }
    }

    private fun fillProgressbar(count: Int, default: Int) {
        val animation = ObjectAnimator.ofInt(
            binding.pbRegisterLesson,
            "progress",
            default + ceil((count - 1) * 12.5).toInt(),
            default + ceil(count * 12.5).toInt()
        )

        animation.duration = 300
        animation.interpolator = LinearInterpolator()
        animation.start()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: Button) {
        button.isEnabled = true
        button.setBackgroundColor(
            resources.getColor(
                R.color.primary_500,
                theme
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(
            resources.getColor(
                R.color.gray_300,
                theme
            )
        )
    }

    private fun focusInputForm(editText: EditText, button: Button) {
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

    private fun startAnimation(animation: Animation, textView: TextView, view: View) {
        textView.visibility = View.VISIBLE
        textView.startAnimation(animation)

        view.visibility = View.VISIBLE
        view.startAnimation(animation)
    }
}