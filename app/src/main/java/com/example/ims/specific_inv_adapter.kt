package com.example.ims

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class specific_inv_adapter(private val items: ArrayList<inv_itemsItem>) :
    RecyclerView.Adapter<specific_inv_adapter.Inv_ViewHolder>() {
    lateinit var mylistener: onitemclick
    lateinit var mylistener_1: OnItemLongClickListener
    lateinit var editlistener: EditClick
    lateinit var fs: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    interface onitemclick {
        fun itemClickListener(position: Int)
    }

    fun onItem(listener: onitemclick) {
        mylistener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int): Boolean
    }

    fun onItem_1(listener: OnItemLongClickListener) {
        mylistener_1 = listener
    }

    interface EditClick {
        fun onEditClick(position: Int)
    }

    fun onEdit(listener: EditClick) {
        editlistener = listener
    }

    class Inv_ViewHolder(
        view: View,
        mylistener: onitemclick,
        mylistener_1: OnItemLongClickListener,
        editlistener: EditClick
    ) : RecyclerView.ViewHolder(view) {
        val product_title: TextView = view.findViewById(R.id.p_name)
        val inventory_name: TextView = view.findViewById(R.id.inventory_name)
        val quantity_left: TextView = view.findViewById(R.id.qty_remaining)
        val product_no: TextView = view.findViewById(R.id.p_no)
        val edit: ImageButton = view.findViewById(R.id.edit_btn)

        init {
            view.setOnClickListener {
                mylistener.itemClickListener(adapterPosition)
            }
            view.setOnLongClickListener {

                mylistener_1.onItemLongClick(adapterPosition)
            }
            edit.setOnClickListener {
                editlistener.onEditClick(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): specific_inv_adapter.Inv_ViewHolder {
        fs = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_two, parent, false)
        return specific_inv_adapter.Inv_ViewHolder(view, mylistener, mylistener_1, editlistener)
    }

    override fun onBindViewHolder(holder: Inv_ViewHolder, position: Int) {
        val item = items[position]
        val p_no = position + 1
        holder.product_title.text = item.ItemName
        holder.inventory_name.text = item.Category
        holder.quantity_left.text = "#" + item.Stock.toString()
        holder.product_no.text = p_no.toString() + "."

    }

    override fun getItemCount(): Int {
        return items.size
    }
}







