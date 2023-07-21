package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.CommunityData
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentCommunityBinding
import com.abstractcoder.baudoapp.recyclers.CommunityAdapter
import com.abstractcoder.baudoapp.recyclers.CommunityMain
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.InfoDialog

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var firestore = Firestore()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var communityAdapter: CommunityAdapter
    private lateinit var communityRecyclerView: RecyclerView
    private var communityMainList: ArrayList<CommunityMain> = arrayListOf<CommunityMain>()
    private var filterApplied = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        firestore.activateSubscribers(requireContext(), email!!)
        // Load Communities
        firestore.communitiesLiveData.observe(viewLifecycleOwner, Observer { communities ->
            // Update your UI with the new data
            binding.contentLoading.visibility = ProgressBar.GONE
            binding.filterButtons.visibility = LinearLayout.VISIBLE
            binding.imageListRecycler.visibility = RecyclerView.VISIBLE
            Log.d(ContentValues.TAG, "communities on HomeFragment: $communities")
            // Setup subfragments
            val communitiesArrayList: ArrayList<CommunityData> = ArrayList()
            communitiesArrayList.addAll(communities)
            initData(communitiesArrayList)
        })
        /*val community = firestore.retrieveCommunities(object : CommunitiesCallback {
            override fun onSuccess(incomingPostList: ArrayList<CommunityData>) {
                binding.contentLoading.visibility = ProgressBar.GONE
                binding.filterButtons.visibility = LinearLayout.VISIBLE
                binding.imageListRecycler.visibility = RecyclerView.VISIBLE
                Log.d(ContentValues.TAG, "posts on HomeFragment: $incomingPostList")
                // Setup subfragments
                initData(incomingPostList)
            }
        })*/
    }

    private fun applyFilter(filterNumber: Int, categoryName: String, buttonText: android.widget.TextView, categoryColor: Int) {
        buttonText.setTypeface(null, Typeface.BOLD)
        buttonText.setTextColor(
            ContextCompat.getColor(
                this.requireContext(),
                categoryColor
            )
        )
        this.filterApplied = filterNumber
        var filteredArrayList = arrayListOf<CommunityMain>()
        filteredArrayList = communityMainList.filter { it.category == categoryName } as ArrayList<CommunityMain>
        communityAdapter = CommunityAdapter(filteredArrayList)
        communityRecyclerView.adapter = communityAdapter
    }

    private fun resetCommunityFilter(buttonTextArray: ArrayList<TextView>) {
        for (buttonText in buttonTextArray) {
            buttonText.setTypeface(null, Typeface.NORMAL)
            buttonText.setTextColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.white
                )
            )
        }
        this.filterApplied = 0
        communityAdapter = CommunityAdapter(communityMainList)
        communityRecyclerView.adapter = communityAdapter
    }

    private fun initData(incomingCommunities: ArrayList<CommunityData>) {

        Log.d(ContentValues.TAG, "Init data")
        Log.d(ContentValues.TAG, "incomingCommunities: ${incomingCommunities.size}")
        val nonDuplicates = incomingCommunities.distinct()
        communityMainList = arrayListOf<CommunityMain>()
        for (community in nonDuplicates) {
            val uri = Uri.parse(community.thumbnail)
            val name = community.name
            val lastname = community.lastname
            val description = community.description
            val location = community.location
            val category = community.category
            val whatsapp = community.number
            val instagram = community.instagram
            val facebook = community.facebook
            val imagePost = CommunityMain(uri, name!!, lastname!!, description!!, location!!, category!!, whatsapp!!, instagram!!, facebook!!)
            communityMainList.add(imagePost)
        }

        communityRecyclerView = binding.imageListRecycler
        communityRecyclerView.layoutManager = layoutManager
        communityRecyclerView.setHasFixedSize(true)
        communityAdapter = CommunityAdapter(communityMainList)
        communityRecyclerView.adapter = communityAdapter

        val buttonArray = arrayListOf<TextView>(
            binding.buttonProductivosText,
            binding.buttonCulturaText,
            binding.buttonTurismoText
        )
        binding.buttonProductivos.setOnClickListener {
            if (this.filterApplied == 1) resetCommunityFilter(buttonArray)
            else {
                resetCommunityFilter(buttonArray)
                applyFilter(
                    1,
                    "productivos",
                    binding.buttonProductivosText,
                    R.color.productivos
                )
            }
        }

        binding.buttonCultura.setOnClickListener {
            if (this.filterApplied == 2) resetCommunityFilter(buttonArray)
            else {
                resetCommunityFilter(buttonArray)
                applyFilter(
                    2,
                    "cultura",
                    binding.buttonCulturaText,
                    R.color.cultura
                )
            }
        }

        binding.buttonTurismo.setOnClickListener {
            if (this.filterApplied == 3) resetCommunityFilter(buttonArray)
            else {
                resetCommunityFilter(buttonArray)
                applyFilter(
                    3,
                    "turismo",
                    binding.buttonTurismoText,
                    R.color.turismo
                )
            }
        }

        binding.communityInfo.setOnClickListener {
            InfoDialog("comunidad").show(requireFragmentManager(), "info dialog")
        }

    }
}