package com.pereira.baudoapp.fragments.home_fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.*
import com.pereira.baudoapp.databinding.FragmentHomePodcastBinding
import com.pereira.baudoapp.recyclers.PodcastPostAdapter
import com.pereira.baudoapp.recyclers.PodcastPostMain
import com.google.firebase.firestore.FirebaseFirestore

class HomePodcastFragment : Fragment() {

    private var _binding: FragmentHomePodcastBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userData: FirebaseUser

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var podcastAdapter: PodcastPostAdapter
    private lateinit var podcastRecyclerView: RecyclerView
    private var isOnline: Boolean = false
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

        val incomingActivePodcastList: ArrayList<PodcastInfo> = arguments?.get("active_podcasts") as ArrayList<PodcastInfo>
        Log.d(ContentValues.TAG, "incomingActivePodcastList on PodcastFragment: ${incomingActivePodcastList.size}")

        val sharedPref = this.activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPref?.getString("email", "")
        isOnline = sharedPref?.getBoolean("online", false)!!
        // Load Posts
        db.collection("users").document(email!!).get().addOnSuccessListener {
            val myData = it.toObject(FirebaseUser::class.java) ?: FirebaseUser()
            // Update your UI with the new data
            userData = myData
            initData(view,incomingPostList, incomingActivePodcastList)
        }
    }

    private fun getPodcastStatus(podcastId: String?): String? {
        val userActivePodcasts = userData.active_podcasts
        val activePodcast = userActivePodcasts.find { it.post == podcastId }
        if (activePodcast != null) {
            return if (activePodcast?.finished == true) "Finalizado" else "Iniciado"
        } else {
            return "No escuchado"
        }
    }

    private fun initData(view: View, incomingPosts: ArrayList<PostData>, incomingActivePodcastList: ArrayList<PodcastInfo> ) {

        Log.d(ContentValues.TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingPosts: ${incomingPosts.size}")
        val nonDuplicates = incomingPosts.distinct()
        podcastPostMainList = arrayListOf<PodcastPostMain>()
        for (post in nonDuplicates) {
            if (post.type == "podcast") {
                val id = post.id
                val thumbnail = Uri.parse(post.thumbnail)
                val background = Uri.parse(post.thumbnail2)
                val title = post.title
                val timestamp = post.creation_date
                val description = post.description
                val media = Uri.parse(post.main_media)
                val activePodcast = incomingActivePodcastList.filter { it.post.equals(id) }
                var status = getPodcastStatus(id)
                if (activePodcast.isNotEmpty() &&
                    activePodcast[0].post == id &&
                    activePodcast[0].finished == true) {
                }
                if (activePodcast.isNotEmpty() &&
                    activePodcast[0].post == id &&
                    activePodcast[0].finished == false) {
                }
                val podcastPost = PodcastPostMain(id, thumbnail, background, title, timestamp, description, media, status)
                podcastPostMainList.add(podcastPost)
            }
        }

        podcastRecyclerView = binding.podcastListRecycler
        podcastRecyclerView.layoutManager = layoutManager
        podcastRecyclerView.setHasFixedSize(true)
        podcastAdapter = context?.let { PodcastPostAdapter(it, podcastPostMainList) }!!
        podcastRecyclerView.adapter = podcastAdapter

        podcastAdapter.onItemClick = {
            if (isOnline) {
                val intent = Intent(this.context, InnerPodcastContentActivity::class.java)
                intent.putExtra("podcast", it)
                startActivity(intent)
            } else {
                Toast.makeText(this.context, "Conexión inactiva, intente mas tarde", Toast.LENGTH_SHORT).show()
            }
        }
    }

}