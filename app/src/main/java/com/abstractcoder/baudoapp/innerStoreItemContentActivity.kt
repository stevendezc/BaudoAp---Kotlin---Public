package com.abstractcoder.baudoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.abstractcoder.baudoapp.databinding.ActivityInnerStoreItemContentBinding
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.bumptech.glide.Glide

class innerStoreItemContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInnerStoreItemContentBinding

    private lateinit var postId: String
    private lateinit var layoutManager: LinearLayoutManager

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

            binding.backButton.setOnClickListener {
                finish()
            }
        }
    }
}