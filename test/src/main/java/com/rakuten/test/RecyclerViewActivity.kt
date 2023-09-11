package com.rakuten.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.view.canHaveTooltip

//import com.rakuten.tech.mobile.inappmessaging.runtime.extensions.canHaveTooltip

class RecyclerViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<RecyclerModel>()
        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(RecyclerModel(R.drawable.ic_launcher_background, "Item $i"))
        }
        recyclerview.adapter = CustomAdapter(data)
    }

    override fun onResume() {
        super.onResume()
        InAppMessaging.instance().registerMessageDisplayActivity(this)
    }

    override fun onPause() {
        InAppMessaging.instance().unregisterMessageDisplayActivity()
        super.onPause()
    }
}

data class RecyclerModel(val image: Int, val text: String)

class CustomAdapter(private val mList: List<RecyclerModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]
        holder.imageView.setImageResource(model.image)
        holder.imageView.canHaveTooltip(if (position == 3) "navigation_stadium" else "image$position")
        holder.textView.text = model.text
    }

    override fun getItemCount() = mList.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recycler_image)
        val textView: TextView = itemView.findViewById(R.id.recycler_text)
    }
}