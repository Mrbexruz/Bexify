package com.example.bexigram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.bexigram.databinding.ItemRvBinding
import com.example.bexigram.models.User
import com.squareup.picasso.Picasso


class UserAdapter(val rvAction: RvAction,private val list: ArrayList<User>): RecyclerView.Adapter<UserAdapter.Vh>() {
    inner class Vh(val itemRvBinding: ItemRvBinding): ViewHolder(itemRvBinding.root){
        fun onBind(user: User){
            itemRvBinding.name.text = user.name
            Picasso.get().load(user.photoUrl).into(itemRvBinding.image)
            itemRvBinding.root.setOnClickListener {
                rvAction.itemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun getItemCount(): Int =list.size


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }
    interface RvAction{
        fun itemClick(user: User)
    }
}