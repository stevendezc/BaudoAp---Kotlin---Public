package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutPaymentBinding
import com.abstractcoder.baudoapp.utils.CustomArrayAdapter
import com.abstractcoder.baudoapp.utils.JsonFile
import com.abstractcoder.baudoapp.utils.wompi.FinancialInstitution
import com.abstractcoder.baudoapp.utils.wompi.FinancialInstitutionsResponse
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CheckoutPaymentFragment : Fragment() {
    private var _binding: FragmentCheckoutPaymentBinding? = null
    private val binding get() = _binding!!

    private var selectedMethod = 0
    private lateinit var entidadList: List<String>

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getInstitutions() {
        val client = OkHttpClient()
        // Request
        val request = Request.Builder()
            .url("https://sandbox.wompi.co/v1/pse/financial_institutions")
            .method("GET", null)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer pub_test_16jNk5Ea0ME5n2j1RLnOo28t0f1Fia0m")
            .build()
        // Enqueue request
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("HTTP Error", "Algo no cargo bien")
                    } else {
                        val body = response.body()?.string()
                        val gson = Gson()
                        val dataModel = gson.fromJson(body, FinancialInstitutionsResponse::class.java)
                        entidadList = dataModel.data.map { it.financial_institution_name }
                        println("entidadList: $entidadList")
                        binding.sample.text = entidadList.toString()
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            if (selectedMethod == 0) {
                listener?.onDataReceived("contact")
            } else {
                selectedMethod = 0
                binding.paymentMethods.visibility = LinearLayout.VISIBLE
                binding.ccPayment.visibility = LinearLayout.GONE
                binding.psePayment.visibility = LinearLayout.GONE
            }
        }

        //getInstitutions()
        var jsonData = JsonFile().readJsonFile(requireContext(), "entidades_financieras.json")
        var parsedJsonData = JSONObject(jsonData)

        var entidadJson = parsedJsonData.get("entidades") as JSONObject
        var entidadJsonArray = entidadJson.get("data") as JSONArray
        val entidadArray = Array(entidadJsonArray.length()) { entidadJsonArray.get(it) as JSONObject }
        val arrayOfProperty1 = entidadArray.map { it.get("financial_institution_name").toString() }.toTypedArray()
        val entidadInputAdapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOfProperty1)
        entidadInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.entidadInput.adapter = entidadInputAdapter

        jsonData = JsonFile().readJsonFile(requireContext(), "pse_inputs.json")
        parsedJsonData = JSONObject(jsonData)

        var tipoPersonaJson = parsedJsonData.get("tipo_persona") as JSONArray
        var tipoPersonaArray = Array(tipoPersonaJson.length()) { tipoPersonaJson.getString(it) }
        val personaInputAdapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipoPersonaArray)
        personaInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.personaInput.adapter = personaInputAdapter

        var tipoDocumentoJson = parsedJsonData.get("tipo_documento") as JSONArray
        var tipoDocumentoArray = Array(tipoDocumentoJson.length()) { tipoDocumentoJson.getString(it) }
        val documentoInputAdapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipoDocumentoArray)
        documentoInputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tipoDocumentoInput.adapter = documentoInputAdapter

        binding.pseButton.setOnClickListener {
            selectedMethod = 1
            binding.paymentMethods.visibility = LinearLayout.GONE
            binding.ccPayment.visibility = LinearLayout.GONE
            binding.psePayment.visibility = LinearLayout.VISIBLE
        }

        binding.ccButton.setOnClickListener {
            selectedMethod = 2
            binding.paymentMethods.visibility = LinearLayout.GONE
            binding.psePayment.visibility = LinearLayout.GONE
            binding.ccPayment.visibility = LinearLayout.VISIBLE
        }

        binding.ccSubmit.setOnClickListener {
            val validPaymentForm = checkPaymentForm()
            if (validPaymentForm) {
                Toast.makeText(requireContext(), "Informacion de pago valida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Informacion de pago invalida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPaymentForm(): Boolean {
        if (!binding.cardFormCardHolder.isValid) return false
        if (!binding.cardFormCardNumber.isValid) return false
        if (!binding.cardFormCardExpiration.isValid) return false
        if (!binding.cardFormCardCvv.isValid) return false
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }
}