package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.StoreCheckOutActivity
import com.abstractcoder.baudoapp.databinding.FragmentStoreBinding
import com.abstractcoder.baudoapp.innerStoreItemContentActivity
import com.abstractcoder.baudoapp.recyclers.PurchaseItemMain
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.abstractcoder.baudoapp.recyclers.StoreItemAdapter
import com.abstractcoder.baudoapp.utils.Firestore
import com.google.gson.Gson

class StoreFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var storeItemAdapter: StoreItemAdapter
    private lateinit var storeRecyclerView: RecyclerView
    private var storeItemMainList: ArrayList<StoreItemMain> = arrayListOf<StoreItemMain>()

    private var firestore = Firestore()
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
        val email = sharedPreferences.getString("email", "")
        firestore.activateSubscribers(requireContext(), email!!)

        firestore.productsLiveData.observe(viewLifecycleOwner, Observer { posts ->
            Log.d(ContentValues.TAG, "items on StoreFragment: $posts")
            // Setup subfragments
            val productsArrayList: ArrayList<StoreItemMain> = ArrayList()
            val organizedPosts = posts.sortedByDescending { it.creation_date }.toCollection(ArrayList())
            productsArrayList.addAll(organizedPosts)
            setup(view, productsArrayList)
        })
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "itemList") {
            // Handle the update of the specific preference key
            getShoppingCartItems(key)
        }
    }

    //private fun setup(view: View, incomingPosts: ArrayList<PostData>) {
    private fun setup(view: View, productsArrayList: ArrayList<StoreItemMain>) {
        storeRecyclerView = binding.storeListRecycler
        storeRecyclerView.layoutManager = layoutManager
        storeRecyclerView.setHasFixedSize(true)
        storeItemAdapter = StoreItemAdapter(productsArrayList)
        storeRecyclerView.adapter = storeItemAdapter

        storeItemAdapter.onItemClick = {
            val intent = Intent(this.context, innerStoreItemContentActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        binding.shoppingCartButton.setOnClickListener {
            val intent = Intent(activity, StoreCheckOutActivity::class.java)
            startActivity(intent)
        }
        getShoppingCartItems("itemList")
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