package com.depromeet.sloth.ui.register

import com.depromeet.sloth.R
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.ui.base.BaseFragment

class RegisterLessonFirstFragment : BaseFragment<FragmentRegisterLessonFirstBinding>() {

    lateinit var categoryMap: HashMap<Int, String>
    private var categoryList = mutableListOf<String>()
    lateinit var categoryAdapter: ArrayAdapter<String>

    lateinit var siteMap: HashMap<Int, String>
    private var siteList = mutableListOf<String>()
    lateinit var siteAdapter: ArrayAdapter<String>

    lateinit var lessonCount: Number

    companion object {
        const val LESSON_NAME = "lessonName"
        const val LESSON_COUNT = "lessonCount"
        const val LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val LESSON_SITE_NAME = "lessonSiteName"
    }

    override fun getViewBinding(): FragmentRegisterLessonFirstBinding =
        FragmentRegisterLessonFirstBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as RegisterLessonActivity).apply {
            categoryMap = lessonCategoryMap
            categoryList = lessonCategoryList

            siteMap = lessonSiteMap
            siteList = lessonSiteList
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun initViews() = with(binding) {
        if (::categoryAdapter.isInitialized.not()) {
            initAdapter()
        }

        bindSpinner()

        lockButton(btnRegisterLesson)

        // validate
        focusInputForm(etRegisterLessonName, btnRegisterLesson)
        validateInputForm(etRegisterLessonCount, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonCategory, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonSite, btnRegisterLesson)

        btnRegisterLesson.setOnClickListener {
            clearFocus(etRegisterLessonCount)

            if (etRegisterLessonName.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "강의 이름을 입력해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "강의 개수를 입력해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString()[0] == '0'
                || etRegisterLessonCount.text.toString().toInt() == 0
            ) {
                Toast.makeText(requireContext(), "강의 개수가 올바르지 않습니다.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (spnRegisterLessonCategory.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "강의 카테고리를 선택해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (spnRegisterLessonSite.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "강의 사이트를 선택해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val args = Bundle().apply {
                putString(LESSON_NAME, etRegisterLessonName.text.toString())
                putInt(LESSON_COUNT, etRegisterLessonCount.text.toString().toInt())
                putString(LESSON_CATEGORY_NAME,
                    spnRegisterLessonCategory.selectedItem.toString())
                putString(LESSON_SITE_NAME,
                    spnRegisterLessonSite.selectedItem.toString())
            }

            Log.d("bundle", "$args")


            // nextFragment
            (activity as RegisterLessonActivity).changeFragment(
                (activity as RegisterLessonActivity).registerLessonSecondFragment, args)
        }
    }

    private fun initAdapter() {
        Log.d("categoryAdapter", "notInitialized")
        categoryAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            categoryList
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        siteAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            siteList
        )
        siteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private fun bindSpinner() = with(binding) {
        spnRegisterLessonCategory.adapter = categoryAdapter
        spnRegisterLessonSite.adapter = siteAdapter
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: AppCompatButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.bg_register_rounded_button_sloth
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: AppCompatButton) {
        button.isEnabled = false
        button.background = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.bg_register_rounded_button_disabled
        )
    }

    private fun focusSpinnerForm(spinner: Spinner, button: AppCompatButton) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                clearFocus(binding.etRegisterLessonCount)

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

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button)
                } else {
                    unlockButton(button)
                }
            }
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }


    private fun validateInputForm(editText: EditText, button: AppCompatButton) {
        var result = ""

        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    lessonCount = charSequence.toString().toInt()
                    result = lessonCount.toString()
                    if (result[0] == '0') {
                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                        lockButton(button)
                    } else {
                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                        unlockButton(button)
                    }
                    editText.setText(result)
                    // 커서 위치 설정
                    editText.setSelection(result.length)
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

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun clearFocus(editText: EditText) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
    }
}