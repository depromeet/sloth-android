package com.depromeet.presentation.ui.manage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentManageBinding
import com.depromeet.presentation.databinding.ManageCalendarDayBinding
import com.depromeet.presentation.databinding.ManageCalendarHeaderBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.util.displayText
import com.depromeet.presentation.util.makeInVisible
import com.depromeet.presentation.util.makeVisible
import com.depromeet.presentation.util.setOnMenuItemSingleClickListener
import com.depromeet.presentation.util.setTextColorRes
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


data class Event(val id: String, val text: String, val date: LocalDate)

@SuppressLint("NewApi")
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

    private var selectedDate: LocalDate? = null

    private val today = LocalDate.now()

    //TODO 한글로 텍스트를 변환
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()

    override fun onStart() {
        super.onStart()
        viewModel.fetchUserProfile()
        viewModel.fetchLessonStatisticsInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        // initViews()

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)
        val endMonth = currentMonth.plusMonths(50)
        configureBinders(daysOfWeek)

        binding.manageCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.manageCalendar.scrollToMonth(currentMonth)

        binding.manageCalendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            // Show today's events initially.
            binding.manageCalendar.post { selectDate(today) }
        }

        binding.manageCalendar.monthScrollListener = { month ->
            binding.manageCalendarHeaderMonthYearText.text = month.yearMonth.displayText()

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.manageCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.manageCalendarHeaderRightArrow.setOnClickListener {
            binding.manageCalendar.findFirstVisibleMonth()?.let {
                binding.manageCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.manageCalendarHeaderLeftArrow.setOnClickListener {
            binding.manageCalendar.findFirstVisibleMonth()?.let {
                binding.manageCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        initListener()
        initObserver()
    }

    @SuppressLint("NewApi")
    override fun initViews() {
        super.initViews()

    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.manageCalendar.notifyDateChanged(it) }
            binding.manageCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

//    private fun updateAdapterForDate(date: LocalDate) {
//        eventsAdapter.apply {
//            events.clear()
//            events.addAll(this@Example3Fragment.events[date].orEmpty())
//            notifyDataSetChanged()
//        }
//        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
//    }

    //    private fun saveEvent(text: String) {
//        if (text.isBlank()) {
//            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG)
//                .show()
//        } else {
//            selectedDate?.let {
//                events[it] =
//                    events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
//                updateAdapterForDate(it)
//            }
//        }
//    }
//
//    private fun deleteEvent(event: Event) {
//        val date = event.date
//        events[date] = events[date].orEmpty().minus(event)
//        updateAdapterForDate(date)
//    }
//
    private fun updateAdapterForDate(date: LocalDate?) {
//        eventsAdapter.apply {
//            events.clear()
//            events.addAll(this@Example3Fragment.events[date].orEmpty())
//            notifyDataSetChanged()
//        }
        binding.manageCalendarSelectedDateText.text = selectionFormatter.format(date)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = ManageCalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.manageCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.calendarDayText
                val dotView = container.binding.calendarDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.makeVisible()
                    when (data.date) {
                        today -> {
                            textView.setTextColorRes(R.color.calendar_white)
                            textView.setBackgroundResource(R.drawable.calendar_today_bg)
                            dotView.makeInVisible()
                        }

                        selectedDate -> {
                            textView.setTextColorRes(R.color.calendar_blue)
                            textView.setBackgroundResource(R.drawable.calendar_selected_bg)
                            dotView.makeInVisible()
                        }

                        else -> {
                            textView.setTextColorRes(R.color.calendar_black)
                            textView.background = null
                            dotView.isVisible = events[data.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = ManageCalendarHeaderBinding.bind(view).legendLayout.container
        }
        binding.manageCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.first().toString()
                                tv.setTextColorRes(R.color.calendar_black)
                            }
                    }
                }
            }
    }

    private fun initListener() {
        binding.tbManage.setOnMenuItemSingleClickListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    viewModel.navigateToSetting()
                    true
                }

                else -> false
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToUpdateUserProfileDialogEvent.collect {
                    showUpdateUserProfileDialog()
                }
            }

            launch {
                viewModel.navigateToSettingEvent.collect {
                    val action = ManageFragmentDirections.actionManageToSetting()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.navigateToExpireDialogEvent.collect {
                    showExpireDialog()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showUpdateUserProfileDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateUserProfileDialog(
            viewModel.uiState.value.userName
        )
        findNavController().safeNavigate(action)
    }
}