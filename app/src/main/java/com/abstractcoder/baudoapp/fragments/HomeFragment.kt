package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.abstractcoder.baudoapp.*
import com.abstractcoder.baudoapp.databinding.FragmentHomeBinding
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeImageFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomePodcastFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeVideoFragment
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var firestore = Firestore()
    private val bundle = Bundle()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        firestore.activateSubscribers(requireContext(), email!!)
        // Load Posts

        firestore.postsLiveData.observe(viewLifecycleOwner, Observer { posts ->
            // Update your UI with the new data
            binding.contentLoading.visibility = ProgressBar.GONE
            binding.contentNavigationView.visibility = BottomNavigationView.VISIBLE
            binding.contentSubFragmentWrapper.visibility = FrameLayout.VISIBLE
            Log.d(ContentValues.TAG, "posts on HomeFragment: $posts")
            // Setup subfragments
            val postsArrayList: ArrayList<PostData> = ArrayList()
            val organizedPosts = posts.sortedByDescending { it.creation_date }.toCollection(ArrayList())
            postsArrayList.addAll(organizedPosts)
            fragmentSetup(postsArrayList)
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
        videoSubFragment.arguments = bundle
        val podcastSubFragment = HomePodcastFragment()
        podcastSubFragment.arguments = bundle
        makeCurrentFragment(imageSubFragment)

        // Fragment Navigation
        binding.contentNavigationView.itemBackgroundResource = R.color.content_items_background_color
        binding.contentNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_image -> makeCurrentFragment(imageSubFragment)
                R.id.ic_video -> makeCurrentFragment(videoSubFragment)
                R.id.ic_podcast -> makeCurrentFragment(podcastSubFragment)
            }
            true
        }

        binding.homeInfo.setOnClickListener {
            InfoDialog("contenido").show(requireFragmentManager(), "info dialog")
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