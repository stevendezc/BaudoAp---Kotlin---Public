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
import com.abstractcoder.baudoapp.*
import com.abstractcoder.baudoapp.databinding.FragmentHomeBinding
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeImageFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomePodcastFragment
import com.abstractcoder.baudoapp.fragments.home_fragments.HomeVideoFragment
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userData: FirebaseUser
    private val db = FirebaseFirestore.getInstance()
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

        // Load Posts
        db.collection("users").document(email!!).get().addOnSuccessListener { user ->
            val myData = user.toObject(FirebaseUser::class.java) ?: FirebaseUser()
            userData = myData
            db.collection("posts").get().addOnSuccessListener { posts ->
                val postDataList = mutableListOf<PostData>()
                for (document in posts) {
                    val postData = document.toObject(PostData::class.java)
                    postData.id = document.id
                    postDataList.add(postData)
                }
                binding.contentLoading.visibility = ProgressBar.GONE
                binding.contentNavigationView.visibility = BottomNavigationView.VISIBLE
                binding.contentSubFragmentWrapper.visibility = FrameLayout.VISIBLE
                Log.d(ContentValues.TAG, "posts on HomeFragment: $postDataList")
                // Setup subfragments
                val postsArrayList: ArrayList<PostData> = ArrayList()
                val organizedPosts = postDataList.sortedByDescending { post -> post.creation_date }.toCollection(ArrayList())
                postsArrayList.addAll(organizedPosts)
                fragmentSetup(postsArrayList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bundle.clear()
    }

    private fun fragmentSetup(posts: ArrayList<PostData>) {
        bundle.putSerializable("posts", posts)
        bundle.putSerializable("active_podcasts", userData.active_podcasts)
        val imageSubFragment = HomeImageFragment()
        imageSubFragment.arguments = bundle
        val videoSubFragment = HomeVideoFragment()
        videoSubFragment.arguments = bundle
        val podcastSubFragment = HomePodcastFragment()
        podcastSubFragment.arguments = bundle
        makeCurrentFragment(imageSubFragment)

        // Fragment Navigation
        binding.contentNavigationView.itemBackgroundResource = R.color.content_items_background_color
        binding.contentNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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