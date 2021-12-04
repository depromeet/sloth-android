package com.depromeet.sloth.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.home.LessonState
import com.depromeet.sloth.data.network.home.AllLessonResponse
import com.depromeet.sloth.data.network.home.TodayLessonResponse
import com.depromeet.sloth.databinding.FragmentClassBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.register.RegisterLessonFirstActivity
import java.text.SimpleDateFormat
import java.util.*

class ClassFragment : BaseFragment<LessonViewModel, FragmentClassBinding>() {
    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentClassBinding =
        FragmentClassBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        var lessonList = listOf<AllLessonResponse>()

        mainScope {
            preferenceManager.getAccessToken()?.run {
                viewModel.fetchAllLessonList(
                    accessToken = this
                ).let {
                    when (it) {
                        is LessonState.Success<List<AllLessonResponse>> -> {
                            lessonList = it.data
                            updateLessonList(lessonList)
                        }
                        is LessonState.Error -> {
                            Log.d("Error", "${it.exception}")
                        }
                        is LessonState.Unauthorized -> {
                            Log.d("Error", "Unauthorized")
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

        //setTestData()
    }

    private fun moveRegisterActivity() {
        val intent = Intent(requireContext(), RegisterLessonFirstActivity::class.java)
        startActivity(intent)
    }

    private fun moveDetailActivity(lesson: AllLessonResponse) {
        val intent = Intent(requireContext(), LessonDetailActivity::class.java)
        intent.putExtra("lessonId", lesson.lessonId.toString())
        startActivity(intent)
    }

    private fun updateLessonList(lessonList: List<AllLessonResponse>) {
        when (lessonList.isEmpty()) {
            true -> {
                binding.ivClassRegister.visibility = View.VISIBLE
                val nothingLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.NOTHING) { _ -> moveRegisterActivity() }

                nothingLessonAdapter.submitList(listOf(AllLessonResponse.EMPTY))
                binding.rvClassLesson.adapter = nothingLessonAdapter
            }

            false -> {
                binding.ivClassRegister.visibility = View.INVISIBLE

                val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING)
                val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING)
                val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED)

                val doingLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.DOING) { lesson ->
                    moveDetailActivity(lesson)
                }
                val planningLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PLANNING) { lesson ->
                    moveDetailActivity(lesson)
                }
                val passedLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PASSED) { lesson ->
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

                lessonList.let {
                    doingLessonAdapter.submitList(
                        lessonList.filter {
                            val startDateString = it.startDate
                            val endDateString = it.endDate
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val startDate = dateFormat.parse(startDateString)
                            val endDate = dateFormat.parse(endDateString)
                            val todayDate = Calendar.getInstance()
                            val startDiffTime = (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                            val endDiffTime = (endDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                            val progressRatio = it.currentProgressRate / it.goalProgressRate.toDouble()
                            val inValidData = startDiffTime < 0.0 && endDiffTime >= 0.0 && progressRatio < 1.0
                            inValidData
                        }
                    )

                    planningLessonAdapter.submitList(
                        lessonList.filter {
                            val startDateString = it.startDate
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val startDate = dateFormat.parse(startDateString)
                            val todayDate = Calendar.getInstance()
                            val endDiffTime = (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                            val inValidData = endDiffTime > 0.0
                            inValidData
                        }
                    )

                    passedLessonAdapter.submitList(
                        lessonList.filter {
                            val progressRatio = it.currentProgressRate / it.goalProgressRate
                            val inValidData = (progressRatio == 1)
                            inValidData
                        }
                    )
                }

                binding.rvClassLesson.let {
                    it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
                    it.adapter = concatAdapter
                }
            }
        }
    }

    private fun setTestData() {
        val dummyList = listOf<AllLessonResponse>(
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nScala 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 8,
                goalProgressRate = 8,
                totalNumber = 60,
                price = 50000,
                startDate = "2021-08-19 10:12:14",
                endDate = "2021-11-24 10:12:14"
            ),
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nKotlin 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 0,
                goalProgressRate = 20,
                totalNumber = 50,
                price = 30000,
                startDate = "2021-12-22 10:12:14",
                endDate = "2022-11-11 10:12:14"
            ),
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nC++ 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 0,
                goalProgressRate = 12,
                totalNumber = 22,
                price = 150000,
                startDate = "2022-01-12 10:12:14",
                endDate = "2022-11-24 10:12:14"
            ),
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nPython 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 6,
                goalProgressRate = 8,
                totalNumber = 16,
                price = 25000,
                startDate = "2021-11-10 10:12:14",
                endDate = "2021-12-24 10:12:14"
            ),
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nGolang 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 8,
                goalProgressRate = 8,
                totalNumber = 42,
                price = 55000,
                startDate = "2021-09-19 10:12:14",
                endDate = "2021-10-22 10:12:14"
            ),
            AllLessonResponse(
                lessonName = "프로그래밍 시작하기 : \nRuby 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 4,
                goalProgressRate = 4,
                totalNumber = 40,
                price = 68000,
                startDate = "2021-11-15 10:12:14",
                endDate = "2021-11-30 10:12:14"
            ),
            AllLessonResponse(
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

        val doingLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.DOING) { lesson ->
            moveDetailActivity(lesson)
        }
        val planningLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PLANNING) { lesson ->
            moveDetailActivity(lesson)
        }
        val passedLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PASSED) { lesson ->
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
                    val startDiffTime = (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                    val endDiffTime = (endDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
                    val progressRatio = it.currentProgressRate / it.goalProgressRate.toDouble()
                    val inValidData = startDiffTime < 0.0 && endDiffTime >= 0.0 && progressRatio < 1.0
                    inValidData
                }
            )

            planningLessonAdapter.submitList(
                dummyList.filter {
                    val startDateString = it.startDate
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val startDate = dateFormat.parse(startDateString)
                    val todayDate = Calendar.getInstance()
                    val endDiffTime = (startDate.time - todayDate.time.time) / (60 * 60 * 24 * 1000).toDouble()
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

        binding.rvClassLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }

    override fun initViews() {
        super.initViews()

        binding.ivClassRegister.setOnClickListener {
            startActivity(RegisterLessonFirstActivity.newIntent(requireActivity()))
        }
    }
}