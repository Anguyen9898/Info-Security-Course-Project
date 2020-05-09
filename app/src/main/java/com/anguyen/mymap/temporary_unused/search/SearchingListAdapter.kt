package com.anguyen.mymap.temporary_unused.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.STATUS_WAITING_RESPOND
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.models.UserRespondDetail
import com.anguyen.mymap.temporary_unused.SearchPresenter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_list_item.view.*

class SearchingListAdapter(private val mContext: Context,
                           private var mUsers: List<UserRespondDetail>,
                           private val onItemClickListener: OnSearchItemsClickListener,
                           private val mPresenter: SearchPresenter

):RecyclerView.Adapter<SearchingListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.user_list_item,
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

        //holder.txtHiddenUserId.text = user.id
        val strID = "${user.id.substring(0, user.id.length/2)}..."
        holder.txtID.text = strID

        setupRequestButton(user.id, holder)

        holder.btnRequest.onClick { //Send Request
            holder.btnRequest.visibility = View.INVISIBLE
            holder.btnFollowing.visibility = View.INVISIBLE

            holder.btnWaiting.visibility = View.VISIBLE

            onItemClickListener.onSendRequestClickHandler(user.id, STATUS_WAITING_RESPOND)
        }

        holder.btnWaiting.onClick { //Cancel Request
//            holder.btnWaiting.visibility = View.INVISIBLE
//            holder.btnFollowing.visibility = View.INVISIBLE
//
//            holder.btnRequest.visibility = View.VISIBLE
//
//            onItemClickListener.onWaitingClickHandler(STATUS_CANCEL_REQUEST)
        }

        holder.btnFollowing.onClick { //Revoke Following
            //onItemClickListener.onFollowingClickHandler(STATUS_REVOKE_FOLLOW)
        }
    }

    private fun setupRequestButton(hiddenId: String, holder: ViewHolder){
        mPresenter.checkNotificationStatus { receiverId ->
            if(receiverId == hiddenId){
                holder.btnRequest.visibility = View.INVISIBLE
                holder.btnFollowing.visibility = View.INVISIBLE

                holder.btnWaiting.visibility = View.VISIBLE
            }else{
                holder.btnWaiting.visibility = View.INVISIBLE
                holder.btnFollowing.visibility = View.INVISIBLE

                holder.btnRequest.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgProfile = itemView.image_profile!!
        val txtHiddenUserId = itemView.txt_user_id_hidden!!
        val txtUserName = itemView.txt_username!!
        val txtID = itemView.txt_id!!
        val btnRequest = itemView.btn_send_request!!
        val btnWaiting = itemView.btn_waiting_respond!!
        val btnFollowing = itemView.btn_following!!
    }

}