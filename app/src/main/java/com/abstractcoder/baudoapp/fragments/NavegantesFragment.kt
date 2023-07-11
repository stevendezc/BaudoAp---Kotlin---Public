package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.NavegantesLinks
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentNavegantesBinding
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.abstractcoder.baudoapp.utils.JsonFile
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException

class NavegantesFragment : Fragment() {

    private var _binding: FragmentNavegantesBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var navegantesLinks: NavegantesLinks

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var navegantesAdapter: NavegantesAdapter
    private lateinit var navegantesRecyclerView: RecyclerView
    private var navegantesMainList: ArrayList<NavegantesMain> = arrayListOf<NavegantesMain>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavegantesBinding.inflate(inflater, container, false)

        binding.navegantesInfo.setOnClickListener {
            InfoDialog("navegantes").show(requireFragmentManager(), "info dialog")
        }

        return binding.root
    }

    private fun getEventDrawables(type: Int): Int {
        val eventsImages = listOf<Int>(
            R.drawable.rio,
            R.drawable.riachuelo,
            R.drawable.quebrada,
            R.drawable.libre
        )
        return eventsImages.get(type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db.collection("navegantes_links").get().addOnSuccessListener {
            for (document in it) {
                navegantesLinks = document.toObject(NavegantesLinks::class.java)
                println("navegantesLinks: $navegantesLinks")
                val jsonData = JsonFile().readJsonFile(requireContext(), "navegantes_info.json")
                val parsedJsonData = JSONObject(jsonData)
                for (key in parsedJsonData.keys()) {
                    val mesKey = "${key}_mes"
                    val anualKey = "${key}_anual"
                    val mesLink = navegantesLinks[mesKey]
                    val anualLink = navegantesLinks[anualKey]
                    var eventObject = parsedJsonData.get(key) as JSONObject
                    var eventDrawable = getEventDrawables(eventObject.get("type") as Int)
                    println("eventObject type: ${eventObject::class.java}")
                    println("eventObject: $eventObject")
                    val navegantesItem = NavegantesMain(
                        eventObject.get("title").toString(),
                        eventObject.get("monthly_price").toString(),
                        eventObject.get("yearly_price").toString(),
                        eventObject.get("has_input") as Boolean,
                        eventDrawable,
                        if (key != "libre") mesLink else  navegantesLinks["libre"],
                        if (key != "libre") anualLink else "",
                        eventObject.get("btn_color").toString(),
                        eventObject.get("info_1").toString(),
                        eventObject.get("info_2").toString(),
                        eventObject.get("info_3").toString(),
                    )
                    navegantesMainList.add(navegantesItem)
                }

                layoutManager = LinearLayoutManager(context)
                navegantesRecyclerView = binding.navegantesListRecycler
                navegantesRecyclerView.layoutManager = layoutManager
                navegantesRecyclerView.setHasFixedSize(true)
                navegantesAdapter = NavegantesAdapter(resources, navegantesMainList)
                navegantesRecyclerView.adapter = navegantesAdapter
            }
        }

    }

}