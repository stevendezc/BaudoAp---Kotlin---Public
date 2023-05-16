package com.abstractcoder.baudoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            val imageMainMedia = storeItemContent.image
            Glide.with(binding.storeItemImageView)
                .load(imageMainMedia)
                .into(binding.storeItemImageView)

            binding.storeItemTitle.text = storeItemContent.name
            binding.storeItemPrice.text = "Precio $" + storeItemContent.price
            binding.storeItemDescription.text = "Descripcion del producto, " + storeItemContent.description

            binding.backButton.setOnClickListener {
                finish()
            }
        }
    }
}