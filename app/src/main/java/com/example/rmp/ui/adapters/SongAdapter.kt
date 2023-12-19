package com.example.rmp.ui.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.rmp.R
import com.example.rmp.ui.songlist.SongListViewModel

class SongAdapter(private val songs: ArrayList<SongListViewModel>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.song_title)
        val songPhoto: ImageView = view.findViewById(R.id.song_photo)
        val button: ImageButton = view.findViewById(R.id.playThisMusicButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_list_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val DashboardViewModel = songs[position]

        val imageView = DashboardViewModel.image
        holder.songPhoto.setImageDrawable(imageView.drawable)

        holder.songTitle.text = DashboardViewModel.text

        holder.button.setOnClickListener {
            val context = holder.itemView.context

            val navController = Navigation.findNavController(context as Activity, R.id.nav_host_fragment_activity_main)
            val bundle = Bundle()
            bundle.putInt("song_number", position)

            navController.navigate(R.id.navigation_home, bundle)
        }
    }

    override fun getItemCount() = songs.size
}
