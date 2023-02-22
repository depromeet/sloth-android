import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.*
import com.depromeet.sloth.presentation.adapter.viewholder.onboarding.*
import com.depromeet.sloth.presentation.screen.onboarding.OnBoardingItemClickListener
import com.depromeet.sloth.presentation.screen.onboarding.OnBoardingUiModel
import com.depromeet.sloth.util.setOnSingleClickListener

class OnBoardingAdapter(
    private val clickListener: OnBoardingItemClickListener
) : ListAdapter<OnBoardingUiModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<OnBoardingUiModel>() {
        override fun areItemsTheSame(oldItem: OnBoardingUiModel, newItem: OnBoardingUiModel): Boolean {
            return (oldItem is OnBoardingUiModel.OnBoardingDoingItem && newItem is OnBoardingUiModel.OnBoardingDoingItem && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId) ||
                    (oldItem is OnBoardingUiModel.OnBoardingFinishedItem && newItem is OnBoardingUiModel.OnBoardingFinishedItem && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId)
        }

        override fun areContentsTheSame(oldItem: OnBoardingUiModel, newItem: OnBoardingUiModel): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_on_boarding_header -> OnBoardingHeaderViewHolder(
                ItemOnBoardingHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_on_boarding_title -> OnBoardingTitleViewHolder(
                ItemOnBoardingTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            R.layout.item_on_boarding_empty -> OnBoardingEmptyItemViewHolder(
                ItemOnBoardingEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            R.layout.item_on_boarding_doing -> OnBoardingDoingItemViewHolder(
                ItemOnBoardingDoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            R.layout.item_on_boarding_finished -> OnBoardingFinishedItemViewHolder(
                ItemOnBoardingFinishedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val uiModel = getItem(position)) {
            is OnBoardingUiModel.OnBoardingHeaderItem -> (holder as OnBoardingHeaderViewHolder).apply {
                bind(uiModel.itemType)
            }

            is OnBoardingUiModel.OnBoardingTitleItem -> (holder as OnBoardingTitleViewHolder).apply {
                bind(uiModel.itemType)
            }

            is OnBoardingUiModel.OnBoardingEmptyItem -> (holder as OnBoardingEmptyItemViewHolder).apply {
                binding.clTodayLesson.setOnSingleClickListener { clickListener.onClick() }
            }

            is OnBoardingUiModel.OnBoardingDoingItem -> (holder as OnBoardingDoingItemViewHolder).apply {
                bind(uiModel.todayLesson)

                binding.btnTodayLessonPlus.setOnSingleClickListener {
                    clickListener.onPlusClick()
                    uiModel.todayLesson.presentNumber++
                    updateDoingLessonProgress(holder, uiModel.todayLesson)
                }
                binding.btnTodayLessonMinus.setOnSingleClickListener {
                    val isOutOfRange = uiModel.todayLesson.presentNumber <= 0
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onMinusClick()
                    uiModel.todayLesson.presentNumber--
                    updateDoingLessonProgress(holder, uiModel.todayLesson)
                }
            }
            is OnBoardingUiModel.OnBoardingFinishedItem -> (holder as OnBoardingFinishedItemViewHolder).apply {
                bind(uiModel.todayLesson)
                binding.btnTodayLessonPlus.setOnSingleClickListener {
                    val isOutOfRange = uiModel.todayLesson.presentNumber >= uiModel.todayLesson.totalNumber
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onPlusClick()
                    updateFinishedLessonProgress(holder, uiModel.todayLesson)
                }
                binding.btnTodayLessonMinus.setOnSingleClickListener {
                    clickListener.onMinusClick()
                    updateFinishedLessonProgress(holder, uiModel.todayLesson)
                }
                binding.clTodayFinishedBottom.setOnSingleClickListener {
                    clickListener.onFinishClick()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OnBoardingUiModel.OnBoardingHeaderItem -> R.layout.item_on_boarding_header
            is OnBoardingUiModel.OnBoardingTitleItem -> R.layout.item_on_boarding_title
            is OnBoardingUiModel.OnBoardingEmptyItem -> R.layout.item_on_boarding_empty
            is OnBoardingUiModel.OnBoardingDoingItem -> R.layout.item_on_boarding_doing
            is OnBoardingUiModel.OnBoardingFinishedItem -> R.layout.item_on_boarding_finished
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    private fun updateDoingLessonProgress(holder: OnBoardingDoingItemViewHolder, todayLesson: TodayLessonResponse) {
        holder.binding.apply {
            val animation = ObjectAnimator.ofInt(
                holder.binding.pbTodayLessonBar,
                "progress",
                holder.binding.pbTodayLessonBar.progress, todayLesson.presentNumber * 1000
            )
            animation.apply {
                duration = DELAY_TIME
            }.start()
            tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
        }
    }

    private fun updateFinishedLessonProgress(holder: OnBoardingFinishedItemViewHolder, todayLesson: TodayLessonResponse) {
        holder.binding.apply {
            tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
        }
    }

    companion object {
        const val DELAY_TIME = 350L
    }
}
