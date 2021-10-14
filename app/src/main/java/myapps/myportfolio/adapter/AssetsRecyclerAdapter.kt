package myapps.myportfolio.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import myapps.myportfolio.R
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share

class AssetsRecyclerAdapter(private val context: Context) :
    RecyclerView.Adapter<AssetsRecyclerAdapter.ViewHolder>() {
    private var assets = mutableListOf<Share>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        val tvValue = itemView.findViewById<TextView>(R.id.tvValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.asset_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asset = assets[holder.adapterPosition]

        holder.tvName.text = asset.name
        holder.tvPrice.text = asset.value.toString()
        holder.tvValue.text = (asset.value * asset.number).toString()
    }

    override fun getItemCount(): Int {
        return assets.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(){
        assets = DataManager.shares
        notifyDataSetChanged()
    }
}