package com.abstractcoder.baudoapp.fragments.home_fragments

import android.content.ContentValues
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
import com.abstractcoder.baudoapp.databinding.FragmentHomePodcastBinding
import com.abstractcoder.baudoapp.recyclers.PodcastPostAdapter
import com.abstractcoder.baudoapp.recyclers.PodcastPostMain

class HomePodcastFragment : Fragment() {

    private var _binding: FragmentHomePodcastBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var podcastAdapter: PodcastPostAdapter
    private lateinit var podcastRecyclerView: RecyclerView
    private var podcastPostMainList: ArrayList<PodcastPostMain> = arrayListOf<PodcastPostMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomePodcastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)

        val incomingPostList: ArrayList<PostData> = arguments?.get("posts") as ArrayList<PostData>
        Log.d(ContentValues.TAG, "incomingPostList on PodcastFragment: ${incomingPostList.size}")
        // Load Posts
        initData(view,incomingPostList)
    }

    private fun initData(view: View, incomingPosts: ArrayList<PostData>) {

        Log.d(ContentValues.TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingPosts: ${incomingPosts.size}")
        val nonDuplicates = incomingPosts.distinct()
        podcastPostMainList = arrayListOf<PodcastPostMain>()
        for (post in nonDuplicates) {
            if (post.type == "podcast") {
                val thumbnail = Uri.parse(post.thumbnail)
                val title = post.title
                val timestamp = post.timestamp
                val description = post.description
                val media = Uri.parse(post.main_media)
                val podcastPost = PodcastPostMain(thumbnail, title, timestamp, description, media)
                podcastPostMainList.add(podcastPost)
            }
        }

        podcastRecyclerView = binding.podcastListRecycler
        podcastRecyclerView.layoutManager = layoutManager
        podcastRecyclerView.setHasFixedSize(true)
        podcastAdapter = context?.let { PodcastPostAdapter(it, podcastPostMainList) }!!
        podcastRecyclerView.adapter = podcastAdapter
    }

}