package com.anguyen.mymap.adapters.gender

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.commons.onClick
import kotlinx.android.synthetic.main.gender_bottom_dialog_item.view.*

class GenderAdapter(private val mContext: Context,
                    private var mGenders: List<String>,
                    private val onItemClickListener: OnGendersItemsClickListener
):RecyclerView.Adapter<GenderAdapter.ViewHolder>(){

    private var lastChecked: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.gender_bottom_dialog_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mGenders.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val gender = mGenders[i]
        holder.txtItem.text = gender

        holder.layoutItem.onClick {
            holder.rdItem.isChecked = true

            if(lastChecked != null){
                lastChecked?.isChecked = false
            }

            lastChecked = holder.rdItem

            onItemClickListener.onRecycleViewItemClickHandler(
                holder.rdItem,
                holder.txtItem.text.toString(),
                i
            )
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val layoutItem = itemView.layout_item!!
        val rdItem = itemView.rd_item!!
        val txtItem = itemView.txt_gender_item!!
    }

}