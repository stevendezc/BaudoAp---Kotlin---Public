package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.abstractcoder.baudoapp.*
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeImageFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomePodcastFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeVideoFragment
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.MyCallback
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var firestore = Firestore()
    private val bundle = Bundle()

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

        // Load Posts
        val posts = firestore.retrieveDocuments(object : MyCallback {
            override fun onSuccess(result: ArrayList<PostData>) {
                Log.d(ContentValues.TAG, "posts on HomeFragment: $result")
                // Setup subfragments
                fragmentSetup(result)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bundle.clear()
    }

    private fun fragmentSetup(posts: ArrayList<PostData>) {
        bundle.putSerializable("posts", posts)
        val imageSubFragment = HomeImageFragment()
        imageSubFragment.arguments = bundle
        val videoSubFragment = HomeVideoFragment()
        val podcastSubFragment = HomePodcastFragment()
        podcastSubFragment.arguments = bundle
        makeCurrentFragment(imageSubFragment)

        // Fragment Navigation
        contentNavigationView.itemBackgroundResource = R.color.content_items_background_color
        contentNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_image -> makeCurrentFragment(imageSubFragment)
                R.id.ic_video -> makeCurrentFragment(videoSubFragment)
                R.id.ic_podcast -> makeCurrentFragment(podcastSubFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.contentSubFragmentWrapper, fragment)
            commit()
        }
    }

}