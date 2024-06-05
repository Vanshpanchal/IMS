package com.example.ims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class product_adapter(private val items: List<inv_itemsItem>) :
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
        val quantity_left: TextView = view.findViewById(R.id.qty_remaining)
        val product_no: TextView = view.findViewById(R.id.p_no)
        val edit_btn: ImageButton = view.findViewById(R.id.edit_btn)

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
        holder.product_title.text = item.ItemName
        holder.inventory_name.text = item.Category
        holder.quantity_left.text = "#" + item.Stock.toString()
        holder.product_no.text = p_no.toString() + "."
        holder.edit_btn.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return items.size
    }
}