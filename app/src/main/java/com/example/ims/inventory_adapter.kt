package com.example.ims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class inventory_adapter(private val items: List<inventory_list>): RecyclerView.Adapter<inventory_adapter.InventoryViewHolder>()  {
    lateinit var mylistener: onitemclick


    interface onitemclick {
        fun itemClickListener(position: Int)
    }

    fun onItem(listener: inventory_adapter.onitemclick) {
        mylistener = listener
    }


    class InventoryViewHolder(
        view: View,
        listener: inventory_adapter.onitemclick
    ) : RecyclerView.ViewHolder(view){
        val inv_name: TextView = view.findViewById(R.id.inventory1_name)
        val id_no: TextView = view.findViewById(R.id.p_no)
        val created_date : TextView = view.findViewById(R.id.created_date)

        init {
            view.setOnClickListener {
                listener.itemClickListener(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: inventory_adapter.InventoryViewHolder, position: Int) {
       val item = items[position]
        val sr_no= position+1
        holder.inv_name.text = item.inventoryName
        holder.created_date.text = item.created_date
        holder.id_no.text = sr_no.toString()

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_three, parent, false)
        return inventory_adapter.InventoryViewHolder(view, mylistener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}