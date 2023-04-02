package com.example.fermiontask.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fermiontask.R
import com.example.fermiontask.model.ProductColorsModel

class ProductColorsAdapter(
    private val context: Context,
    private val colorsList: ArrayList<ProductColorsModel>,
    private val productColorsAdapterClickListener: ProductColorsAdapterClickListener,
    val isFromProductsListing: Boolean
) : RecyclerView.Adapter<ProductColorsAdapter.ProductColorsHolder>() {

    interface ProductColorsAdapterClickListener {
        fun productColorsAdapterClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductColorsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_colors_rec_layout, parent, false)
        return ProductColorsHolder(view)
    }

    override fun onBindViewHolder(holder: ProductColorsHolder, position: Int) {
        val productColorModel = colorsList[position]
        if (isFromProductsListing) {
            holder.product_colors_rec_view.layoutParams.height = 30
            holder.product_colors_rec_view.layoutParams.width = 30

            holder.product_colors_rec_name_tv.visibility = View.GONE
        }
//        holder.product_colors_rec_view.setBackgroundColor(Color.parseColor(productColorModel.colorCode))
        holder.product_colors_rec_view.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(productColorModel.colorCode))

        holder.product_colors_rec_name_tv.text = productColorModel.colorName
    }

    override fun getItemCount(): Int {
        return colorsList.size
    }

    inner class ProductColorsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_colors_rec_view: View = itemView.findViewById(R.id.product_colors_rec_view)
        val product_colors_rec_name_tv: TextView =
            itemView.findViewById(R.id.product_colors_rec_name_tv)

        init {
            itemView.setOnClickListener {
                productColorsAdapterClickListener.productColorsAdapterClick(adapterPosition)
            }
        }
    }
}