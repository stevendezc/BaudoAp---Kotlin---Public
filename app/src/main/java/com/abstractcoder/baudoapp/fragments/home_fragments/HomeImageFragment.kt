package com.abstractcoder.baudoapp.fragments.home_fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.recyclers.ImagePostAdapter
import com.abstractcoder.baudoapp.recyclers.ImagePostMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.MyCallback

class HomeImageFragment : Fragment() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var imageAdapter: ImagePostAdapter
    private lateinit var imageRecyclerView: RecyclerView
    private var imagePostMainList: ArrayList<ImagePostMain> = arrayListOf<ImagePostMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)

        val incomingPostList: ArrayList<PostData> = arguments?.get("posts") as ArrayList<PostData>
        Log.d(TAG, "incomingPostList on ImageFragment: ${incomingPostList.size}")
        // Load Posts
        initData(view, incomingPostList)
    }

    private fun initData(view: View, incomingPosts: ArrayList<PostData>) {

        Log.d(TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingPosts: ${incomingPosts.size}")
        val nonDuplicates = incomingPosts.distinct()
        imagePostMainList = arrayListOf<ImagePostMain>()
        for (post in nonDuplicates) {
            if (post.type == "image") {
                val uri = Uri.parse(post.thumbnail)
                val author = post.author
                val description = post.description
                val imagePost = ImagePostMain(uri, author, description)
                imagePostMainList.add(imagePost)
            }
        }

        imageRecyclerView = view.findViewById(R.id.image_list_recycler)
        imageRecyclerView.layoutManager = layoutManager
        imageRecyclerView.setHasFixedSize(true)
        imageAdapter = ImagePostAdapter(imagePostMainList)
        imageRecyclerView.adapter = imageAdapter
    }

}