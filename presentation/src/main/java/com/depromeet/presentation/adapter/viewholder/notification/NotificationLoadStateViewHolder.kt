package com.depromeet.presentation.adapter.viewholder.notification

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.databinding.ItemLoadStateBinding


// loadingState 를 관리 해주는 adapter 의 뷰홀더
class NotificationLoadStateViewHolder(
    private val binding: ItemLoadStateBinding,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) = with(binding) {
        pbLoading.isVisible = loadState is LoadState.Loading
        tvNetworkErrorState.isVisible = loadState is LoadState.Error
        tvNetworkErrorRetry.isVisible = loadState is LoadState.Error
        btnRetry.isVisible = loadState is LoadState.Error
    }
}
