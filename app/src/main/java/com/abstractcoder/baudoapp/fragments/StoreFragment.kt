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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.StoreCheckOutActivity
import com.abstractcoder.baudoapp.databinding.FragmentStoreBinding
import com.abstractcoder.baudoapp.innerStoreItemContentActivity
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.abstractcoder.baudoapp.recyclers.StoreItemAdapter
import com.abstractcoder.baudoapp.utils.Firestore

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private lateinit var storeItemAdapter: StoreItemAdapter
    private lateinit var storeRecyclerView: RecyclerView
    private var storeItemMainList: ArrayList<StoreItemMain> = arrayListOf<StoreItemMain>()

    private var firestore = Firestore()
    private lateinit var sharedPreferences: SharedPreferences

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

    //private fun setup(view: View, incomingPosts: ArrayList<PostData>) {
    private fun setup(view: View, productsArrayList: ArrayList<StoreItemMain>) {

        val nonDuplicates = productsArrayList.distinct()

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
    }

}