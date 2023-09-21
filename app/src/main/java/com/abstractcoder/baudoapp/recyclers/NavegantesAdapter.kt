package com.pereira.baudoapp.recyclers

import android.animation.LayoutTransition
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.pereira.baudoapp.BrowserActivity
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.NavegantesListItemBinding
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

        holder.Button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(currentItem.btn_color))
        holder.Button.setOnClickListener { openLink(holder, currentItem.link!!) }
        holder.Title.text = currentItem.title
        holder.Price.text = currentItem.price
        holder.supportText.text = currentItem.support_text
        holder.extraInfo.text = currentItem.extra_info

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

        val Button = binding.navegantesPaymentbutton
        val Title = binding.navegantesTitle
        val Price = binding.navegantesPrice
        val supportText = binding.navegantesSupportText
        val extraInfo = binding.navegantesExtraInfo
        val navegantesMoreinfo = binding.navegantesMoreinfo
        val navegantesInfoContainer = binding.navegantesInfoContainer

    }
}