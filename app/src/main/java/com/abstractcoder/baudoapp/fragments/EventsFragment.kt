package com.abstractcoder.baudoapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
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
import com.abstractcoder.baudoapp.recyclers.*
import com.abstractcoder.baudoapp.utils.Firestore
import com.abstractcoder.baudoapp.utils.InfoDialog
import java.text.SimpleDateFormat
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
    private var subjectFilter = 0

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventsRecyclerView: RecyclerView
    private var eventsMainList: ArrayList<EventMain> = arrayListOf<EventMain>()

    private var monthButtonArray = arrayListOf<Button>()
    private var subjectButtonArray = arrayListOf<Any>()

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

        /*this.subjectButtonArray = arrayListOf<Any>(
            binding.periodismoButton, binding.memoriaButton,
            binding.generoButton, binding.ambienteButton
        )*/

        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        firestore.activateSubscribers(requireContext(), email!!)
        firestore.eventsLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { events ->
            // Update your UI with the new data
            Log.d(ContentValues.TAG, "events on EventsFragment: $events")
            eventsMainList.addAll(events)
            initEvents(eventsMainList)
        })
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

        /*binding.periodismoButton.setOnClickListener {
            if (this.subjectFilter == 1) {
                resetSubjectFilter()
                applyFilters()
            }
            else {
                activeSubjectFilter(1, binding.periodismoButton)
                applyFilters()
            }
        }
        binding.memoriaButton.setOnClickListener {
            if (this.subjectFilter == 2) {
                resetSubjectFilter()
                applyFilters()
            }
            else {
                activeSubjectFilter(2, binding.memoriaButton)
                applyFilters()
            }
        }
        binding.generoButton.setOnClickListener {
            if (this.subjectFilter == 3) {
                resetSubjectFilter()
                applyFilters()
            }
            else {
                activeSubjectFilter(3, binding.generoButton)
                applyFilters()
            }
        }
        binding.ambienteButton.setOnClickListener {
            if (this.subjectFilter == 4) {
                resetSubjectFilter()
                applyFilters()
            }
            else {
                activeSubjectFilter(4, binding.ambienteButton)
                applyFilters()
            }
        }*/

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

    /*private fun resetSubjectFilter() {
        for (subjectButton in subjectButtonArray) {
            when (subjectButton) {
                is android.widget.LinearLayout -> {
                    subjectButton.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.baudo_grey
                        )
                    )
                    binding.memoriaButtonLabel1.setTextColor(ContextCompat.getColor(
                        this.requireContext(),
                        R.color.white
                    ))
                    binding.memoriaButtonLabel2.setTextColor(ContextCompat.getColor(
                        this.requireContext(),
                        R.color.white
                    ))
                }
                is android.widget.Button -> {
                    subjectButton.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.baudo_grey
                        )
                    )
                    subjectButton.setTextColor(
                        ContextCompat.getColor(
                            this.requireContext(),
                            R.color.white
                        )
                    )
                }
            }
        }
        this.subjectFilter = 0
    }
    private fun activeSubjectFilter(filterSubject: Int, layoutButton: Any) {
        resetSubjectFilter()
        when (layoutButton) {
            is android.widget.LinearLayout -> {
                layoutButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                binding.memoriaButtonLabel1.setTextColor(ContextCompat.getColor(
                    this.requireContext(),
                    R.color.baudo_grey
                ))
                binding.memoriaButtonLabel2.setTextColor(ContextCompat.getColor(
                    this.requireContext(),
                    R.color.baudo_grey
                ))
            }
            is android.widget.Button -> {
                layoutButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                layoutButton.setTextColor(
                    ContextCompat.getColor(
                        this.requireContext(),
                        R.color.baudo_grey
                    )
                )
            }
        }
        this.subjectFilter = filterSubject
    }*/

    private fun applyFilters() {
        var filteredArrayList = arrayListOf<EventMain>()
        filteredArrayList = eventsMainList.filter { it.year == yearFilter && it.month == monthFilter } as ArrayList<EventMain>
        if (subjectFilter != 0) {
            filteredArrayList = eventsMainList.filter { it.year == yearFilter && it.month == monthFilter && it.subject == subjectList[subjectFilter-1] } as ArrayList<EventMain>
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