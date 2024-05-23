package com.example.ims

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.ArrayList

class list_view_adapter(context: Context, private val resource: Int, private val num: Array<String>, private val msg: Array<String>,private val res_id : Array<Int>) :
    ArrayAdapter<String>(context, resource, msg) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val ls_num: TextView = view.findViewById(R.id.ls_num)
        val ls_msg: TextView = view.findViewById(R.id.ls_msg)

        val imageView: ImageView = view.findViewById(R.id.img)

        ls_msg.text = msg[position]
        ls_num.text = num[position]
        imageView.setImageResource(res_id[position])
//        imageView.setImageResource(R.drawable.ic_launcher_foreground) // Replace with actual image resource or logic

        return view
    }
}

