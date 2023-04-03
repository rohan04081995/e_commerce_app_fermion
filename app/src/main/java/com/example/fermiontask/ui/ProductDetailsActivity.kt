package com.example.fermiontask.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fermiontask.R
import com.example.fermiontask.adapter.ProductColorsAdapter
import com.example.fermiontask.adapter.ProductSizesAdapter
import com.example.fermiontask.model.ProductModel

class ProductDetailsActivity : AppCompatActivity(),
    ProductColorsAdapter.ProductColorsAdapterClickListener,
    ProductSizesAdapter.ProductSizesAdapterClickListener {

    private lateinit var product: ProductModel

    private lateinit var product_details_iv: ImageView
    private lateinit var product_details_price_tv: TextView
    private lateinit var product_details_size_tv: TextView
    private lateinit var product_details_sizes_rv: RecyclerView
    private lateinit var product_details_color_tv: TextView
    private lateinit var product_details_colors_rv: RecyclerView
    private lateinit var product_description_tv: TextView
    private lateinit var buy_now_button_details: Button
    private lateinit var product_details_toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        product_details_toolbar = findViewById(R.id.product_details_toolbar)
        setSupportActionBar(product_details_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_arrow_back_))

        product_details_iv = findViewById(R.id.product_details_iv)
        product_details_price_tv = findViewById(R.id.product_details_price_tv)
        product_details_size_tv = findViewById(R.id.product_details_size_tv)
        product_details_sizes_rv = findViewById(R.id.product_details_sizes_rv)
        product_details_color_tv = findViewById(R.id.product_details_color_tv)
        product_details_colors_rv = findViewById(R.id.product_details_colors_rv)
        product_description_tv = findViewById(R.id.product_description_tv)
        buy_now_button_details = findViewById(R.id.buy_now_button_details)

        buy_now_button_details.setOnClickListener {

            Toast.makeText(this, "thank you for buying keep shopping", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            product = intent.getParcelableExtra(
                MainActivity.PRODUCT_PARCEL_KEY, ProductModel::class.java
            )!!
        } else {
            product = intent.getParcelableExtra(MainActivity.PRODUCT_PARCEL_KEY)!!
        }

        supportActionBar?.title = product.productName
        product_details_toolbar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )


        Glide.with(this).load(product.productImage).placeholder(R.drawable.ic_launcher_foreground)
            .into(product_details_iv)

        product_details_price_tv.text = "₹${product.price}"

        if (product.productColorsList.isNotEmpty()) {
            product_details_color_tv.text = "Color: ${product.productColorsList[0].colorName}"
            product_details_color_tv.setTextColor(Color.parseColor(product.productColorsList[0].colorCode))
            product_details_colors_rv.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val colorsAdapter = ProductColorsAdapter(
                this, product.productColorsList,
                this, false
            )
            product_details_colors_rv.adapter = colorsAdapter
        } else {
            product_details_color_tv.visibility = View.GONE
            product_details_colors_rv.visibility = View.GONE
        }

        if (product.sizesList.isNotEmpty()) {
            product_details_size_tv.text = "Size: ${product.sizesList[0]}"
            product_details_sizes_rv.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val sizesAdapter = ProductSizesAdapter(this, product.sizesList, this)
            product_details_sizes_rv.adapter = sizesAdapter
        } else {
//            product_details_size_tv.visibility = View.GONE
            product_details_size_tv.text = "Size: Large"
            product_details_sizes_rv.visibility = View.GONE
        }
        if (product.productDescription != null && !product.productDescription.equals("")) {
            product_description_tv.text = "${product.productDescription}"
        }

    }

    override fun productColorsAdapterClick(position: Int) {
        product_details_color_tv.text = "Color: ${product.productColorsList[position].colorName}"
        product_details_color_tv.setTextColor(Color.parseColor(product.productColorsList[position].colorCode))

    }

    override fun productSizesAdapterClick(position: Int) {
        product_details_size_tv.text = "Size: ${product.sizesList[position]}"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}