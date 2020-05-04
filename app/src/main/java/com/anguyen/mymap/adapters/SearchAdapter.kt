package com.anguyen.mymap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.OnRecycleViewItemClickListener
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.models.UserSearchListDetail
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_item.view.*

class SearchAdapter(private val mContext: Context,
                    private var mUsers: List<UserSearchListDetail>,
                    private val onItemClickListener: OnRecycleViewItemClickListener
):RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.user_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mUsers.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user = mUsers[i]

        Glide.with(mContext).load(user.avatarUrl).into(holder.imgProfile)

        holder.txtUserName.text = user.username
        holder.txtID.text = "${user.id.substring(0, user.id.length/2)}..."

        holder.btnRequest.onClick {
            onItemClickListener.onRecycleViewItemClickHandler(holder.btnRequest)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgProfile = itemView.image_profile!!
        val txtUserName = itemView.txt_username!!
        val txtID = itemView.txt_id!!
        val btnRequest = itemView.btn_request!!
    }

}