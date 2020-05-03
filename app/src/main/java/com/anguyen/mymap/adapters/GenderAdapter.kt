package com.anguyen.mymap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.OnRecycleViewItemClickListener
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import kotlinx.android.synthetic.main.hidden_gender_chooser_item.view.*

class GenderAdapter(private val mContext: Context,
                    private var mGenders: List<String>,
                    private val onItemClickListener: OnRecycleViewItemClickListener
):RecyclerView.Adapter<GenderAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.hidden_gender_chooser_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGenders.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val gender = mGenders[i]
        holder.buttonItem.text = gender

        holder.buttonItem.onClick {
            onItemClickListener.onRecycleViewItemClickHandler(holder.buttonItem)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val buttonItem = itemView.btn_gender_item!!
    }

}