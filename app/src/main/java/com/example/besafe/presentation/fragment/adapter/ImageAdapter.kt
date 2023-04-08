package com.example.besafe.presentation.fragment.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.besafe.R
import com.example.besafe.data.remote.dto.Message
import com.example.besafe.databinding.ChatMeItemBinding
import com.example.besafe.databinding.ImageOtherItemBinding
import java.text.SimpleDateFormat
import java.util.*


class ImageAdapter(
    private var listener: OnItemImageClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: ArrayList<Message> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            0 -> {
                //text,file and me
                val myMessageView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_me_item, parent, false)
                ChatMeMessageViewHolder(myMessageView)
            }
            1 -> {
                //text, file  and other
                val otherMessageView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_other_item, parent, false)
                ImageOtherMessageViewHolder(otherMessageView)
            }

            else -> {
                val myMessageView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_me_item, parent, false)
                ChatMeMessageViewHolder(myMessageView)
            }
        }
    }


    fun addMessage(message: Message) {
        messages.add(0, message)
        notifyItemInserted(0)
        notifyItemRangeChanged(0, messages.size, messages)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ImageAdapter.ChatMeMessageViewHolder -> {
                holder.bind(messages[position])
            }
            is ImageAdapter.ImageOtherMessageViewHolder -> {
                holder.bind(messages[position])
            }
        }


    }

    override fun getItemCount(): Int {
        return messages.size
    }


    override fun getItemViewType(position: Int): Int {

        if (messages[position].role.equals("user")) {
            return 0
        } else {
            return 1
        }
        return 0
    }


    // text message and file name for me 0
    inner class ChatMeMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatMeItemBinding.bind(itemView)
        fun bind(item: Message) = with(itemView) {
//
            binding.tvMeChatMessage.text = item.content
            binding.tvMeChatMessageTime.text = "${getCurrentTime()}"

        }

    }

    // text message and file name for other 1
    inner class ImageOtherMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val binding = ImageOtherItemBinding.bind(itemView)
        fun bind(item: Message) = with(itemView) {

            if (item.content != null) {
                Glide.with(this).load(item.content).into(binding.imgOtherImage)
            }
            binding.imgDownload.setOnClickListener {
                listener.contentDownload(item)
            }
            binding.imgShare.setOnClickListener {
                listener.contentShare(item)
            }

        }

    }


    fun getCurrentTime(): String {
        val inputDateFormat = SimpleDateFormat("HH:mm:ss")
        val date = inputDateFormat.parse(inputDateFormat.format(Date()))
        val outputDateFormat = SimpleDateFormat("hh:mm a")
        return outputDateFormat.format(date)
    }


}

interface OnItemImageClickListener {
    fun contentShare(message: Message)
    fun contentDownload(message: Message)
}
