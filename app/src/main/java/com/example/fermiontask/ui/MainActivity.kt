package com.example.fermiontask.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fermiontask.R
import com.example.fermiontask.adapter.ProductAdapter
import com.example.fermiontask.model.ProductColorsModel
import com.example.fermiontask.model.ProductModel

class MainActivity : AppCompatActivity(), ProductAdapter.ProductAdapterClickListener {

    companion object {

        val PRODUCT_PARCEL_KEY = "product_parcel_key"
    }

    private lateinit var product_rv: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productsList: ArrayList<ProductModel>
    private lateinit var home_toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        home_toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(home_toolbar)
        supportActionBar?.title = "Home"
        home_toolbar.overflowIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        home_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        product_rv = findViewById(R.id.product_rv)
        productsList = ArrayList()

        productsList = getProductsList()
        product_rv.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(this, productsList, this)
        product_rv.adapter = productAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_menu -> {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getProductsList(): ArrayList<ProductModel> {
        var product_List = ArrayList<ProductModel>()

        for (i in 1..5) {
            product_List.add(
                ProductModel(
                    productName = "T shirt",
                    productImage = "data:image/jpeg;base64,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",
                    productDescription = "A T-shirt, or tee for short, is a style of fabric shirt named after the T shape of its body and sleeves. Traditionally, it has short sleeves and a round neckline, known as a crew neck, which lacks a collar. T-shirts are generally made of a stretchy, light, and inexpensive fabric and are easy to clean",
                    price = 500,
                    sizesList = arrayListOf("S", "M", "L"),
                    productColorsList = arrayListOf(
                        ProductColorsModel("Red", "#FF0000"),
                        ProductColorsModel("Blue", "#0000FF"),
                        ProductColorsModel("Yellow", "#FEE200"),
                        ProductColorsModel("Purple", "#3700B3"),
                        ProductColorsModel("Black", "#000000"),
                    )
                )
            )
            product_List.add(
                ProductModel(
                    productName = "T shirt",
                    productImage = "data:image/jpeg;base64,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",
                    productDescription = "A T-shirt, or tee for short, is a style of fabric shirt named after the T shape of its body and sleeves. Traditionally, it has short sleeves and a round neckline, known as a crew neck, which lacks a collar. T-shirts are generally made of a stretchy, light, and inexpensive fabric and are easy to clean",
                    price = 700,
                    sizesList = arrayListOf(),
                    productColorsList = arrayListOf()
                )
            )
            product_List.add(
                ProductModel(
                    productName = "T shirt",
                    productImage = "data:image/jpeg;base64,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",
                    productDescription = "A T-shirt, or tee for short, is a style of fabric shirt named after the T shape of its body and sleeves. Traditionally, it has short sleeves and a round neckline, known as a crew neck, which lacks a collar. T-shirts are generally made of a stretchy, light, and inexpensive fabric and are easy to clean",
                    price = 1000,
                    sizesList = arrayListOf(),
                    productColorsList = arrayListOf()
                )
            )
            product_List.add(
                ProductModel(
                    productName = "Shirt",
                    productImage = "data:image/jpeg;base64,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",
                    productDescription = "A shirt is a cloth garment for the upper body (from the neck to the waist) Originally an undergarment worn exclusively by men, it has become, in American English, a catch-all term for a broad variety of upper-body garments and undergarments. In British English, a shirt is more specifically a garment with a collar, sleeves with cuffs, and a full vertical opening with buttons or snaps (North Americans would call that a \"dress shirt\", a specific type of collared shirt). A shirt can also be worn with a necktie under the shirt collar.",
                    price = 600,
                    sizesList = arrayListOf("S", "M", "L", "XL", "2XL"),
                    productColorsList = arrayListOf(
                        ProductColorsModel("Red", "#FF0000"),
                        ProductColorsModel("Blue", "#0000FF"),
                        ProductColorsModel("Yellow", "#FEE200"),
                        ProductColorsModel("Purple", "#3700B3"),
                    )
                )
            )
            product_List.add(
                ProductModel(
                    productName = "Shirt",
                    productImage = "data:image/jpeg;base64,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",
                    productDescription = "A shirt is a cloth garment for the upper body (from the neck to the waist) Originally an undergarment worn exclusively by men, it has become, in American English, a catch-all term for a broad variety of upper-body garments and undergarments. In British English, a shirt is more specifically a garment with a collar, sleeves with cuffs, and a full vertical opening with buttons or snaps (North Americans would call that a \"dress shirt\", a specific type of collared shirt). A shirt can also be worn with a necktie under the shirt collar.",
                    price = 500,
                    sizesList = arrayListOf(),
                    productColorsList = arrayListOf()
                )
            )
        }

        return product_List
    }

    override fun productAdapterClick(position: Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(PRODUCT_PARCEL_KEY, productsList[position])
        startActivity(intent)
    }
}