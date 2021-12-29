package com.reglardo.audiogram.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.reglardo.audiogram.R
import com.reglardo.audiogram.fragments.ProfileFragment
import com.reglardo.audiogram.fragments.SearchFragment
import com.reglardo.audiogram.network.UserSearchResponse


class SearchAdapter(
    private val searchFragment: SearchFragment,
    private val searchResponses: List<UserSearchResponse>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val searchLayout : LinearLayout = view.findViewById(R.id.search_layout)
        val userImage: ImageView = view.findViewById(R.id.search_user_image)
        val username: TextView = view.findViewById(R.id.search_username)
        val name: TextView = view.findViewById(R.id.search_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_search, parent, false)

        return SearchViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val search = searchResponses[position]

        val imgUri = "${URL.BASE_URL}${search.image}".toUri().buildUpon().scheme("http").build()
        holder.userImage.load(imgUri)
        holder.username.text = search.username
        holder.name.text = "${search.firstName} ${search.lastName}"

        holder.searchLayout.setOnClickListener {
            replaceFragment(ProfileFragment.newInstance(search.username))
        }
    }

    override fun getItemCount() = searchResponses.size

    private fun replaceFragment(fragment: Fragment) {
        val transaction = searchFragment.parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, "ProfileSearch")
        transaction.commit()
    }
}