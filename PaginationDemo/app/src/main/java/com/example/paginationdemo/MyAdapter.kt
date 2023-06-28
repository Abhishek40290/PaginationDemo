package com.example.paginationdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class MyAdapter(context: MainActivity, data: ArrayList<ModelClass>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val context: Context
    private val data: ArrayList<ModelClass>

    init {
        this.context = context
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = data[position]
        val textView = holder.itemView.findViewById<TextView>(R.id.textView)
            textView.text= model.name
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val rowItem: ConstraintLayout = itemView.findViewById(R.id.rowItem)

        init {
             rowItem.setOnClickListener{
                Toast.makeText(context, data[adapterPosition].toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

