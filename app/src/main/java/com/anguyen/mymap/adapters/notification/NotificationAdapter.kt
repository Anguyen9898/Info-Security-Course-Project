package com.anguyen.mymap.adapters.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.models.NotificationDetail
import kotlinx.android.synthetic.main.notify_item.view.*

class NotificationAdapter(private val mContext: Context,
                          private var mNotifications: List<NotificationDetail>,
                          private val onItemClickListener: OnNotificationItemsClickListener
):RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.notify_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mNotifications.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val notificationDetail = mNotifications[i]

        //Glide.with(mContext).load(notificationDetail.senderAvatarUrl).into(holder.imgProfile)

        //holder.txtContent.text = notificationDetail.content

        holder.contentLayout.onClick {
            onItemClickListener.onClick(holder.buttonLayout)
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgProfile = itemView.image_profile!!
        val txtContent = itemView.txt_content!!
        val contentLayout = itemView.layout_content!!
        val buttonLayout = itemView.layout_button!!

    }

}