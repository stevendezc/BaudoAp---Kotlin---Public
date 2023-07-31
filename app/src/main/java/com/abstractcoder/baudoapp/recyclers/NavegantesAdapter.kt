package com.abstractcoder.baudoapp.recyclers

import android.animation.LayoutTransition
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.BrowserActivity
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.NavegantesListItemBinding
import kotlin.collections.ArrayList

class NavegantesAdapter(private val resources: Resources, private val navegantesList: ArrayList<NavegantesMain>) : RecyclerView.Adapter<NavegantesAdapter.NavegantesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavegantesHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.navegantes_list_item,
            parent, false)
        return NavegantesAdapter.NavegantesHolder(itemView)
    }

    private fun openLink(holder: NavegantesHolder, link: String) {
        var browserIntent = Intent(holder.itemView.context, BrowserActivity::class.java).apply {
            putExtra("incoming_url", link)
        }
        holder.itemView.context.startActivity(browserIntent)
    }

    override fun onBindViewHolder(holder: NavegantesHolder, position: Int) {
        val currentItem = navegantesList[position]

        val bitmap = BitmapFactory.decodeResource(resources, currentItem.image!!) // Replace with your JPG image resource
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedBitmapDrawable.cornerRadius = resources.getDimension(R.dimen.item_spacing) // Set the desired corner radius
        holder.imageContainer.background = roundedBitmapDrawable

        holder.title.text = currentItem.title
        if (currentItem.has_input == true) {
            holder.noInputButtonContainer.visibility = LinearLayout.GONE
            holder.navegantesSubtitle.text = "Apoya nuestro trabajo y sé parte de nuestros financiadores."
        } else {
            holder.inputButtonContainer.visibility = LinearLayout.GONE
        }
        holder.monthButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(currentItem.btn_color))
        holder.yearButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor(currentItem.btn_color))
        holder.paymentSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor(currentItem.btn_color))

        holder.monthButton.setOnClickListener { openLink(holder, currentItem.link_1!!) }
        holder.yearButton.setOnClickListener { openLink(holder, currentItem.link_2!!) }
        holder.paymentSubmit.setOnClickListener { openLink(holder, currentItem.link_1!!) }

        holder.monthlyPrice.text = currentItem.monthly_price
        holder.yearlyPrice.text = currentItem.yearly_price
        holder.info1.text = currentItem.info_1
        if (currentItem.info_2 != "") {
            holder.info2.visibility = TextView.VISIBLE
            holder.info2.text = currentItem.info_2
        }
        if (currentItem.info_3 != "") {
            holder.info3.visibility = TextView.VISIBLE
            holder.info3.text = currentItem.info_3
        }

        holder.navegantesInfoContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        holder.navegantesMoreinfo.setOnClickListener {
            val layoutVisibility = if(holder.navegantesInfoContainer.visibility == LinearLayout.GONE) LinearLayout.VISIBLE else LinearLayout.GONE
            TransitionManager.beginDelayedTransition(holder.navegantesInfoContainer, AutoTransition())
            holder.navegantesInfoContainer.visibility = layoutVisibility
        }
    }

    override fun getItemCount(): Int {
        return navegantesList.size
    }

    class NavegantesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = NavegantesListItemBinding.bind(itemView)

        val imageContainer = binding.imageContainer
        val title = binding.navegantesTitle
        val noInputButtonContainer = binding.noInputButtons
        val monthButton = binding.monthButton
        val monthlyPrice = binding.monthlyPrice
        val yearButton = binding.yearButton
        val yearlyPrice = binding.yearlyPrice
        val inputButtonContainer = binding.inputButtons
        val navegantesSubtitle = binding.navegantesSubtitle
        val navegantesMoreinfo = binding.navegantesMoreinfo
        val navegantesInfoContainer = binding.navegantesInfoContainer
        val paymentSubmit = binding.librePaymentSubmit
        val info1 = binding.info1
        val info2 = binding.info2
        val info3 = binding.info3

    }
}