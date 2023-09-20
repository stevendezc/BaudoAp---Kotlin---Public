package com.pereira.baudoapp.fragments.home_fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.FullSizeVideoActivity
import com.pereira.baudoapp.PostData
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.FragmentHomeVideoBinding
import com.pereira.baudoapp.recyclers.*

class HomeVideoFragment : Fragment() {

    private var _binding: FragmentHomeVideoBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var videoAdapter: VideoPostAdapter
    private lateinit var videoRecyclerView: RecyclerView
    private var videoPostMainList: ArrayList<VideoPostMain> = arrayListOf<VideoPostMain>()
    private var isOnline: Boolean = false
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

        layoutManager = GridLayoutManager(context, 2)

        val sharedPref = this.activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        isOnline = sharedPref?.getBoolean("online", false)!!

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
                val imagePost = VideoPostMain(id, video, thumbnail, title!!, description!!, category!!)
                videoPostMainList.add(imagePost)
            }
        }

        videoRecyclerView = binding.videoListRecycler
        videoRecyclerView.layoutManager = layoutManager
        videoRecyclerView.setHasFixedSize(true)
        videoAdapter = VideoPostAdapter(videoPostMainList)
        videoRecyclerView.adapter = videoAdapter

        videoAdapter.onItemClick = {
            if (isOnline) {
                val intent = Intent(this.context, FullSizeVideoActivity::class.java)
                intent.putExtra("video", it)
                startActivity(intent)
            } else {
                Toast.makeText(this.context, "Conexión inactiva, intente mas tarde", Toast.LENGTH_SHORT).show()
            }
        }
    }

}