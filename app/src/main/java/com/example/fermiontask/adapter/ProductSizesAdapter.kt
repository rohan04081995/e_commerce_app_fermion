package com.example.fermiontask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fermiontask.R

class ProductSizesAdapter(
    private val context: Context,
    private val sizesList: ArrayList<String>,
    private val productSizesAdapterClickListenerClickListener: ProductSizesAdapterClickListener
) : RecyclerView.Adapter<ProductSizesAdapter.ProductColorsHolder>() {

    interface ProductSizesAdapterClickListener {
        fun productSizesAdapterClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductColorsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_sizes_rec_layout, parent, false)
        return ProductColorsHolder(view)
    }

    override fun onBindViewHolder(holder: ProductColorsHolder, position: Int) {
        holder.product_sizes_rec_name_tv.text = sizesList[position]

    }

    override fun getItemCount(): Int {
        return sizesList.size
    }

    inner class ProductColorsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_sizes_rec_name_tv: TextView =
            itemView.findViewById(R.id.product_sizes_rec_name_tv)

        init {
            itemView.setOnClickListener {
                productSizesAdapterClickListenerClickListener.productSizesAdapterClick(
                    adapterPosition
                )
            }
        }
    }
}