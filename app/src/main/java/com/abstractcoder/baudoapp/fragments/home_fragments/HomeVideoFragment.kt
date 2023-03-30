package com.abstractcoder.baudoapp.fragments.home_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.FullSizeVideoActivity
import com.abstractcoder.baudoapp.PostData
import com.abstractcoder.baudoapp.databinding.FragmentHomeVideoBinding
import com.abstractcoder.baudoapp.recyclers.*

class HomeVideoFragment : Fragment() {

    private var _binding: FragmentHomeVideoBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var videoAdapter: VideoPostAdapter
    private lateinit var videoRecyclerView: RecyclerView
    private var videoPostMainList: ArrayList<VideoPostMain> = arrayListOf<VideoPostMain>()
    lateinit var videoId: Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = GridLayoutManager(context, 3)

        val incomingPostList: ArrayList<PostData> = arguments?.get("posts") as ArrayList<PostData>
        initVideoData(incomingPostList)
    }

    private fun getSpanCount() {
        val metrics: DisplayMetrics = DisplayMetrics()
    }

    private fun initVideoData(incomingPosts: ArrayList<PostData>) {
        val nonDuplicates = incomingPosts.distinct()
        videoPostMainList = arrayListOf<VideoPostMain>()
        for (post in nonDuplicates) {
            if (post.type == "video") {
                val id = post.id
                val video = Uri.parse(post.main_media)
                val thumbnail = Uri.parse(post.thumbnail)
                val title = post.title
                val description = post.description
                val category = post.category
                val imagePost = VideoPostMain(id, video, thumbnail, title, description, category)
                videoPostMainList.add(imagePost)
            }
        }

        videoRecyclerView = binding.videoListRecycler
        videoRecyclerView.layoutManager = layoutManager
        videoRecyclerView.setHasFixedSize(true)
        videoAdapter = VideoPostAdapter(videoPostMainList)
        videoRecyclerView.adapter = videoAdapter

        videoAdapter.onItemClick = {
            val intent = Intent(this.context, FullSizeVideoActivity::class.java)
            intent.putExtra("video", it)
            startActivity(intent)
        }
    }

}