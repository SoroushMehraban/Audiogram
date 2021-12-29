package com.reglardo.audiogram.adapter

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.MediaViewModel
import com.reglardo.audiogram.R
import com.reglardo.audiogram.fragments.RecorderFragment
import com.reglardo.audiogram.network.UserSearchResponse
import org.w3c.dom.Text


class SearchAdapter(
    private val searchResponses: List<UserSearchResponse>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
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
        Log.i("SearchProfile", imgUri.toString())
        holder.userImage.load(imgUri)
        holder.username.text = search.username
        holder.name.text = "${search.firstName} ${search.lastName}"
    }

    override fun getItemCount() = searchResponses.size
}