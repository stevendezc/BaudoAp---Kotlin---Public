package com.abstractcoder.baudoapp

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.abstractcoder.baudoapp.databinding.ActivityInnerStoreItemContentBinding
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.abstractcoder.baudoapp.utils.StoreDialog
import com.bumptech.glide.Glide

class innerStoreItemContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerStoreItemContentBinding

    private lateinit var postId: String
    private lateinit var layoutManager: LinearLayoutManager

    private var selectedSize: String = ""
    private var productQuantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInnerStoreItemContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutManager = LinearLayoutManager(this.baseContext)
        val storeItemContent = intent.getParcelableExtra<StoreItemMain>("item")
        postId = storeItemContent?.id.toString()

        setup(storeItemContent!!)
    }

    private fun setup(storeItemContent: StoreItemMain) {
        if (storeItemContent != null) {
            val imageMainMedia = storeItemContent.thumbnail
            Glide.with(binding.storeItemImageView)
                .load(imageMainMedia)
                .into(binding.storeItemImageView)

            binding.storeItemTitle.text = storeItemContent.title
            binding.storeItemPrice.text = "Precio $" + storeItemContent.price
            binding.storeItemDescription.text = "Descripcion del producto, " + storeItemContent.description

            if (storeItemContent.stock_xs == 0) { binding.xsBadge.visibility = TextView.GONE }
            if (storeItemContent.stock_s == 0) { binding.sBadge.visibility = TextView.GONE }
            if (storeItemContent.stock_m == 0) { binding.mBadge.visibility = TextView.GONE }
            if (storeItemContent.stock_l == 0) { binding.lBadge.visibility = TextView.GONE }
            if (storeItemContent.stock_xl == 0) { binding.xlBadge.visibility = TextView.GONE }

            if (storeItemContent.stock_xs == 0 &&
                storeItemContent.stock_s == 0 &&
                storeItemContent.stock_m == 0 &&
                storeItemContent.stock_l == 0 &&
                storeItemContent.stock_xl == 0) {
                binding.sizeLabel.visibility = TextView.GONE
                binding.purchaseButton.visibility = TextView.GONE
                binding.addToCart.visibility = TextView.GONE
                binding.nonPurchaseableButton.visibility = TextView.VISIBLE
            }

            if (this.selectedSize == "") {
                binding.quantityContainer.visibility = LinearLayout.GONE
            }
            binding.xsBadge.setOnClickListener {
                this.selectedSize = "XS"
                switchBottomMenuColor(0)
            }
            binding.sBadge.setOnClickListener {
                this.selectedSize = "S"
                switchBottomMenuColor(1)
            }
            binding.mBadge.setOnClickListener {
                this.selectedSize = "M"
                switchBottomMenuColor(2)
            }
            binding.lBadge.setOnClickListener {
                this.selectedSize = "L"
                switchBottomMenuColor(3)
            }
            binding.xlBadge.setOnClickListener {
                this.selectedSize = "XL"
                switchBottomMenuColor(4)
            }

            binding.backButton.setOnClickListener {
                finish()
            }

            binding.quantity.text = this.productQuantity.toString()
            binding.quantityIncreaser.setOnClickListener {
                if (this.productQuantity < getMaxQuantityLimit(storeItemContent)!!) {
                    this.productQuantity = this.productQuantity + 1
                    binding.quantity.text = this.productQuantity.toString()
                }
            }
            binding.quantityDecreaser.setOnClickListener {
                if (this.productQuantity > 0) {
                    this.productQuantity = this.productQuantity - 1
                    binding.quantity.text = this.productQuantity.toString()
                }
            }

            binding.purchaseButton.setOnClickListener {
                if (selectedSize != "" && productQuantity != 0) {
                    StoreDialog(
                        true,
                        imageMainMedia!!,
                        storeItemContent.title!!,
                        storeItemContent.price!!,
                        this.productQuantity,
                        this.selectedSize
                    ).show(this.supportFragmentManager, "purchase dialog")
                } else {
                    Toast.makeText(
                        this,
                        "No hay productos activos para la compra",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            binding.addToCart.setOnClickListener {
                if (selectedSize != "" && productQuantity != 0) {
                    StoreDialog(
                        false,
                        imageMainMedia!!,
                        storeItemContent.title!!,
                        storeItemContent.price!!,
                        this.productQuantity,
                        this.selectedSize
                    ).show(this.supportFragmentManager, "purchase dialog")
                } else {
                    Toast.makeText(
                        this,
                        "No hay productos activos para la compra",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            binding.shoppingCartButton.setOnClickListener {
                val intent = Intent(this, StoreCheckOutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getMaxQuantityLimit(storeItemContent: StoreItemMain): Int? {
        val sizeMap = mapOf(
            "XS" to storeItemContent.stock_xs,
            "S" to storeItemContent.stock_s,
            "M" to storeItemContent.stock_m,
            "L" to storeItemContent.stock_l,
            "XL" to storeItemContent.stock_xl
        )
        return sizeMap[selectedSize]
    }

    private fun switchBottomMenuColor(pos: Int) {
        binding.quantityContainer.visibility = LinearLayout.VISIBLE
        binding.quantityLabel.visibility = TextView.VISIBLE
        val selectedColor = ContextCompat.getColor(this, R.color.white)
        val unselectedColor = ContextCompat.getColor(this, R.color.gray_85)
        val optionsItems = arrayOf(
            binding.xsBadge,
            binding.sBadge,
            binding.mBadge,
            binding.lBadge,
            binding.xlBadge,
        )
        for (index in optionsItems.indices) {
            val option = optionsItems[index]
            println(pos)
            println(option)
            if (index == pos) {
                option.background = selectedColor.toDrawable()
                option.setTypeface(null, Typeface.BOLD)
            } else {
                option.background = unselectedColor.toDrawable()
                option.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}