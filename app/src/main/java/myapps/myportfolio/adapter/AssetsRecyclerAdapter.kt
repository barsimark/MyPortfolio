package myapps.myportfolio.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import myapps.myportfolio.R
import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share
import myapps.myportfolio.touch.AssetsTouchHelperAdapter
import kotlin.math.round

class AssetsRecyclerAdapter(private val context: Context) :
    RecyclerView.Adapter<AssetsRecyclerAdapter.ViewHolder>(), AssetsTouchHelperAdapter {
    private var assets = mutableListOf<Share>()

    interface AssetDeleter{
        fun shareDeleted(share: Share)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.findViewById<TextView>(R.id.tvName)
        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        var tvValue = itemView.findViewById<TextView>(R.id.tvValue)
        var tvNumber = itemView.findViewById<TextView>(R.id.tvNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.asset_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asset = assets[holder.adapterPosition]

        holder.tvName.text = asset.name
        holder.tvPrice.text = String.format("%.4f (%.4f%%)", asset.value, (asset.value / asset.buyprice) - 1.0)
        holder.tvValue.text = String.format("%.4f", asset.value * asset.number)
        holder.tvNumber.text = String.format("%.4f", asset.number)
    }

    override fun getItemCount(): Int {
        return assets.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(){
        assets = DataManager.shares
        notifyDataSetChanged()
    }

    override fun onItemDismissed(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete item")
        builder.setMessage("Are you sure?")
        builder.setPositiveButton("Yes"){ dialog, which ->
            (context as AssetDeleter).shareDeleted(assets[position])
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){ dialog, which ->
            refresh()
            dialog.dismiss()
        }
        builder.show()
    }
}