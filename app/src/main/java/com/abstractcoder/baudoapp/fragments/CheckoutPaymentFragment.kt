package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutPaymentBinding
import com.abstractcoder.baudoapp.utils.*
import org.json.JSONArray
import org.json.JSONObject

class CheckoutPaymentFragment : Fragment() {
    private var _binding: FragmentCheckoutPaymentBinding? = null
    private val binding get() = _binding!!

    private var selectedMethod = 0
    private lateinit var entidadList: List<String>

    private var listener: OnFragmentInteractionListener? = null
    private var sharedCheckoutData: CheckoutData = CheckoutData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutPaymentBinding.inflate(inflater, container, false)

        val checkout_data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("checkout_data", CheckoutData::class.java)
        } else {
            arguments?.getParcelable("checkout_data") as? CheckoutData
        }
        if (checkout_data != null) {
            sharedCheckoutData = checkout_data
        }
        println("checkout_data in payment: $checkout_data")

        return binding.root
    }

    private fun fillContactData(entity_array: Array<String>, person_type_array: Array<String>, doc_type_array: Array<String>) {
        val entity_selected_index = entity_array.indexOf(sharedCheckoutData.pse_payment_info?.banking_entity)
        val person_selected_index = person_type_array.indexOf(sharedCheckoutData.pse_payment_info?.person)
        val doctype_selected_index = doc_type_array.indexOf(sharedCheckoutData.pse_payment_info?.doc_type)
        println("entity_selected_index: $entity_selected_index")
        println("person_selected_index: $person_selected_index")
        println("doctype_selected_index: $doctype_selected_index")
        // PSE INPUTS
        binding.entidadInput.setSelection(entity_selected_index)
        binding.personaInput.setSelection(person_selected_index)
        binding.tipoDocumentoInput.setSelection(doctype_selected_index)
        binding.documentInput.setText(sharedCheckoutData.pse_payment_info?.doc)
        // CC INPUTS
        binding.cardFormCardHolder.setText(sharedCheckoutData.cc_payment_info?.card_holder)
        sharedCheckoutData.cc_payment_info?.card_number?.let { binding.cardFormCardNumber.setText(it.toString()) }
        binding.cardFormCardExpiration.setText(sharedCheckoutData.cc_payment_info?.card_expiration)
        sharedCheckoutData.cc_payment_info?.card_cvv?.let { binding.cardFormCardCvv.setText(it) }
    }

    private fun fillCheckoutData(type: String):CheckoutData {
        return CheckoutData(
            type,
            sharedCheckoutData.subtotal,
            sharedCheckoutData.contact_info,
            PsePaymentInfo(
                binding.entidadInput.selectedItem.toString(),
                binding.personaInput.selectedItem.toString(),
                binding.tipoDocumentoInput.selectedItem.toString(),
                binding.documentInput.text.toString()
            ),
            CcPaymentInfo(
                binding.cardFormCardHolder.text.toString(),
                if (binding.cardFormCardNumber.text.toString() == "") null else binding.cardFormCardNumber.text.toString(),
                binding.cardFormCardExpiration.text.toString(),
                if (binding.cardFormCardCvv.text.toString() == "") null else binding.cardFormCardCvv.text.toString()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            if (selectedMethod == 0) {
                val checkoutData = fillCheckoutData("contact")
                listener?.onDataReceived(checkoutData)
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
        val entidadJsonObjectArray = Array(entidadJsonArray.length()) { entidadJsonArray.get(it) as JSONObject }
        val entidadArray = entidadJsonObjectArray.map { it.get("financial_institution_name").toString() }.toTypedArray()
        val entidadInputAdapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, entidadArray)
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

        if (sharedCheckoutData.pse_payment_info != null || sharedCheckoutData.cc_payment_info != null) {
            fillContactData(entidadArray, tipoPersonaArray, tipoDocumentoArray)
        }

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
            val validPaymentForm = checkCcPaymentForm()
            if (validPaymentForm) {
                Toast.makeText(requireContext(), "Informacion de pago valida", Toast.LENGTH_SHORT).show()
                val checkoutData = fillCheckoutData("cc_submit")
                listener?.onDataReceived(checkoutData)
            } else {
                Toast.makeText(requireContext(), "Informacion de pago invalida", Toast.LENGTH_SHORT).show()
            }
        }

        binding.pseSubmit.setOnClickListener {
            val validPaymentForm = checkPsePaymentForm()
            if (validPaymentForm) {
                Toast.makeText(requireContext(), "Informacion de pago valida", Toast.LENGTH_SHORT).show()
                val checkoutData = fillCheckoutData("pse_submit")
                listener?.onDataReceived(checkoutData)
            } else {
                Toast.makeText(requireContext(), "Informacion de pago invalida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCcPaymentForm(): Boolean {
        if (!binding.cardFormCardHolder.isValid) return false
        if (!binding.cardFormCardNumber.isValid) return false
        if (!binding.cardFormCardExpiration.isValid) return false
        if (!binding.cardFormCardCvv.isValid) return false
        return true
    }

    private fun checkPsePaymentForm(): Boolean {
        if (binding.entidadInput.selectedItemId.toString() == "0") return false
        if (binding.personaInput.selectedItemId.toString() == "0") return false
        if (binding.tipoDocumentoInput.selectedItemId.toString() == "0") return false
        if (binding.documentInput.text.toString() == "") return false
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

    companion object {
        fun newInstance(checkout_data: CheckoutData): CheckoutPaymentFragment {
            val fragment = CheckoutPaymentFragment()
            val args = Bundle()
            args.putParcelable("checkout_data", checkout_data)
            fragment.arguments = args
            return fragment
        }
    }
}