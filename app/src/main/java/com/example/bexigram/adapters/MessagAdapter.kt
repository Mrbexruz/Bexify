package com.example.bexigram.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.bexigram.R
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
            itemRvBinding.tvSms.text = message.text
            itemRvBinding.tvDateTime.text = message.date

            // Show edit/delete options when the user long-clicks on their message
            itemRvBinding.root.setOnLongClickListener {
                showPopupMenu(message, it)
                true
            }
        }

        private fun showPopupMenu(message: Message, view: View) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.message_options_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        rvAction.onEdit(message) // Trigger the edit action
                        true
                    }
                    R.id.menu_delete -> {
                        rvAction.onDelete(message) // Trigger the delete action
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    // ViewHolder for received messages (sent by the other user)
    inner class ToVh(private val itemRvBinding: ToItemBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(message: Message) {
            itemRvBinding.tvSms.text = message.text
            itemRvBinding.tvDateTime.text = message.date
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
        fun onEdit(message: Message) // Edit action
        fun onDelete(message: Message) // Delete action
    }
}
