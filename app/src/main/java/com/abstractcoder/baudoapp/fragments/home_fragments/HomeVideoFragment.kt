package com.abstractcoder.baudoapp.fragments.home_fragments

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.recyclers.*
import com.google.firebase.Timestamp

class HomeVideoFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_home_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = GridLayoutManager(context, 3)
        initVideoDummyData()

        videoRecyclerView = view.findViewById(R.id.video_list_recycler)
        videoRecyclerView.layoutManager = layoutManager
        videoRecyclerView.setHasFixedSize(true)
        videoAdapter = VideoPostAdapter(videoPostMainList)
        videoRecyclerView.adapter = videoAdapter
    }

    private fun getSpanCount() {
        val metrics: DisplayMetrics = DisplayMetrics()
    }

    private fun initVideoDummyData() {
        videoPostMainList = arrayListOf<VideoPostMain>()
        videoId = arrayOf(
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
        )

        for (i in videoId.indices) {
            val imagePost = VideoPostMain(videoId[i])
            videoPostMainList.add(imagePost)
        }
    }

}