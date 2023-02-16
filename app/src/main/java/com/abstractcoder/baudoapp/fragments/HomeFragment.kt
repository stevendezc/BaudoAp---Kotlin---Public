package com.abstractcoder.baudoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentHomeBinding
import com.abstractcoder.baudoapp.recyclers.ImagePostAdapter
import com.abstractcoder.baudoapp.recyclers.ImagePostMain
import kotlinx.android.synthetic.main.activity_home.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var imageAdapter: ImagePostAdapter
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imagePostMainList: ArrayList<ImagePostMain>

    lateinit var imageId: Array<Int>
    lateinit var heading: Array<String>
    lateinit var postMain: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        val layoutManager = LinearLayoutManager(context)
        imageRecyclerView = view.findViewById(R.id.image_list_recycler)
        imageRecyclerView.layoutManager = layoutManager
        imageRecyclerView.setHasFixedSize(true)
        imageAdapter = ImagePostAdapter(imagePostMainList)
        imageRecyclerView.adapter = imageAdapter
    }

    private fun initData() {
        imagePostMainList = arrayListOf<ImagePostMain>()
        imageId = arrayOf(
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
            R.drawable.background,
        )
        heading = arrayOf(
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
            getString(R.string.home_image),
        )
        postMain = arrayOf(
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
            getString(R.string.app_name),
        )

        for (i in imageId.indices) {
            val imagePost = ImagePostMain(imageId[i], heading[i], postMain[i])
            imagePostMainList.add(imagePost)
        }

    }

}