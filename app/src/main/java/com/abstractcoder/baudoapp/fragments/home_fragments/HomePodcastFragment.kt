package com.abstractcoder.baudoapp.fragments.home_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.recyclers.PodcastPostAdapter
import com.abstractcoder.baudoapp.recyclers.PodcastPostMain
import com.google.firebase.Timestamp

class HomePodcastFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var podcastAdapter: PodcastPostAdapter
    private lateinit var podcastRecyclerView: RecyclerView
    private var podcastPostMainList: ArrayList<PodcastPostMain> = arrayListOf<PodcastPostMain>()
    lateinit var podcastId: Array<Int>
    lateinit var podcastHeading: Array<String>
    lateinit var podcastDate: Array<Timestamp>
    lateinit var podcastPostMain: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_podcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        initPodcastDummyData()

        podcastRecyclerView = view.findViewById(R.id.podcast_list_recycler)
        podcastRecyclerView.layoutManager = layoutManager
        podcastRecyclerView.setHasFixedSize(true)
        podcastAdapter = PodcastPostAdapter(podcastPostMainList)
        podcastRecyclerView.adapter = podcastAdapter
    }

    private fun initPodcastDummyData() {
        podcastPostMainList = arrayListOf<PodcastPostMain>()
        podcastId = arrayOf(
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
        )
        podcastHeading = arrayOf(
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
        )
        val currentDatetime = Timestamp.now()
        podcastDate = arrayOf(
            currentDatetime,
            currentDatetime,
            currentDatetime,
            currentDatetime,
            currentDatetime,
        )
        podcastPostMain = arrayOf(
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
        )

        for (i in podcastId.indices) {
            val podcastPost = PodcastPostMain(podcastId[i], podcastHeading[i], podcastDate[i], podcastPostMain[i])
            podcastPostMainList.add(podcastPost)
        }
    }

}