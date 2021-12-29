package com.depromeet.sloth.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.lesson.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.databinding.FragmentTodayBinding
import com.depromeet.sloth.ui.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.LessonItemDecoration
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.register.RegisterLessonFirstActivity

class TodayFragment : BaseFragment<LessonViewModel, FragmentTodayBinding>() {
    private val pm: PreferenceManager by lazy { PreferenceManager(requireActivity()) }
    lateinit var accessToken: String
    lateinit var refreshToken: String

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentTodayBinding =
        FragmentTodayBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = pm.getAccessToken()
        refreshToken = pm.getRefreshToken()

        initViews()
        fetchLessonList()

        //setTestData()
    }

    override fun initViews() {
        with(binding) {
            rvTodayLesson.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            ivTodayAlarm.setOnClickListener {
                val dlg = SlothDialog(requireContext(), DialogState.WAIT)
                dlg.start()
            }
        }
    }

    private fun fetchLessonList() {
        mainScope {
            viewModel.fetchTodayLessonList(accessToken = accessToken).let {
                when (it) {
                    is LessonState.Success<List<LessonTodayResponse>> -> {
                        Log.d("fetch Success", "${it.data}")
                        setLessonList(it.data)
                    }
                    is LessonState.Unauthorized -> {
                        viewModel.fetchTodayLessonList(accessToken = refreshToken)
                            .let { lessonTodayResponse ->
                                when (lessonTodayResponse) {
                                    is LessonState.Success -> {
                                        Log.d("fetch Success", "${lessonTodayResponse.data}")
                                        setLessonList(lessonTodayResponse.data)
                                    }
                                    is LessonState.Unauthorized -> {
                                        val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
                                        dlg.onItemClickListener =
                                            object : SlothDialog.OnItemClickedListener {
                                                override fun onItemClicked() {
                                                    //logout

                                                    //finish
                                                    mainScope {
                                                        viewModel.removeAuthToken(pm)
                                                        startActivity(
                                                            LoginActivity.newIntent(
                                                                requireActivity()
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        dlg.start()
                                    }
                                    is LessonState.Forbidden -> {
                                        // refresh 토큰이 존재하지 않을 경우 예외 처리
                                        val dlg =
                                            SlothDialog(requireContext(), DialogState.FORBIDDEN)
                                        dlg.onItemClickListener =
                                            object : SlothDialog.OnItemClickedListener {
                                                override fun onItemClicked() {
                                                    //logout

                                                    //finish
                                                    mainScope {
                                                        viewModel.removeAuthToken(pm)
                                                        startActivity(LoginActivity.newIntent(
                                                                requireActivity()
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        dlg.start()
                                    }
                                    is LessonState.Error -> {
                                        Log.d("fetch Error", "${lessonTodayResponse.exception}")
                                    }
                                    else -> Unit
                                }
                            }
                    }
                    is LessonState.NotFound -> {
                        Log.d("Error", "NotFound")
                    }
                    is LessonState.Forbidden -> {
                        Log.d("Error", "Forbidden")
                    }
                    is LessonState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }
                }
            }
        }
    }

    private fun moveRegisterActivity() {
        val intent = Intent(requireContext(), RegisterLessonFirstActivity::class.java)
        startActivity(intent)
    }

    private fun moveDetailActivity(lessonToday: LessonTodayResponse) {
        val intent = Intent(requireContext(), LessonDetailActivity::class.java)
        intent.putExtra("lessonId", lessonToday.lessonId.toString())
        startActivity(intent)
    }

    private fun setLessonList(lessonTodayList: List<LessonTodayResponse>) {
        when (lessonTodayList.isEmpty()) {
            true -> {
                val nothingHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOTHING)
                val nothingLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOTHING) { _, _ -> moveRegisterActivity() }
                val concatAdapter = ConcatAdapter(
                    nothingHeader,
                    nothingLessonAdapter
                )
                nothingLessonAdapter.submitList(listOf(LessonTodayResponse.EMPTY))

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    tvTodayTitleMessage.text = getString(R.string.home_today_title_not_register)
                }
            }

            false -> {
                val lessonFinishedList = mutableListOf<LessonTodayResponse>()
                val lessonNotFinishedList = mutableListOf<LessonTodayResponse>()
                lessonTodayList.forEach { lesson ->
                    if (lesson.untilTodayFinished) lessonFinishedList.add(lesson)
                    else lessonNotFinishedList.add(lesson)
                }

                val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
                val notFinishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { clickType, lesson ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lesson,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lesson,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_NORMAL -> {
                            }
                        }
                    }
                val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
                val finishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { clickType, lesson ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lesson,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lesson,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_NORMAL -> {
                            }
                        }
                    }
                val concatAdapter = ConcatAdapter(
                    notFinishedHeader,
                    notFinishedLessonAdapter,
                    finishedHeader,
                    finishedLessonAdapter
                )

                if (lessonFinishedList.isEmpty()) {
                    concatAdapter.removeAdapter(finishedHeader)
                    concatAdapter.removeAdapter(finishedLessonAdapter)
                } else {
                    finishedLessonAdapter.submitList(lessonFinishedList)
                }

                if (lessonNotFinishedList.isEmpty()) {
                    concatAdapter.removeAdapter(notFinishedHeader)
                    concatAdapter.removeAdapter(notFinishedLessonAdapter)
                } else {
                    notFinishedLessonAdapter.submitList(lessonNotFinishedList)
                }

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    when {
                        lessonFinishedList.isNotEmpty() && lessonNotFinishedList.isEmpty() -> tvTodayTitleMessage.text =
                            getString(R.string.home_today_title_win)
                        lessonFinishedList.isEmpty() && lessonNotFinishedList.isNotEmpty() -> tvTodayTitleMessage.text =
                            getString(R.string.home_today_title_not_start)
                        else -> tvTodayTitleMessage.text = getString(R.string.home_today_title_lose)
                    }
                }
            }
        }
    }

    private fun updateLessonCount(
        lesson: LessonTodayResponse,
        count: Int,
        bodyType: TodayLessonAdapter.BodyType
    ) {
        mainScope {
            viewModel.updateLessonCount(
                accessToken = accessToken,
                count = count,
                lessonId = lesson.lessonId
            ).let {
                when (it) {
                    is LessonState.Success<LessonUpdateCountResponse> -> {
                        Log.d("update Success", it.data.toString())
                        when (bodyType) {
                            TodayLessonAdapter.BodyType.NOT_FINISHED -> {
                                if (it.data.presentNumber == lesson.untilTodayNumber) fetchLessonList()
                            }
                            TodayLessonAdapter.BodyType.FINISHED -> {
                                if (it.data.presentNumber < lesson.untilTodayNumber) fetchLessonList()
                            }
                            else -> Unit
                        }
                    }
                    is LessonState.Unauthorized -> {
                        Log.i("Unauthorized", "refreshToken used")
                        viewModel.updateLessonCount(
                            accessToken = refreshToken,
                            count = count,
                            lessonId = lesson.lessonId
                        ).let { lessonUpdateCountResponse ->
                            when (lessonUpdateCountResponse) {
                                is LessonState.Success -> {
                                    when (bodyType) {
                                        TodayLessonAdapter.BodyType.NOT_FINISHED -> {
                                            if (lessonUpdateCountResponse.data.presentNumber == lesson.untilTodayNumber) {
                                                fetchLessonList()
                                            } else {
                                            }
                                        }
                                        TodayLessonAdapter.BodyType.FINISHED -> {
                                            if (lessonUpdateCountResponse.data.presentNumber < lesson.untilTodayNumber) {
                                                fetchLessonList()
                                            } else {
                                            }
                                        }
                                        else -> Unit
                                    }
                                }

                                is LessonState.Error -> {
                                    Log.d("update Error", "${lessonUpdateCountResponse.exception}")
                                }
                                else -> Unit
                            }
                        }
                    }
                    is LessonState.NotFound -> {
                        Log.d("Error", "NotFound")
                    }
                    is LessonState.Forbidden -> {
                        Log.d("Error", "Forbidden")
                    }
                    is LessonState.Error -> {
                        Log.d("update Error", "${it.exception}")
                    }
                }
            }
        }
    }

    private fun setTestData() {
        val dummyList = listOf<LessonTodayResponse>(
            LessonTodayResponse(
                categoryName = "개발",
                lessonId = 1,
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 9,
                siteName = "TEST1",
                untilTodayFinished = false,
                untilTodayNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "디자인",
                lessonId = 2,
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 5,
                remainDay = 19,
                siteName = "TEST2",
                untilTodayFinished = true,
                untilTodayNumber = 5
            ),
            LessonTodayResponse(
                categoryName = "기획",
                lessonId = 3,
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 10,
                siteName = "TEST3",
                untilTodayFinished = false,
                untilTodayNumber = 6
            ),
            LessonTodayResponse(
                categoryName = "개발",
                lessonId = 4,
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 6,
                remainDay = 7,
                siteName = "TEST4",
                untilTodayFinished = true,
                untilTodayNumber = 6
            ),
            LessonTodayResponse(
                categoryName = "디자인",
                lessonId = 5,
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 1,
                remainDay = 11,
                siteName = "TEST5",
                untilTodayFinished = false,
                untilTodayNumber = 4
            ),
            LessonTodayResponse(
                categoryName = "기획",
                lessonId = 6,
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 2,
                remainDay = 1,
                siteName = "TEST6",
                untilTodayFinished = false,
                untilTodayNumber = 3
            )
        )

        val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
        val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
        val notFinishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { clickType, lesson ->
                when (clickType) {
                    TodayLessonAdapter.ClickType.CLICK_PLUS -> {

                    }

                    TodayLessonAdapter.ClickType.CLICK_MINUS -> {

                    }

                    TodayLessonAdapter.ClickType.CLICK_NORMAL -> {

                    }
                }
            }
        val finishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { _, lesson ->
                moveDetailActivity(lesson)
            }
        val concatAdapter = ConcatAdapter(
            notFinishedHeader,
            notFinishedLessonAdapter,
            finishedHeader,
            finishedLessonAdapter
        )

        dummyList.let {
            finishedLessonAdapter.submitList(
                dummyList.filter { it.untilTodayFinished }
            )
            notFinishedLessonAdapter.submitList(
                dummyList.filter { it.untilTodayFinished.not() }
            )
        }

        binding.rvTodayLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}