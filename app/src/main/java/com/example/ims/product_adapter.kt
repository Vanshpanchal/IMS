package com.example.ims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class product_adapter(private val items: List<product_list>) :
    RecyclerView.Adapter<product_adapter.ProductViewHolder>() {
    lateinit var mylistener: onitemclick
//    lateinit var deletelistener: onitemclick
//    lateinit var editlistener: onitemclick


    interface onitemclick {
        fun itemClickListener(position: Int)
    }

    fun onItem(listener: onitemclick) {
        mylistener = listener
    }

//    fun ondelete(delListener: onitemclick) {
//        deletelistener = delListener
//    }

//    fun onedit(editListener: onitemclick) {
//        editlistener = editListener
//    }

    class ProductViewHolder(
        view: View,
        listener: onitemclick
    ) :
        RecyclerView.ViewHolder(view) {
        val product_title: TextView = view.findViewById(R.id.p_name)
        val inventory_name: TextView = view.findViewById(R.id.inventory_name)
        val quantity_left : TextView = view.findViewById(R.id.qty_remaining)
        val product_no: TextView = view.findViewById(R.id.p_no)

        init {
            view.setOnClickListener {
                listener.itemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): product_adapter.ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_two, parent, false)
        return ProductViewHolder(view, mylistener)
    }

    override fun onBindViewHolder(holder: product_adapter.ProductViewHolder, position: Int) {
        val item = items[position]
        val p_no = position + 1
        holder.product_title.text = item.title
        holder.inventory_name.text = item.id
        holder.quantity_left.text = "Stock: "+item.quantity_left
        holder.product_no.text = p_no.toString() + "."
    }

    override fun getItemCount(): Int {
        return items.size
    }
}