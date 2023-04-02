package com.example.fermiontask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fermiontask.R
import com.example.fermiontask.model.ProductModel

class ProductAdapter(
    private val context: Context,
    private val productsList: ArrayList<ProductModel>,
    private val productAdapterClickListener: ProductAdapterClickListener
) :
    RecyclerView.Adapter<ProductAdapter.ProductHolder>(),
    ProductColorsAdapter.ProductColorsAdapterClickListener {

    interface ProductAdapterClickListener {
        fun productAdapterClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.product_rec_layout, parent, false)
        return ProductHolder(view)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        var productModel = productsList[position]
        Glide.with(context)
            .load(productModel.productImage)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.product_rec_iv)

        holder.product_name_rec_tv.text = productModel.productName
        holder.product_price_rec_tv.text = "₹${productModel.price}"

        if (productModel.sizesList.isNotEmpty()) {
            val size = "${productModel.sizesList.size} sizes available"
            holder.product_sizes_rec_tv.text = size
            holder.product_sizes_rec_tv.visibility = View.VISIBLE
        } else {
            holder.product_sizes_rec_tv.visibility = View.GONE
        }
        if (productModel.productColorsList.isNotEmpty()) {
//            val color = "${productModel.productColorsList.size} Colors available"
//            holder.product_colors_rec_tv.text = color
            holder.product_colors_rec_rv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = ProductColorsAdapter(context, productModel.productColorsList,
                this,true)
            holder.product_colors_rec_rv.adapter=adapter
            holder.product_colors_rec_rv.visibility = View.VISIBLE
        } else {
            holder.product_colors_rec_rv.visibility = View.GONE
        }
    }


    inner class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_rec_iv: ImageView = itemView.findViewById(R.id.product_rec_iv)
        val product_name_rec_tv: TextView = itemView.findViewById(R.id.product_name_rec_tv)
        val product_price_rec_tv: TextView = itemView.findViewById(R.id.product_price_rec_tv)
        val product_colors_rec_tv: TextView = itemView.findViewById(R.id.product_colors_rec_tv)
        val product_sizes_rec_tv: TextView = itemView.findViewById(R.id.product_sizes_rec_tv)
        val product_colors_rec_rv: RecyclerView = itemView.findViewById(R.id.product_colors_rec_rv)

        init {
            itemView.setOnClickListener {
                productAdapterClickListener.productAdapterClick(adapterPosition)
            }
        }
    }

    override fun productColorsAdapterClick(position: Int) {
        TODO("Not yet implemented")
    }
}