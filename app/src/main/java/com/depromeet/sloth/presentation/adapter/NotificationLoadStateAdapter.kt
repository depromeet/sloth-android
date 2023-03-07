package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.depromeet.sloth.databinding.ItemLoadStateBinding
import com.depromeet.sloth.presentation.adapter.viewholder.notification.NotificationLoadStateViewHolder

class NotificationLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<NotificationLoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): NotificationLoadStateViewHolder {
        return NotificationLoadStateViewHolder(
            ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )
    }

    override fun onBindViewHolder(holder: NotificationLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}