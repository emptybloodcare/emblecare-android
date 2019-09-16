package org.sopt.wjdma.emblecare.measure

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_item_measure_outcome.view.*
import org.sopt.wjdma.emblecare.R
import org.sopt.wjdma.emblecare.network.Get.MeasureListData

class MeasureOutcomeRecyclerViewAdapter(val ctx: Context, var dataList: ArrayList<MeasureListData>) : RecyclerView.Adapter<MeasureOutcomeRecyclerViewAdapter.Holder>(){
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_measure_outcome,viewGroup,false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.hemo.text = dataList[position].hb.toString()+"g/dL"
        if(dataList[position].period==0){ //false일 때,
            Glide.with(ctx)
                    .load(R.drawable.icon_period_off)
                    .into(holder.period)
        } else{ //true일 때,
            Glide.with(ctx)
                    .load(R.drawable.icon_period_on)
                    .into(holder.period)
        }
        holder.date.text = dataList[position].date
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var hemo = itemView.findViewById(R.id.tv_rv_item_measure_outcome_hemo) as TextView
        var period = itemView.findViewById(R.id.iv_rv_item_measure_outcome_period) as ImageView
        var date = itemView.findViewById(R.id.tv_rv_item_measure_outcome_date) as TextView
    }
}