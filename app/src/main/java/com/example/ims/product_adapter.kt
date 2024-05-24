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
    lateinit var deletelistener: onitemclick

    interface onitemclick {
        fun itemClickListener(position: Int)
    }

    fun onItem(listener: onitemclick) {
        mylistener = listener
    }

    fun ondelete(delListener: onitemclick) {
        deletelistener = delListener
    }

    class ProductViewHolder(view: View, listener: onitemclick, deletelistener: onitemclick) :
        RecyclerView.ViewHolder(view) {
        val product_title: TextView = view.findViewById(R.id.p_name)
        val product_price: TextView = view.findViewById(R.id.p_price)
        val product_no: TextView = view.findViewById(R.id.p_no)
        val del: ImageButton = view.findViewById(R.id.delete)

        init {
            view.setOnClickListener {
                listener.itemClickListener(adapterPosition)
            }
            view.findViewById<ImageButton>(R.id.delete).setOnClickListener {
                deletelistener.itemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): product_adapter.ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_one, parent, false)
        return ProductViewHolder(view, mylistener, deletelistener)
    }

    override fun onBindViewHolder(holder: product_adapter.ProductViewHolder, position: Int) {
        val item = items[position]
        val p_no = position + 1
        holder.product_title.text = item.title
        holder.product_price.text = item.price
        holder.product_no.text = p_no.toString() + "."
    }

    override fun getItemCount(): Int {
        return items.size
    }
}