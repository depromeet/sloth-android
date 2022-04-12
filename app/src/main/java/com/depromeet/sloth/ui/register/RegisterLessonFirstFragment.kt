package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.extensions.hideKeyBoard
import com.depromeet.sloth.extensions.lockButton
import com.depromeet.sloth.extensions.unlockButton
import com.depromeet.sloth.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterLessonFirstFragment : BaseFragment<FragmentRegisterLessonFirstBinding>() {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    lateinit var lessonCategoryAdapter: ArrayAdapter<String>
    lateinit var lessonSiteAdapter: ArrayAdapter<String>
    lateinit var lessonCount: Number

    companion object {
        const val LESSON_NAME = "lessonName"
        const val LESSON_TOTAL_NUMBER = "lessonCount"
        const val LESSON_CATEGORY_NAME = "lessonCategoryName"
        const val LESSON_SITE_NAME = "lessonSiteName"
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentRegisterLessonFirstBinding {
        return FragmentRegisterLessonFirstBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun initViews() = with(binding) {
        if (::lessonCategoryAdapter.isInitialized.not()) {
            bindAdapter()
        }

        if (etRegisterLessonCount.hasFocus()) {
            clearFocus(etRegisterLessonCount)
        }

        bindSpinner()

        lockButton(btnRegisterLesson, requireContext())

        focusInputForm(etRegisterLessonName, btnRegisterLesson)
        validateInputForm(etRegisterLessonCount, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonCategory, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonSite, btnRegisterLesson)

        btnRegisterLesson.setOnClickListener {
            clearFocus(etRegisterLessonCount)

            if (etRegisterLessonName.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "강의 이름을 입력해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "강의 개수를 입력해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString()[0] == '0'
                || etRegisterLessonCount.text.toString().toInt() == 0
            ) {
                Toast.makeText(requireContext(), "강의 개수가 올바르지 않아요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (spnRegisterLessonCategory.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "강의 카테고리를 선택해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (spnRegisterLessonSite.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "강의 사이트를 선택해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val args = Bundle().apply {
                putString(LESSON_NAME, etRegisterLessonName.text.toString())
                putInt(LESSON_TOTAL_NUMBER, etRegisterLessonCount.text.toString().toInt())
                putString(LESSON_CATEGORY_NAME,
                    spnRegisterLessonCategory.selectedItem.toString())
                putString(LESSON_SITE_NAME,
                    spnRegisterLessonSite.selectedItem.toString())
            }

            (activity as RegisterLessonActivity).changeFragment(
                (activity as RegisterLessonActivity).registerLessonSecondFragment, args)

            //moveRegisterLessonSecond()
        }
    }

    private fun moveRegisterLessonSecond() = with(binding) {
        findNavController().navigate(
            //data 는 bundle 객체에 담아 보냄
            R.id.action_register_lesson_first_to_register_lesson_second, bundleOf(
                //"key" to "value"
                LESSON_NAME to etRegisterLessonName.text.toString(),
                LESSON_TOTAL_NUMBER to etRegisterLessonCount.text.toString().toInt(),
                LESSON_CATEGORY_NAME to spnRegisterLessonCategory.selectedItem.toString(),
                LESSON_SITE_NAME to spnRegisterLessonSite.selectedItem.toString()
            )
        )
    }

    private fun bindAdapter() {
        lessonCategoryAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            (activity as RegisterLessonActivity).lessonCategoryList
        )
        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        lessonSiteAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            (activity as RegisterLessonActivity).lessonSiteList
        )
        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private fun bindSpinner() = with(binding) {
        spnRegisterLessonCategory.adapter = lessonCategoryAdapter
        spnRegisterLessonSite.adapter = lessonSiteAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun focusSpinnerForm(spinner: Spinner, button: AppCompatButton) = with(binding) {
        spinner.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(requireActivity())
                }
                false
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    clearFocus(etRegisterLessonCount)
                    clearFocus(etRegisterLessonName)

                    val spinnerId = spinner.selectedItemPosition
                    if (spinnerId == 0) {
                        lockButton(button, requireContext())
                    } else {
                        unlockButton(button, requireContext())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    unlockButton(button, requireContext())
                }
            }
        }
    }

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button, requireContext())
                } else {
                    unlockButton(button, requireContext())
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


    private fun validateInputForm(editText: EditText, button: AppCompatButton) = with(binding) {
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
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                        lockButton(button, requireContext())
                    } else {
                        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                        unlockButton(button, requireContext())
                    }
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvRegisterLessonTotalNumberInfo.apply {
                        text = getString(R.string.input_lesson_count, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = ""
                    editText.setText(result)
                    tvRegisterLessonTotalNumberInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty() || editable[0] == '0') {
                    lockButton(button, requireContext())
                } else {
                    unlockButton(button, requireContext())
                }
            }
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
                tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun clearFocus(editText: EditText) = with(binding) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
        tvRegisterLessonTotalNumberInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
    }
}