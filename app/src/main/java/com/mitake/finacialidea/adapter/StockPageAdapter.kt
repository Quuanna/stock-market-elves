package com.mitake.finacialidea.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.databinding.ItemStockInfoBinding
import androidx.lifecycle.ViewModel





class StockPageAdapter(
    private var dataArray: ArrayList<StockInfo>, val listener: OnStockClickListener
) :
    RecyclerView.Adapter<StockPageAdapter.ViewHolder>() {

    interface OnStockClickListener {
        fun onStockClick(stockItem: StockInfo, position: Int)
    }

    private var thisPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStockInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataArray[position], position)

        if (position == getThisPosition()) {
            holder.itemView.setBackgroundResource(R.drawable.corners_background_gr)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.corners_background)
        }
    }

    fun getThisPosition(): Int {
        return thisPosition
    }

    fun setThisPosition(thisPosition: Int) {
        this.thisPosition = thisPosition
    }

    fun updateData(list: List<StockInfo>) {
        dataArray.clear()
        dataArray.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataArray.size
    }

    inner class ViewHolder(private val itemBind: ItemStockInfoBinding) :
        RecyclerView.ViewHolder(itemBind.root) {

        fun bind(itemInfo: StockInfo, position: Int) {
            itemBind.tvStockTitle.text = itemInfo.title
            itemBind.tvStockCode.text = itemInfo.code
            itemBind.tvStockPrice.text = itemInfo.price
            itemBind.viewTarget.background =
                itemInfo.target?.let { ContextCompat.getDrawable(itemView.context, it) }
            if (itemInfo.isTarget == true) {
                itemBind.tvMargin.setTextColor(
                    itemView.context.resources.getColor(
                        R.color.red,
                        null
                    )
                )
            } else {
                itemBind.tvMargin.setTextColor(
                    itemView.context.resources.getColor(
                        R.color.green,
                        null
                    )
                )
            }
            itemBind.tvMargin.text = itemInfo.targetPercentage


            itemBind.layoutStock.setOnClickListener {
                notifyDataSetChanged()
                listener.onStockClick(itemInfo, position)
            }
        }
    }
}


