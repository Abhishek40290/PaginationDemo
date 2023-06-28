package com.example.paginationdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var arrayList = ArrayList<ModelClass>()
    private var data = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i")
    private var data2 = arrayOf("a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "i2")
    private var loading = true
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var mLayoutManager: LinearLayoutManager? = null
    private var myAdapter: MyAdapter? = null
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        myAdapter = MyAdapter(this, getData())
        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView.adapter = myAdapter
        setUpPagination()
    }

    private fun setUpPagination() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0){
                    visibleItemCount = mLayoutManager!!.childCount
                    totalItemCount = mLayoutManager!!.itemCount
                    pastVisibleItems = mLayoutManager!!.findFirstVisibleItemPosition()
                    if(loading) {
                        if(visibleItemCount + pastVisibleItems >= totalItemCount){
                            loading = false
                            Toast.makeText(this@MainActivity, " This is the last item!", Toast.LENGTH_SHORT).show()
                            getData2()
                            myAdapter!!.notifyDataSetChanged()
                            loading = true
                        }
                    }
                }
            }
        })
    }

    private fun getData2(): ArrayList<ModelClass> {
        for(i in data2.indices) {
            arrayList.add(ModelClass(data2[i]))
        }
        return arrayList
    }

    private fun getData(): ArrayList<ModelClass> {
        for( i in data.indices) {
            arrayList.add(ModelClass(data[i]))
        }
        return arrayList
    }

}
