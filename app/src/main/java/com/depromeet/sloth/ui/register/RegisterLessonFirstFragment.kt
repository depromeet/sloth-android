package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.databinding.FragmentRegisterLessonFirstBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.KEY_LESSON_TOTAL_NUMBER
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


//TODO button 관련은 mediatorLivedata 를 통해서
//EditText, TextView, Button 는 style 만들어서 그걸로 재활용
//데이터를 계속 뷰에서 담아서 이동하지말고 viewmodel 에서 관리
//form 함수들이랑 button 분리시키기
//category,site 비동기

@AndroidEntryPoint
class RegisterLessonFirstFragment :
    BaseFragment<FragmentRegisterLessonFirstBinding>(R.layout.fragment_register_lesson_first) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    lateinit var lessonCategoryMap: HashMap<Int, String>
    private var lessonCategoryList: MutableList<String> = mutableListOf()

    lateinit var lessonSiteMap: HashMap<Int, String>
    private var lessonSiteList: MutableList<String> = mutableListOf()

    lateinit var lessonCategoryAdapter: ArrayAdapter<String>
    lateinit var lessonSiteAdapter: ArrayAdapter<String>

    lateinit var lessonCount: Number

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            lessonCategoryListState.observe(viewLifecycleOwner, EventObserver { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(requireContext())

                    is LessonState.Success<List<LessonCategoryResponse>> -> {
                        setLessonCategoryList(lessonState.data)
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(requireContext(),
                            requireActivity()) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast("강의 카테고리를 가져오지 못했어요")
                    }
                    else -> Unit
                }
                hideProgress()
            })

            lessonSiteListState.observe(viewLifecycleOwner, EventObserver { lessonState ->
                when (lessonState) {
                    is LessonState.Loading -> handleLoadingState(requireContext())

                    is LessonState.Success<List<LessonSiteResponse>> -> {
                        setLessonSiteList(lessonState.data)
                    }

                    is LessonState.Unauthorized -> {
                        showLogoutDialog(requireContext(),
                            requireActivity()) { viewModel.removeAuthToken() }
                    }

                    is LessonState.Error -> {
                        Timber.tag("fetch Error").d(lessonState.throwable)
                        showToast("강의 사이트를 가져오지 못했어요")
                    }
                    else -> Unit
                }
                hideProgress()
            })

            lessonCategorySelectedItemPosition.observe(viewLifecycleOwner) { position ->
                initViews()
                lessonCategoryAdapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.item_spinner,
                    lessonCategoryList
                )
                lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.spnRegisterLessonCategory.adapter = lessonCategoryAdapter

                binding.spnRegisterLessonCategory.setSelection(position)
            }

            lessonSiteSelectedItemPosition.observe(viewLifecycleOwner) { position ->
                lessonSiteAdapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.item_spinner,
                    lessonSiteList
                )
                lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.spnRegisterLessonSite.adapter = lessonSiteAdapter

                binding.spnRegisterLessonSite.setSelection(position)
            }
        }
    }

    private fun setLessonCategoryList(data: List<LessonCategoryResponse>) {
        lessonCategoryMap =
                //data.map { it.categoryId to it.categoryName }.toMap() as HashMap<Int, String>
            data.associate { it.categoryId to it.categoryName } as HashMap<Int, String>
        lessonCategoryList = data.map { it.categoryName }.toMutableList()
        lessonCategoryList.add(0, "강의 카테고리를 선택해 주세요")
    }

    private fun setLessonSiteList(data: List<LessonSiteResponse>) {
        //data.map { it.siteId to it.siteName }.toMap() as HashMap<Int, String>
        lessonSiteMap = data.associate { it.siteId to it.siteName } as HashMap<Int, String>
        lessonSiteList = data.map { it.siteName }.toMutableList()
        lessonSiteList.add(0, "강의 사이트를 선택해 주세요")
        initViews()
    }

    override fun initViews() = with(binding) {
        if (etRegisterLessonCount.hasFocus()) {
            clearFocus(etRegisterLessonCount)
        }
        if (::lessonCategoryAdapter.isInitialized.not() && ::lessonSiteAdapter.isInitialized.not()) {
            bindAdapter()
        }

        lockButton(btnRegisterLesson, requireContext())

        focusInputForm(etRegisterLessonName, btnRegisterLesson)
        validateInputForm(etRegisterLessonCount, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonCategory, btnRegisterLesson)
        focusSpinnerForm(spnRegisterLessonSite, btnRegisterLesson)

        btnRegisterLesson.setOnClickListener {
            clearFocus(etRegisterLessonCount)

            if (etRegisterLessonName.text.toString().isEmpty()) {
                showToast("강의 이름을 입력해 주세요")
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString().isEmpty()) {
                showToast("강의 개수를 입력해 주세요")
                return@setOnClickListener
            }

            if (etRegisterLessonCount.text.toString()[0] == '0'
                || etRegisterLessonCount.text.toString().toInt() == 0
            ) {
                showToast("강의 개수가 올바르지 않아요")
                return@setOnClickListener
            }

            if (spnRegisterLessonCategory.selectedItemPosition == 0) {
                showToast("강의 카테고리를 선택해 주세요")
                return@setOnClickListener
            }

            if (spnRegisterLessonSite.selectedItemPosition == 0) {
                showToast("강의 사이트를 선택해 주세요")
                return@setOnClickListener
            }

            moveRegisterLessonSecond()
        }
    }

    private fun moveRegisterLessonSecond() = with(binding) {
        viewModel.setLessonCategoryItemPosition(spnRegisterLessonCategory.selectedItemPosition)
        viewModel.setLessonSiteItemPosition(spnRegisterLessonSite.selectedItemPosition)

        findNavController().navigate(
            R.id.action_register_lesson_first_to_register_lesson_second, bundleOf(
                KEY_LESSON_TOTAL_NUMBER to etRegisterLessonCount.text.toString().toInt(),
            )
        )
        //findNavController().navigate(R.id.action_register_lesson_first_to_register_lesson_second)
    }

    private fun bindAdapter() = with(binding) {
        lessonCategoryAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            lessonCategoryList
        )
        lessonCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRegisterLessonCategory.adapter = lessonCategoryAdapter

        lessonSiteAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            lessonSiteList
        )
        lessonSiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRegisterLessonSite.adapter = lessonSiteAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    //두개의 spinner 를 하나의 listener 로 관리

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

                    spnRegisterLessonCategory

                    val spinnerId = spinner.selectedItemPosition
                    if (spinnerId == 0) {
                        lockButton(button, requireContext())
                    } else {
                        //viewModel 에는 안드로이드 의존성이 있으면 안되므로..
                        // == 이 맞나
                        if (spinner == spnRegisterLessonCategory) {
                            viewModel.setCategoryId(lessonCategoryMap.filterValues
                            { it == spnRegisterLessonCategory.selectedItem.toString() }.keys.first())
                            viewModel.setCategoryName(spinner.selectedItem.toString())
                        } else {
                            viewModel.setSiteId(lessonSiteMap.filterValues
                            { it == spnRegisterLessonSite.selectedItem.toString() }.keys.first())
                            viewModel.setSiteName(spinner.selectedItem.toString())
                        }

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
            override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                viewModel.setLessonName(text.toString())
            }

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
                        //viewModel.setLessonTotalNumber(editText.toString().toInt())
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