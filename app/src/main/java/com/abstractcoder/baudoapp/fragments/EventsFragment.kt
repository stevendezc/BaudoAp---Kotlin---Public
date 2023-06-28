package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.CommunityData
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentEventsBinding
import com.abstractcoder.baudoapp.recyclers.CommunityAdapter
import com.abstractcoder.baudoapp.recyclers.CommunityMain
import com.abstractcoder.baudoapp.recyclers.EventAdapter
import com.abstractcoder.baudoapp.recyclers.EventMain
import com.abstractcoder.baudoapp.utils.Firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private var firestore = Firestore()
    private lateinit var sharedPreferences: SharedPreferences

    private var currentYear = 0
    private var yearFilter = 0
    private var monthFilter = 0
    private var subjectFilter = ""

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventsRecyclerView: RecyclerView
    private var eventsMainList: ArrayList<EventMain> = arrayListOf<EventMain>()

    private var monthButtonArray = arrayListOf<Button>()

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
        val email = sharedPreferences.getString("email", "")
        firestore.activateSubscribers(requireContext(), email!!)
        firestore.eventsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { events ->
            // Update your UI with the new data
            //binding.contentLoading.visibility = ProgressBar.GONE
            //binding.filterButtons.visibility = LinearLayout.VISIBLE
            //binding.imageListRecycler.visibility = RecyclerView.VISIBLE
            Log.d(ContentValues.TAG, "events on EventsFragment: $events")
            //val eventsArrayList: ArrayList<EventMain> = ArrayList()
            eventsMainList.addAll(events)
            initEvents(eventsMainList)
        })
    }

    private fun getCurrentDateValues() {
        val currentLocale = Locale("es", "ES")
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", currentLocale)
        val formattedDate = currentDate.format(formatter)
        currentYear = currentDate.year
        yearFilter = currentYear
        binding.currentDate.text = formattedDate
        binding.currentYear.text = currentYear.toString()
        binding.incomingYear.text = (currentYear + 1).toString()
        val monthButton = monthButtonArray[currentDate.month.value - 1]
        activeMonthFilter(currentDate.month.value, monthButton)
        applyFilters()
    }

    private fun initEvents(eventsMainList: ArrayList<EventMain>) {
        eventsRecyclerView = binding.eventListRecycler
        eventsRecyclerView.layoutManager = layoutManager
        eventsRecyclerView.setHasFixedSize(true)
        eventAdapter = EventAdapter(eventsMainList)
        eventsRecyclerView.adapter = eventAdapter

        getCurrentDateValues()

        binding.currentYear.textSize = 18f
        binding.currentYear.setOnClickListener {
            binding.currentYear.textSize = 18f
            binding.currentYear.setTypeface(null, Typeface.BOLD)
            binding.incomingYear.textSize = 15f
            binding.incomingYear.setTypeface(null, Typeface.NORMAL)
            yearFilter = currentYear
            applyFilters()
        }
        binding.incomingYear.textSize = 15f
        binding.incomingYear.setOnClickListener {
            binding.incomingYear.textSize = 18f
            binding.incomingYear.setTypeface(null, Typeface.BOLD)
            binding.currentYear.textSize = 15f
            binding.currentYear.setTypeface(null, Typeface.NORMAL)
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
    }

    private fun resetMonthFilter() {
        for (button in monthButtonArray) {
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.baudo_blue))
            /*button.setTextColor(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.white
                )
            )*/
        }
    }

    private fun activeMonthFilter(filterMonth: Int, buttonText: android.widget.Button) {
        resetMonthFilter()
        buttonText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.baudo_yellow))
        this.monthFilter = filterMonth
    }

    private fun applyFilters() {
        var filteredArrayList = arrayListOf<EventMain>()
        filteredArrayList = eventsMainList.filter { it.year == yearFilter && it.month == monthFilter } as ArrayList<EventMain>
        eventAdapter = EventAdapter(filteredArrayList)
        eventsRecyclerView.adapter = eventAdapter
    }

}