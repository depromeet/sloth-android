//package com.depromeet.sloth.ui.register
//
//import android.animation.ObjectAnimator
//import android.app.Activity
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.View
//import android.view.animation.Animation
//import android.view.animation.AnimationUtils
//import android.view.animation.LinearInterpolator
//import android.view.inputmethod.InputMethodManager
//import android.widget.*
//import androidx.activity.result.ActivityResultLauncher
//import androidx.annotation.RequiresApi
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.appcompat.widget.AppCompatButton
//import androidx.core.view.isVisible
//import com.depromeet.sloth.R
//import com.depromeet.sloth.databinding.ActivityRegisterLessonFirstBinding
//import com.depromeet.sloth.ui.base.BaseActivity
//import kotlin.math.ceil
//import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
//import androidx.activity.viewModels
//import com.depromeet.sloth.data.PreferenceManager
//import com.depromeet.sloth.data.network.lesson.LessonCategoryResponse
//import com.depromeet.sloth.data.network.lesson.LessonSiteResponse
//import com.depromeet.sloth.data.network.lesson.LessonState
//import com.depromeet.sloth.ui.DialogState
//import com.depromeet.sloth.ui.SlothDialog
//import com.depromeet.sloth.ui.login.LoginActivity
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//
//@AndroidEntryPoint
//class RegisterLessonFirstActivity : BaseActivity<ActivityRegisterLessonFirstBinding>() {
//    @Inject
//    lateinit var preferenceManager: PreferenceManager
//    private val viewModel: RegisterViewModel by viewModels()
//
//    lateinit var resultLauncher: ActivityResultLauncher<Intent>
//
//    private var flag = 0
//
//    lateinit var accessToken: String
//    lateinit var refreshToken: String
//
//    private var lessonCategoryList = mutableListOf<String>()
//    private var lessonSiteList = mutableListOf<String>()
//
//    lateinit var categoryAdapter: ArrayAdapter<String>
//    lateinit var siteAdapter: ArrayAdapter<String>
//
//    private var currentSiteId: Int? = null
//
//    lateinit var categoryId: Number
//    lateinit var siteId: Number
//
//    override fun getViewBinding(): ActivityRegisterLessonFirstBinding =
//        ActivityRegisterLessonFirstBinding.inflate(layoutInflater)
//
//    companion object {
//        fun newIntent(activity: Activity) =
//            Intent(activity, RegisterLessonFirstActivity::class.java)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        accessToken = preferenceManager.getAccessToken()
//        refreshToken = preferenceManager.getRefreshToken()
//
//        resultLauncher = registerForActivityResult(
//            StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                finish()
//            }
//        }
//
//        mainScope {
//            initLessonCategory()
//            initLessonSite()
//        }
//    }
//
//    private suspend fun initLessonCategory() {
//        viewModel.fetchLessonCategoryList(accessToken = accessToken).let {
//            when (it) {
//                is LessonState.Success<List<LessonCategoryResponse>> -> {
//                    Log.d("fetch Success", "${it.data}")
//                    setLessonCategoryList(it.data)
//                }
//                is LessonState.Error -> {
//                    Log.d("fetch Error", "${it.exception}")
//                }
//                is LessonState.Unauthorized -> {
//                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
//                    dlg.onItemClickListener =
//                        object : SlothDialog.OnItemClickedListener {
//                            override fun onItemClicked() {
//                                preferenceManager.removeAuthToken()
//                                startActivity(LoginActivity.newIntent(this@RegisterLessonFirstActivity))
//                            }
//                        }
//                    dlg.start()
//                }
//                is LessonState.NotFound -> {
//                    Log.d("Error", "NotFound")
//                }
//                is LessonState.Forbidden -> {
//                    Log.d("Error", "Forbidden")
//                }
//            }
//        }
//    }
//
//    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
//        lessonCategoryList = data.map { it.categoryName }.toMutableList()
//        lessonCategoryList.add(0, "인강 카테고리를 선택해 주세요")
//        Log.d("lessonCategoryList", "$lessonCategoryList")
//    }
//
//    private suspend fun initLessonSite() {
//        viewModel.fetchLessonSiteList(accessToken = accessToken).let {
//            when (it) {
//                is LessonState.Success -> {
//                    Log.d("fetch Success", "${it.data}")
//                    setLessonSiteList(it.data)
//
//                    initViews()
//                }
//                is LessonState.Error -> {
//                    Log.d("fetch Error", "${it.exception}")
//                }
//
//                is LessonState.Unauthorized -> {
//                    val dlg = SlothDialog(this, DialogState.FORBIDDEN)
//                    dlg.onItemClickListener =
//                        object : SlothDialog.OnItemClickedListener {
//                            override fun onItemClicked() {
//                                preferenceManager.removeAuthToken()
//                                startActivity(LoginActivity.newIntent(this@RegisterLessonFirstActivity))
//                            }
//                        }
//                    dlg.start()
//                }
//                is LessonState.NotFound -> {
//                    Log.d("Error", "NotFound")
//                }
//                is LessonState.Forbidden -> {
//                    Log.d("Error", "Forbidden")
//                }
//            }
//        }
//    }
//
//    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
//        lessonSiteList = data.map { it.siteName }.toMutableList()
//        lessonSiteList.add(0, "강의 사이트를 선택해주세요")
//        Log.d("lessonSiteList", "$lessonSiteList")
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun initViews() = with(binding) {
//        // 애니메이션 삭제 될 수 있음
//        val aniSlide =
//            AnimationUtils.loadAnimation(this@RegisterLessonFirstActivity, R.anim.slide_down)
//
//        tbRegisterLesson.setNavigationOnClickListener { finish() }
//
//        initSpinner()
//
//        if (flag == 0) {
//            lockButton(btnRegisterLesson)
//            focusInputForm(etRegisterLessonName, btnRegisterLesson)
//
//            btnRegisterLesson.setOnClickListener {
//                startAnimation(aniSlide, tvRegisterLessonCount, etRegisterLessonCount)
//
//                flag += 1
//
//                fillProgressbar(flag, 0)
//
//                editTextClearFocus(etRegisterLessonName)
//
//                if (flag == 1) {
//                    lockButton(btnRegisterLesson)
//                    //focusInputForm(etRegisterLessonCount, btnRegisterLesson)
//                    validateInputForm(etRegisterLessonCount, btnRegisterLesson)
//
//                    btnRegisterLesson.setOnClickListener {
//
//                        startAnimation(aniSlide,
//                            tvRegisterLessonCategory,
//                            spnRegisterLessonCategory)
//
//                        // 첫번째 값을 디폴트 값으로 하여 바로 선택이 되는 것을 막음
//                        spnRegisterLessonCategory.setSelection(0, false)
//
//                        flag += 1
//
//                        fillProgressbar(flag, 0)
//
//                        editTextClearFocus(etRegisterLessonCount)
//
//                        if (flag == 2) {
//                            lockButton(btnRegisterLesson)
//
//                            spnRegisterLessonCategory.onItemSelectedListener = object :
//                                AdapterView.OnItemSelectedListener {
//                                override fun onItemSelected(
//                                    p0: AdapterView<*>?,
//                                    p1: View?,
//                                    p2: Int,
//                                    p3: Long,
//                                ) {
//                                    categoryId = spnRegisterLessonCategory.selectedItemPosition
//
//                                    if (!tvRegisterLessonSite.isVisible) {
//
//                                        startAnimation(aniSlide,
//                                            tvRegisterLessonSite,
//                                            spnRegisterLessonSite)
//                                    }
//
//                                    // 강의 사이트 스피너에서 다시 돌아왔을 경우 progressbar 가 계속 차오르는 경우의 대한 예외 처리
//                                    if (flag < 3) {
//
//                                        flag += 1
//
//                                        fillProgressbar(flag, 0)
//                                    }
//
//                                    if (currentSiteId != null) {
//                                        spnRegisterLessonSite.setSelection(currentSiteId!!)
//
//                                        if (categoryId != 0 && siteId != 0 && etRegisterLessonName.text.toString()
//                                                .isNotEmpty() && etRegisterLessonCount.text.toString()
//                                                .isNotEmpty() && etRegisterLessonCount.text.toString()[0] != '0'
//                                        ) {
//                                            unlockButton(btnRegisterLesson)
//                                        }
//                                    } else {
//                                        // 첫번째 값을 디폴트 값으로 하여 바로 선택이 되는 것을 막음
//                                        spnRegisterLessonSite.setSelection(0, false)
//                                    }
//
//                                    if (categoryId == 0) {
//                                        lockButton(btnRegisterLesson)
//                                    } else {
//                                        if (flag == 3) {
//                                            spnRegisterLessonSite.onItemSelectedListener =
//                                                object : AdapterView.OnItemSelectedListener {
//                                                    override fun onItemSelected(
//                                                        p0: AdapterView<*>?,
//                                                        p1: View?,
//                                                        p2: Int,
//                                                        p3: Long,
//                                                    ) {
//                                                        if (currentSiteId != null) {
//                                                            if (currentSiteId != spnRegisterLessonSite.selectedItemPosition) {
//                                                                currentSiteId =
//                                                                    spnRegisterLessonSite.selectedItemPosition
//                                                                siteId = currentSiteId!!.toInt()
//                                                                Log.d(
//                                                                    "siteId changed to",
//                                                                    currentSiteId.toString()
//                                                                )
//                                                            } else {
//                                                                siteId = currentSiteId!!.toInt()
//                                                            }
//                                                        } else {
//                                                            siteId =
//                                                                spnRegisterLessonSite.selectedItemPosition
//                                                        }
//                                                        Log.d("categoryId: ", categoryId.toString())
//                                                        Log.d("siteId", siteId.toString())
//
//                                                        if (flag < 4) {
//
//                                                            flag += 1
//
//                                                            fillProgressbar(flag, 0)
//
//                                                        }
//                                                        if (siteId == 0) {
//                                                            lockButton(btnRegisterLesson)
//                                                        }
//
//                                                        if (categoryId != 0 && siteId != 0 && etRegisterLessonName.text.toString()
//                                                                .isNotEmpty() && etRegisterLessonCount.text.toString()
//                                                                .isNotEmpty() && etRegisterLessonCount.text.toString()[0] != '0'
//                                                        ) {
//                                                            currentSiteId = siteId.toInt()
//                                                            unlockButton(btnRegisterLesson)
//                                                        }
//
//                                                        btnRegisterLesson.setOnClickListener {
//                                                            resultLauncher.launch(
//                                                                RegisterLessonSecondActivity.newIntent(
//                                                                    this@RegisterLessonFirstActivity,
//                                                                    etRegisterLessonName.text.toString(),
//                                                                    etRegisterLessonCount.text.toString()
//                                                                        .toInt(),
//                                                                    categoryId as Int,
//                                                                    siteId as Int
//                                                                )
//                                                            )
//
//                                                            overridePendingTransition(
//                                                                R.anim.slide_right_enter,
//                                                                R.anim.slide_right_exit
//                                                            )
//                                                        }
//                                                    }
//
//                                                    override fun onNothingSelected(p0: AdapterView<*>?) {}
//                                                }
//                                        }
//                                    }
//                                }
//
//                                override fun onNothingSelected(p0: AdapterView<*>?) {}
//                            }
//                        }
//                    }
//                }
//                return@setOnClickListener
//            }
//        }
//    }
//
//    private fun initSpinner() {
//        categoryAdapter = ArrayAdapter<String>(this@RegisterLessonFirstActivity,
//            android.R.layout.simple_list_item_1,
//            lessonCategoryList)
//        categoryAdapter.setDropDownViewResource(R.layout.item_spinner)
//        binding.spnRegisterLessonCategory.adapter = categoryAdapter
//
//        siteAdapter = ArrayAdapter<String>(this@RegisterLessonFirstActivity,
//            android.R.layout.simple_list_item_1,
//            lessonSiteList)
//        siteAdapter.setDropDownViewResource(R.layout.item_spinner)
//        binding.spnRegisterLessonSite.adapter = siteAdapter
//    }
//
//    private fun fillProgressbar(count: Int, default: Int) {
//        val animation = ObjectAnimator.ofInt(
//            binding.pbRegisterLesson,
//            "progress",
//            default + ceil((count - 1) * 12.5).toInt(),
//            default + ceil(count * 12.5).toInt()
//        )
//
//        animation.duration = 300
//        animation.interpolator = LinearInterpolator()
//        animation.start()
//    }
//
//    private fun hideKeyboard() {
//        val view = this.currentFocus
//        if (view != null) {
//            val imm: InputMethodManager =
//                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun unlockButton(button: AppCompatButton) {
//        button.isEnabled = true
//        button.background = AppCompatResources.getDrawable(
//            this,
//            R.drawable.bg_register_rounded_button_sloth
//        )
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun lockButton(button: AppCompatButton) {
//        button.isEnabled = false
//        button.background = AppCompatResources.getDrawable(
//            this,
//            R.drawable.bg_register_rounded_button_gray
//        )
//    }
//
//    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
//        editText.addTextChangedListener(object : TextWatcher {
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//
//            }
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun afterTextChanged(editable: Editable?) {
//                if (editable.isNullOrEmpty()) {
//                    lockButton(button)
//                } else {
//                    if(binding.etRegisterLessonCount.text.toString().isNotEmpty() && binding.etRegisterLessonCount.text.toString()[0] == '0'){
//                        lockButton(button)
//                    }
//                    else {
//                        unlockButton(button)
//                    }
//                }
//            }
//        })
//
//        editText.setOnFocusChangeListener { _, gainFocus ->
//            if (gainFocus) {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//            } else {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
//            }
//
//        }
//    }
//
//
//    private fun validateInputForm(editText: EditText, button: AppCompatButton) {
//        editText.setOnFocusChangeListener { _, gainFocus ->
//            if (gainFocus) {
//                if (editText.text.toString().isNotEmpty() && editText.text.toString()[0] == '0') {
//                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
//                } else {
//                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//                }
//            } else {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
//            }
//        }
//
//        editText.addTextChangedListener(object : TextWatcher {
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//
//            }
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun afterTextChanged(editable: Editable?) {
//                if (editable.isNullOrEmpty()) {
//                    lockButton(button)
//                } else {
//                    if (editable[0] == '0') {
//                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
//                        lockButton(button)
//                    } else {
//                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//                        unlockButton(button)
//                    }
//                }
//            }
//        })
//    }
//
//
//    private fun startAnimation(animation: Animation, textView: TextView, view: View) {
//        textView.visibility = View.VISIBLE
//        textView.startAnimation(animation)
//
//        view.visibility = View.VISIBLE
//        view.startAnimation(animation)
//    }
//
//    private fun editTextClearFocus(editText: EditText) {
//        editText.clearFocus()
//        hideKeyboard()
//    }
//}