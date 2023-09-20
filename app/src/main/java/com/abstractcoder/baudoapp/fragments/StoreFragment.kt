package com.pereira.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pereira.baudoapp.R
import com.pereira.baudoapp.StoreCheckOutActivity
import com.pereira.baudoapp.databinding.FragmentStoreBinding
import com.pereira.baudoapp.innerStoreItemContentActivity
import com.pereira.baudoapp.recyclers.PurchaseItemMain
import com.pereira.baudoapp.recyclers.StoreItemMain
import com.pereira.baudoapp.recyclers.StoreItemAdapter
import com.pereira.baudoapp.utils.Firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class StoreFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private var activeFilter = 0

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var storeItemAdapter: StoreItemAdapter
    private lateinit var storeRecyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var shoppingCartSharedPreferences: SharedPreferences
    private var shoppingCartItems: MutableList<PurchaseItemMain> = mutableListOf<PurchaseItemMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Get reference to SharedPreferences
        shoppingCartSharedPreferences = requireContext().getSharedPreferences("shopping_cart", AppCompatActivity.MODE_PRIVATE)
        // Register the listener
        shoppingCartSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        db.collection("productos").get().addOnSuccessListener {
            val productsDataList = mutableListOf<StoreItemMain>()
            for (document in it) {
                val myData = document.toObject(StoreItemMain::class.java)
                myData.id = document.id.toString()
                productsDataList.add(myData)
            }
            // Setup subfragments
            val productsArrayList: ArrayList<StoreItemMain> = ArrayList()
            val organizedPosts = productsDataList.sortedByDescending { post -> post.creation_date }.toCollection(ArrayList())
            productsArrayList.addAll(organizedPosts)
            setup(productsArrayList)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "itemList") {
            // Handle the update of the specific preference key
            getShoppingCartItems(key)
        }
    }

    //private fun setup(view: View, incomingPosts: ArrayList<PostData>) {
    private fun setup(productsArrayList: ArrayList<StoreItemMain>) {
        switchProductsFilter(productsArrayList, "")

        binding.estrenButton.setOnClickListener {
            if (activeFilter != 1) {
                activeFilter = 1
                binding.estrenButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_yellow))
                binding.editorialButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.cositasButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                switchProductsFilter(productsArrayList, "estren")
            } else {
                activeFilter = 0
                binding.estrenButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.editorialButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.cositasButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                switchProductsFilter(productsArrayList, "")
            }
        }

        binding.editorialButton.setOnClickListener {
            if (activeFilter != 2) {
                activeFilter = 2
                binding.estrenButton.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.editorialButton.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.baudo_yellow))
                binding.cositasButton.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                switchProductsFilter(productsArrayList, "editorial")
            } else {
                activeFilter = 0
                binding.estrenButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.editorialButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.cositasButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                switchProductsFilter(productsArrayList, "")
            }
        }

        binding.cositasButton.setOnClickListener {
            if (activeFilter != 3) {
                activeFilter = 3
                binding.estrenButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.editorialButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.cositasButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_yellow))
                switchProductsFilter(productsArrayList, "cositas")
            } else {
                activeFilter = 0
                binding.estrenButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.editorialButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                binding.cositasButton.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.baudo_grey2))
                switchProductsFilter(productsArrayList, "")
            }
        }

        binding.shoppingCartButton.setOnClickListener {
            val intent = Intent(activity, StoreCheckOutActivity::class.java)
            startActivity(intent)
        }
        getShoppingCartItems("itemList")
    }

    private fun switchProductsFilter(productsArrayList: ArrayList<StoreItemMain>, filter: String) {
        var filteredProductsArrayList: ArrayList<StoreItemMain> = ArrayList()
        filteredProductsArrayList.clear()
        filteredProductsArrayList.addAll(productsArrayList)
        if (filter != "") {
            var filteredProductsArray = productsArrayList.filter { product -> product.type == filter }
            filteredProductsArrayList.clear()
            filteredProductsArrayList.addAll(filteredProductsArray)
        }
        storeRecyclerView = binding.storeListRecycler
        storeRecyclerView.layoutManager = layoutManager
        storeRecyclerView.setHasFixedSize(true)
        storeItemAdapter = StoreItemAdapter(filteredProductsArrayList)
        storeItemAdapter.onItemClick = {
            val intent = Intent(this.context, innerStoreItemContentActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }
        storeRecyclerView.adapter = storeItemAdapter
    }

    private fun getShoppingCartItems(key: String) {
        var itemListString = shoppingCartSharedPreferences?.getString(key, "") ?: ""
        shoppingCartItems = if (itemListString.isNotEmpty()) {
            Gson().fromJson(itemListString, Array<PurchaseItemMain>::class.java).toMutableList()
        } else {
            mutableListOf<PurchaseItemMain>()
        }
        binding.shoppingCartItemCount.text = if (shoppingCartItems.size > 0) {
            shoppingCartItems.size.toString()
        } else {
            "n"
        }
    }

}