package com.depromeet.sloth.util.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.entity.RegisterEntity
import com.depromeet.sloth.databinding.RegisterInputItemBinding

//adapter 재활용 대비 재활용 가능 어댑터 구현해야되나
class RegisterAdapter: RecyclerView.Adapter<RegisterAdapter.RegisterViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<RegisterEntity>(){
        override fun areItemsTheSame(oldItem: RegisterEntity, newItem: RegisterEntity): Boolean {
            return oldItem.titleText == newItem.titleText
        }

        override fun areContentsTheSame(oldItem: RegisterEntity, newItem: RegisterEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterViewHolder {
        val binding = RegisterInputItemBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return RegisterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegisterViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class RegisterViewHolder(val binding: RegisterInputItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(registerEntity: RegisterEntity){
            binding.apply {
                categoryTextView.text = registerEntity.titleText
                inputEditText.hint = registerEntity.inputText
            }
        }
    }
}