package com.abstractcoder.baudoapp.fragments.home_fragments

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
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
        initVideoDummyData()

        videoRecyclerView = binding.videoListRecycler
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