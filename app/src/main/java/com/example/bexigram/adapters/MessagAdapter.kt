package com.example.bexigram.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bexigram.databinding.ItemFromBinding
import com.example.bexigram.databinding.ToItemBinding
import com.example.bexigram.models.Message
class MessageAdapter(
    private val rvAction: RvAction,
    private val list: ArrayList<Message>,
    private val currentUserUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_FROM = 1
    private val TYPE_TO = 0

    // ViewHolder for sent messages (from the current user)
    inner class FromVh(private val itemRvBinding: ItemFromBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(message: Message) {
            // Show the message text if it's not null or empty
            itemRvBinding.tvSms.text = message.text
            itemRvBinding.tvDateTime.text = message.date


            // Handle click action on the message
            itemRvBinding.root.setOnClickListener {
                rvAction.imageClick(message)
            }
        }
    }

    // ViewHolder for received messages (sent by the other user)
    inner class ToVh(private val itemRvBindin: ToItemBinding) : RecyclerView.ViewHolder(itemRvBindin.root) {
        fun onBind(message: Message) {
            // Show the message text if it's not null or empty
           itemRvBindin.tvSms.text = message.text
            itemRvBindin.tvDateTime.text = message.date
            // Handle click action on the message
            itemRvBindin.root.setOnClickListener {
                rvAction.imageClick(message)
            }
        }
    }

    // Create appropriate ViewHolder depending on whether the message is sent or received
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TO -> ToVh(ToItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_FROM -> FromVh(ItemFromBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    // Get the total number of messages
    override fun getItemCount(): Int {
        return list.size
    }

    // Bind data to the appropriate ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ToVh -> holder.onBind(list[position])
            is FromVh -> holder.onBind(list[position])
        }
    }

    // Determine if the message is sent by the current user or the other user
    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromUserUid == currentUserUid) {
            TYPE_FROM // Message sent by the current user
        } else {
            TYPE_TO // Message sent to the current user
        }
    }

    interface RvAction {
        fun imageClick(message: Message)
    }
}
