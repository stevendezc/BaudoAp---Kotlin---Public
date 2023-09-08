package com.pereira.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.CommunityData
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.FragmentCommunityBinding
import com.pereira.baudoapp.recyclers.CommunityAdapter
import com.pereira.baudoapp.recyclers.CommunityMain
import com.pereira.baudoapp.utils.InfoDialog
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
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
        // Load Communities
        db.collection("communities").get().addOnSuccessListener {
            val communityDataList = mutableListOf<CommunityData>()
            for (document in it) {
                val myData = document.toObject(CommunityData::class.java)
                communityDataList.add(myData)
            }
            // Update your UI with the new data
            binding.contentLoading.visibility = ProgressBar.GONE
            binding.filterButtons.visibility = LinearLayout.VISIBLE
            binding.imageListRecycler.visibility = RecyclerView.VISIBLE
            // Setup subfragments
            val communitiesArrayList: ArrayList<CommunityData> = ArrayList()
            communitiesArrayList.addAll(communityDataList)
            initData(communitiesArrayList)
        }
    }

    private fun applyFilter(filterNumber: Int, categoryName: String, button: LinearLayout, buttonIcon: ImageView, buttonText: TextView, categoryColor: Int) {
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), categoryColor))
        buttonIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        buttonText.setTypeface(null, Typeface.BOLD)
        buttonText.setTextColor(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.white
            )
        )
        this.filterApplied = filterNumber
        var filteredArrayList = arrayListOf<CommunityMain>()
        filteredArrayList = communityMainList.filter { it.category == categoryName } as ArrayList<CommunityMain>
        communityAdapter = CommunityAdapter(filteredArrayList)
        communityRecyclerView.adapter = communityAdapter
    }

    private fun resetCommunityFilter() {
        binding.buttonProductivos.setBackgroundResource(R.drawable.productivos_button)
        binding.buttonCultura.setBackgroundResource(R.drawable.cultura_button)
        binding.buttonTurismo.setBackgroundResource(R.drawable.turismo_button)
        binding.buttonProductivos.backgroundTintList = null
        binding.buttonCultura.backgroundTintList = null
        binding.buttonTurismo.backgroundTintList = null
        val buttonArrayTexts = arrayListOf<TextView>(
            binding.buttonProductivosText,
            binding.buttonCulturaText,
            binding.buttonTurismoText
        )
        binding.buttonProductivosIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.productivos))
        binding.buttonCulturaIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.cultura))
        binding.buttonTurismoIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.turismo))

        for (buttonText in buttonArrayTexts) {
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

        binding.buttonProductivos.setOnClickListener {
            if (this.filterApplied == 1) resetCommunityFilter()
            else {
                resetCommunityFilter()
                applyFilter(
                    1,
                    "productivos",
                    binding.buttonProductivos,
                    binding.buttonProductivosIcon,
                    binding.buttonProductivosText,
                    R.color.productivos
                )
            }
        }

        binding.buttonCultura.setOnClickListener {
            if (this.filterApplied == 2) resetCommunityFilter()
            else {
                resetCommunityFilter()
                applyFilter(
                    2,
                    "cultura",
                    binding.buttonCultura,
                    binding.buttonCulturaIcon,
                    binding.buttonCulturaText,
                    R.color.cultura
                )
            }
        }

        binding.buttonTurismo.setOnClickListener {
            if (this.filterApplied == 3) resetCommunityFilter()
            else {
                resetCommunityFilter()
                applyFilter(
                    3,
                    "turismo",
                    binding.buttonTurismo,
                    binding.buttonTurismoIcon,
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