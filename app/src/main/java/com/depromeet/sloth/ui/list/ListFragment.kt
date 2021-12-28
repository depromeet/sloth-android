package com.depromeet.sloth.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.list.LessonState
import com.depromeet.sloth.data.network.list.LessonInfoResponse
import com.depromeet.sloth.databinding.FragmentListBinding
import com.depromeet.sloth.ui.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.LessonItemDecoration
import com.depromeet.sloth.ui.LessonViewModel
import com.depromeet.sloth.ui.register.RegisterLessonFirstActivity
import java.text.SimpleDateFormat
import java.util.*

class ListFragment : BaseFragment<LessonViewModel, FragmentListBinding>() {
    private val pm: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentListBinding =
        FragmentListBinding.inflate(layoutInflater)

    lateinit var accessToken: String
    lateinit var refreshToken: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = pm.getAccessToken().toString()
        refreshToken = pm.getRefreshToken().toString()

        initViews()

        //setTestData()
    }

    override fun onStart() {
        super.onStart()

        fetchLessonList()
    }

    private fun fetchLessonList() {
        mainScope {
            viewModel.fetchAllLessonList(accessToken = accessToken).let {
                when (it) {
                    is LessonState.Success<List<LessonInfoResponse>> -> {
                        Log.d("fetch Success", "${it.data}")
                        setLessonList(it.data)
                    }
                    is LessonState.Error -> {
                        Log.d("fetch Error", "${it.exception}")
                    }
                    is LessonState.Unauthorized -> {
                        viewModel.fetchAllLessonList(accessToken = refreshToken).let { lessonInfoResponse ->
                            when(lessonInfoResponse) {
                                is LessonState.Success -> {
                                    Log.d("fetch Success", "${lessonInfoResponse.data}")
                                    setLessonList(lessonInfoResponse.data)
                                }
                                is LessonState.Error -> {
                                    Log.d("fetch Error", "${lessonInfoResponse.exception}")
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
                }
            }
        }
    }

    override fun initViews() {
        with(binding) {
            rvLessonList.addItemDecoration(LessonItemDecoration(requireContext(), 16))

            ivLessonListRegister.setOnClickListener {
                startActivity(RegisterLessonFirstActivity.newIntent(requireActivity()))
            }
            ivLessonListAlarm.setOnClickListener {
                val dlg = SlothDialog(requireContext(),DialogState.WAIT)
                dlg.start()
            }
        }
    }

    private fun moveRegisterActivity() {
        val intent = Intent(requireContext(), RegisterLessonFirstActivity::class.java)
        startActivity(intent)
    }

    private fun moveDetailActivity(lessonInfo: LessonInfoResponse) {
        val intent = Intent(requireContext(), LessonDetailActivity::class.java)
        intent.putExtra("lessonId", lessonInfo.lessonId.toString())
        startActivity(intent)
    }

    private fun setLessonList(lessonInfoList: List<LessonInfoResponse>) {
        when (lessonInfoList.isEmpty()) {
            true -> {
                binding.ivLessonListRegister.visibility = View.INVISIBLE
                val nothingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.NOTHING) { _ -> moveRegisterActivity() }

                nothingLessonAdapter.submitList(listOf(LessonInfoResponse.EMPTY))
                binding.rvLessonList.adapter = nothingLessonAdapter
            }

            false -> {
                binding.ivLessonListRegister.visibility = View.VISIBLE

                val lessonDoingList = mutableListOf<LessonInfoResponse>()
                val lessonPlanningList = mutableListOf<LessonInfoResponse>()
                val lessonPassedList = mutableListOf<LessonInfoResponse>()
                lessonInfoList.forEach { lesson ->
                    when (getLessonType(lesson)) {
                        LessonListAdapter.BodyType.PASSED -> lessonPassedList.add(lesson)
                        LessonListAdapter.BodyType.PLANNING -> lessonPlanningList.add(lesson)
                        else -> lessonDoingList.add(lesson)
                    }
                }

                val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING)
                val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING)
                val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED)
                val doingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.DOING) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val planningLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PLANNING) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val passedLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PASSED) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val concatAdapter = ConcatAdapter(
                    doingHeader,
                    doingLessonAdapter,
                    planningHeader,
                    planningLessonAdapter,
                    passedHeader,
                    passedLessonAdapter
                )

                if (lessonDoingList.isEmpty()) {
                    concatAdapter.removeAdapter(doingHeader)
                    concatAdapter.removeAdapter(doingLessonAdapter)
                } else {
                    doingLessonAdapter.submitList(lessonDoingList)
                }

                if (lessonPlanningList.isEmpty()) {
                    concatAdapter.removeAdapter(planningHeader)
                    concatAdapter.removeAdapter(planningLessonAdapter)
                } else {
                    planningLessonAdapter.submitList(lessonPlanningList)
                }

                if (lessonPassedList.isEmpty()) {
                    concatAdapter.removeAdapter(passedHeader)
                    concatAdapter.removeAdapter(passedLessonAdapter)
                } else {
                    passedLessonAdapter.submitList(lessonPassedList)
                }

                binding.rvLessonList.adapter = concatAdapter
            }
        }
    }

    private fun getLessonType(
        lessonInfo: LessonInfoResponse
    ): LessonListAdapter.BodyType {
        val startDateString = lessonInfo.startDate
        val endDateString = lessonInfo.endDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val startDate = dateFormat.parse(startDateString)
        val todayDate = Calendar.getInstance()
        val endDate = dateFormat.parse(endDateString)
        val isPassed = (endDate.time - todayDate.time.time) < 0L
        val isPlanning = (todayDate.time.time - startDate.time) < 0L

        return when {
            isPassed -> LessonListAdapter.BodyType.PASSED
            isPlanning -> LessonListAdapter.BodyType.PLANNING
            else -> LessonListAdapter.BodyType.DOING
        }
    }

    private fun setTestData() {
        val dummyList = listOf<LessonInfoResponse>(
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nScala 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 8,
                goalProgressRate = 8,
                totalNumber = 60,
                price = 50000,
                startDate = "2021-08-19 10:12:14",
                endDate = "2021-11-24 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nKotlin 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 0,
                goalProgressRate = 20,
                totalNumber = 50,
                price = 30000,
                startDate = "2021-12-22 10:12:14",
                endDate = "2022-11-11 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nC++ 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 0,
                goalProgressRate = 12,
                totalNumber = 22,
                price = 150000,
                startDate = "2022-01-12 10:12:14",
                endDate = "2022-11-24 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nPython 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 6,
                goalProgressRate = 8,
                totalNumber = 16,
                price = 25000,
                startDate = "2021-11-10 10:12:14",
                endDate = "2021-12-24 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nGolang 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 8,
                goalProgressRate = 8,
                totalNumber = 42,
                price = 55000,
                startDate = "2021-09-19 10:12:14",
                endDate = "2021-10-22 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nRuby 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 4,
                goalProgressRate = 4,
                totalNumber = 40,
                price = 68000,
                startDate = "2021-11-15 10:12:14",
                endDate = "2021-11-30 10:12:14"
            ),
            LessonInfoResponse(
                lessonName = "프로그래밍 시작하기 : \nJava 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 1,
                goalProgressRate = 4,
                totalNumber = 30,
                price = 33000,
                startDate = "2021-10-19 10:12:14",
                endDate = "2021-12-31 10:12:14"
            )
        )

        val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING)
        val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING)
        val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED)

        val doingLessonAdapter = LessonListAdapter(LessonListAdapter.BodyType.DOING) { lesson ->
            moveDetailActivity(lesson)
        }
        val planningLessonAdapter =
            LessonListAdapter(LessonListAdapter.BodyType.PLANNING) { lesson ->
                moveDetailActivity(lesson)
            }
        val passedLessonAdapter = LessonListAdapter(LessonListAdapter.BodyType.PASSED) { lesson ->
            moveDetailActivity(lesson)
        }

        val concatAdapter = ConcatAdapter(
            doingHeader,
            doingLessonAdapter,
            planningHeader,
            planningLessonAdapter,
            passedHeader,
            passedLessonAdapter
        )

        dummyList.let {
            doingLessonAdapter.submitList(
                dummyList.filter {
                    val startDateString = it.startDate
                    val endDateString = it.endDate
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val startDate = dateFormat.parse(startDateString)
                    val endDate = dateFormat.parse(endDateString)
                    val todayDate = Calendar.getInstance()
                    val startDiffTime =
                        (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                    val endDiffTime =
                        (endDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                    val progressRatio = it.currentProgressRate / it.goalProgressRate.toDouble()
                    val inValidData =
                        startDiffTime < 0.0 && endDiffTime >= 0.0 && progressRatio < 1.0
                    inValidData
                }
            )

            planningLessonAdapter.submitList(
                dummyList.filter {
                    val startDateString = it.startDate
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val startDate = dateFormat.parse(startDateString)
                    val todayDate = Calendar.getInstance()
                    val endDiffTime =
                        (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                    val inValidData = endDiffTime > 0.0
                    inValidData
                }
            )

            passedLessonAdapter.submitList(
                dummyList.filter {
                    val progressRatio = it.currentProgressRate / it.goalProgressRate
                    val inValidData = (progressRatio == 1)
                    inValidData
                }
            )
        }

        binding.rvLessonList.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}