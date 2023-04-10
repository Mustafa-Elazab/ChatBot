package com.example.besafe.presentation.fragment.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.besafe.R
import com.example.besafe.data.remote.dto.Message
import com.example.besafe.databinding.ChatMeItemBinding
import com.example.besafe.databinding.ChatOtherItemBinding
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(
    private var listener: OnItemChatClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages: ArrayList<Message> = ArrayList()

    var isClicked: Boolean = false

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
                    .inflate(R.layout.chat_other_item, parent, false)
                ChatOtherMessageViewHolder(otherMessageView)
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
            is ChatMeMessageViewHolder -> {
                holder.bind(messages[position])
            }
            is ChatOtherMessageViewHolder -> {
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

            binding.tvMeChatMessage.text = item.content
            binding.tvMeChatMessageTime.text = getCurrentTime()

        }

    }

    // text message and file name for other 1
    inner class ChatOtherMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatOtherItemBinding.bind(itemView)
        fun bind(item: Message) = with(itemView) {
            if (item.content?.isNotEmpty() == true) {
                binding.tvOtherChatMessage.text = "${item.content}"
                Log.d("TAG", "bind: $isClicked")

                binding.apply {
                    imgCopy.setOnClickListener {
                        listener.contentCopy(item)
                    }
                    imgShare.setOnClickListener {
                        listener.contentShare(item)
                    }
                    imgListen.setOnClickListener {
                        listener.contentListen(item)
                        if (isClicked) {
                            binding.imgListen.background.setTint(resources.getColor(R.color.dt_deepblue))
                        }else{
                            binding.imgListen.background.setTint(resources.getColor(R.color.light_blue))
                        }
                    }
                }


            } else {
                binding.tvOtherChatMessage.text = "No Response"
            }
            binding.tvOtherChatMessageTime.text = getCurrentTime()
        }

    }


    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val inputDateFormat = SimpleDateFormat("HH:mm:ss")
        val date = inputDateFormat.parse(inputDateFormat.format(Date()))
        val outputDateFormat = SimpleDateFormat("hh:mm a")
        return outputDateFormat.format(date)
    }
}

interface OnItemChatClickListener {
    fun contentShare(message: Message)
    fun contentListen(message: Message)
    fun contentCopy(message: Message)
}
