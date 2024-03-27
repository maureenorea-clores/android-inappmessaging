package com.rakuten.test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.AppStartEvent

class ThirdFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerview = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerview.layoutManager = LinearLayoutManager(this.context)

        val data = ArrayList<RecyclerModel>()
        for (i in 1..20) {
            data.add(RecyclerModel(R.drawable.ic_launcher_background, "Item $i"))
        }
        recyclerview.adapter = CustomAdapter(data)
    }
}

data class RecyclerModel(val image: Int, val text: String)

class CustomAdapter(private val mList: List<RecyclerModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("Bind $position")
        val model = mList[position]
        holder.imageView.setImageResource(model.image)
//        holder.imageView.canHaveTooltip(if (position == 3) "close_message" else "image$position")
        holder.imageView.tag = if (position == 7) "close_message" else "image$position"
        holder.textView.text = model.text
//        holder.setIsRecyclable(false)
        InAppMessaging.instance().logEvent(AppStartEvent())
    }

    override fun getItemCount() = mList.size

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recycler_image)
        val textView: TextView = itemView.findViewById(R.id.recycler_text)
    }
}