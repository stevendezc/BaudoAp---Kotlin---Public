package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentEventsBinding
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.InfoDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    private var currentYear = 0
    private var yearFilter = 0
    private var monthFilter = 0
    private var subjectFilter = 0

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventsRecyclerView: RecyclerView
    private var eventsMainList: ArrayList<EventMain> = arrayListOf<EventMain>()

    private var monthButtonArray = arrayListOf<Button>()

    private val subjectList = arrayListOf<String>(
        "periodismo", "memoria", "genero", "ambiente"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)

        this.monthButtonArray = arrayListOf<Button>(
            binding.month1, binding.month2, binding.month3, binding.month4,
            binding.month5, binding.month6, binding.month7, binding.month8,
            binding.month9, binding.month10, binding.month11, binding.month12
        )
        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        db.collection("events").get().addOnSuccessListener {
            val eventDataList = mutableListOf<EventMain>()
            for (document in it) {
                println("DOCUMENT: $document")
                val myData = document.toObject(EventMain::class.java)
                myData.id = document.id
                eventDataList.add(myData)
            }
            eventsMainList.addAll(eventDataList)
            initEvents(eventsMainList)
        }
    }

    private fun getCurrentDateValues() {
        var formattedDate = ""
        var month = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentLocale = Locale("es", "ES")
            val currentDate = LocalDate.now()
            month = currentDate.month.value
            val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", currentLocale)
            formattedDate = currentDate.format(formatter)
            currentYear = currentDate.year
        } else {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
            formattedDate = dateFormat.format(currentDate)
            val oldCurrentDate = Calendar.getInstance()
            currentYear = oldCurrentDate.get(Calendar.YEAR)
            month = oldCurrentDate.get(Calendar.MONTH) + 1
        }
        yearFilter = currentYear
        binding.currentDate.text = formattedDate
        binding.currentYear.text = currentYear.toString()
        binding.incomingYear.text = (currentYear + 1).toString()
        val monthButton = monthButtonArray[month - 1]
        activeMonthFilter(month, monthButton)
        applyFilters()
    }

    private fun initEvents(eventsMainList: ArrayList<EventMain>) {
        eventsRecyclerView = binding.eventListRecycler
        eventsRecyclerView.layoutManager = layoutManager
        eventsRecyclerView.setHasFixedSize(true)
        val nonDuplicatedList = eventsMainList.distinct()
        val nonDuplicatedArrayList = arrayListOf<EventMain>()
        nonDuplicatedList.forEach { event: EventMain ->
            nonDuplicatedArrayList.add(event)
        }
        eventAdapter = EventAdapter(requireContext(), requireFragmentManager(), nonDuplicatedArrayList)
        eventsRecyclerView.adapter = eventAdapter

        getCurrentDateValues()

        binding.currentYear.textSize = 18f
        binding.currentYear.setTextColor(resources.getColor(R.color.baudo_yellow))
        binding.currentYear.setOnClickListener {
            binding.currentYear.textSize = 18f
            binding.currentYear.setTypeface(null, Typeface.BOLD)
            binding.currentYear.setTextColor(resources.getColor(R.color.baudo_yellow))
            binding.incomingYear.textSize = 15f
            binding.incomingYear.setTypeface(null, Typeface.NORMAL)
            binding.incomingYear.setTextColor(resources.getColor(R.color.white))
            yearFilter = currentYear
            applyFilters()
        }
        binding.incomingYear.textSize = 15f
        binding.incomingYear.setOnClickListener {
            binding.incomingYear.textSize = 18f
            binding.incomingYear.setTypeface(null, Typeface.BOLD)
            binding.incomingYear.setTextColor(resources.getColor(R.color.baudo_yellow))
            binding.currentYear.textSize = 15f
            binding.currentYear.setTypeface(null, Typeface.NORMAL)
            binding.currentYear.setTextColor(resources.getColor(R.color.white))
            yearFilter = currentYear + 1
            applyFilters()
        }

        binding.month1.setOnClickListener {
            activeMonthFilter(1, binding.month1)
            applyFilters()
        }
        binding.month2.setOnClickListener {
            activeMonthFilter(2, binding.month2)
            applyFilters()
        }
        binding.month3.setOnClickListener {
            activeMonthFilter(3, binding.month3)
            applyFilters()
        }
        binding.month4.setOnClickListener {
            activeMonthFilter(4, binding.month4)
            applyFilters()
        }
        binding.month5.setOnClickListener {
            activeMonthFilter(5, binding.month5)
            applyFilters()
        }
        binding.month6.setOnClickListener {
            activeMonthFilter(6, binding.month6)
            applyFilters()
        }
        binding.month7.setOnClickListener {
            activeMonthFilter(7, binding.month7)
            applyFilters()
        }
        binding.month8.setOnClickListener {
            activeMonthFilter(8, binding.month8)
            applyFilters()
        }
        binding.month9.setOnClickListener {
            activeMonthFilter(9, binding.month9)
            applyFilters()
        }
        binding.month10.setOnClickListener {
            activeMonthFilter(10, binding.month10)
            applyFilters()
        }
        binding.month11.setOnClickListener {
            activeMonthFilter(11, binding.month11)
            applyFilters()
        }
        binding.month12.setOnClickListener {
            activeMonthFilter(11, binding.month12)
            applyFilters()
        }

        binding.eventsInfo.setOnClickListener {
            InfoDialog("eventos").show(requireFragmentManager(), "info dialog")
        }
    }

    private fun resetMonthFilter() {
        for (button in monthButtonArray) {
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.baudo_blue))
        }
    }

    private fun activeMonthFilter(filterMonth: Int, buttonText: android.widget.Button) {
        resetMonthFilter()
        buttonText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.baudo_yellow))
        this.monthFilter = filterMonth
    }

    private fun applyFilters() {
        var filteredArrayList = arrayListOf<EventMain>()
        filteredArrayList = eventsMainList.filter { event -> event.year == yearFilter && event.month == monthFilter } as ArrayList<EventMain>
        if (subjectFilter != 0) {
            filteredArrayList = eventsMainList.filter { event -> event.year == yearFilter && event.month == monthFilter && event.subject == subjectList[subjectFilter-1] } as ArrayList<EventMain>
        }
        val nonDuplicatedList = filteredArrayList.distinct()
        val nonDuplicatedArrayList = arrayListOf<EventMain>()
        nonDuplicatedList.forEach { event: EventMain ->
            nonDuplicatedArrayList.add(event)
        }
        eventAdapter = EventAdapter(requireContext(), requireFragmentManager(), nonDuplicatedArrayList)
        eventsRecyclerView.adapter = eventAdapter
    }

}