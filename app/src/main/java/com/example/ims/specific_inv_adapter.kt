package com.example.ims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class specific_inv_adapter(private val items: ArrayList<inv_itemsItem>) :
RecyclerView.Adapter<specific_inv_adapter.Inv_ViewHolder>() {
    lateinit var mylistener: specific_inv_adapter.onitemclick

    interface onitemclick {
        fun itemClickListener(position: Int)
    }

    fun onItem(listener: onitemclick) {
        mylistener = listener
    }
    class Inv_ViewHolder(view: View, mylistener: onitemclick) : RecyclerView.ViewHolder(view,){
        val product_title: TextView = view.findViewById(R.id.p_name)
        val inventory_name: TextView = view.findViewById(R.id.inventory_name)
        val quantity_left : TextView = view.findViewById(R.id.qty_remaining)
        val product_no: TextView = view.findViewById(R.id.p_no)

        init {
            view.setOnClickListener {
                mylistener.itemClickListener(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): specific_inv_adapter.Inv_ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_two, parent, false)
        return specific_inv_adapter.Inv_ViewHolder(view, mylistener)
    }

    override fun onBindViewHolder(holder: Inv_ViewHolder, position: Int) {
        val item = items[position]
        val p_no = position + 1
        holder.product_title.text = item.itemName
        holder.inventory_name.text = item.inventoryId
        holder.quantity_left.text = "Stock: "+item.stock.toString()
        holder.product_no.text = p_no.toString() + "."
    }

    override fun getItemCount(): Int {
        return items.size
    }
}







