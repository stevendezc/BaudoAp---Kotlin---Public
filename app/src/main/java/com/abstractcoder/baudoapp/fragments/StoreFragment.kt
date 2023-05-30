package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentStoreBinding
import com.abstractcoder.baudoapp.innerImageContentActivity
import com.abstractcoder.baudoapp.innerStoreItemContentActivity
import com.abstractcoder.baudoapp.recyclers.StoreItemMain
import com.abstractcoder.baudoapp.recyclers.StoreItemAdapter
import com.abstractcoder.baudoapp.utils.Firestore
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            Log.d(ContentValues.TAG, "posts on HomeFragment: $posts")
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
        /*storeItemMainList = arrayListOf<StoreItemMain>()
        for (post in nonDuplicates) {
            val id = post.id
            val thumbnail = Uri.parse(post.thumbnail)
            val main_media = Uri.parse(post.main_media)
            val title = post.title
            val author = post.author
            val location = post.location
            val description = post.description
            val comments = post.commentaries
            val creation_date = post.creation_date
            val imagePost = StoreItemMain(id, thumbnail, main_media, title, author, location, description, comments!!, creation_date)
            storeItemMainList.add(imagePost)
        }
        temporalAddToList()*/

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
    }

    /*private fun temporalAddToList() {
        var storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/200"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
        storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/200/300"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
        storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/300/200"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
        storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/300/400"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
        storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/400/300"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
        storeItem = StoreItemMain("1", generateRandomString(10), Uri.parse("https://picsum.photos/200/400"), getRandomPrice(), generateRandomString(50), listOf("M"))
        storeItemMainList.add(storeItem)
    }

    fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    fun getRandomPrice(): Double {
        val min = 50000
        val max = 100000
        val randomPrice = (min + (Math.random() * (max - min))).toDouble()
        return String.format("%.2f", randomPrice).toDouble()
    }*/

}