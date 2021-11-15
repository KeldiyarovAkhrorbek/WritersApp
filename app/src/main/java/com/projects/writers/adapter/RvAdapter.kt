package com.projects.writers.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.projects.writers.R
import com.projects.writers.databinding.ItemWriterBinding
import com.projects.writers.models.Writer
import com.squareup.picasso.Picasso

class RvAdapter(var list: List<Writer>, var listener: OnItemClick) :
    RecyclerView.Adapter<RvAdapter.MyHolder>() {

    inner class MyHolder(var itemWriterBinding: ItemWriterBinding) :
        RecyclerView.ViewHolder(itemWriterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(writer: Writer, position: Int) {
            itemWriterBinding.apply {
                name.text = writer.name
                Picasso.get().load(writer.photoUrl).resize(200, 200)
                    .placeholder(R.drawable.placeholder1).into(image)
                yearTv.text = "${writer.birthYear}-${writer.deathYear}"
                itemView.setOnClickListener {
                    listener.onItemClick(writer, position)
                }
                save.setOnClickListener {
                    listener.onItemSaveClick(writer, position)
                }

                if (writer.saved) {
                    save.setCardBackgroundColor(Color.parseColor("#00B238"))
                } else {
                    save.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemWriterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size


    interface OnItemClick {
        fun onItemClick(writer: Writer, position: Int)
        fun onItemSaveClick(writer: Writer, position: Int)
    }
}