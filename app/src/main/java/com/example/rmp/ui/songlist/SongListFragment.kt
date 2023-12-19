package com.example.rmp.ui.songlist

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rmp.databinding.FragmentSongListBinding
import com.example.rmp.ui.adapters.SongAdapter

class SongListFragment : Fragment() {

    private var _binding: FragmentSongListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerview = binding.recyclerView

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val data = ArrayList<SongListViewModel>()

        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE)
        val musicCursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        val songTitles : MutableList<String> = mutableListOf()
        val songImages : MutableList<ImageView> = mutableListOf()

        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)))
                    .build()
                val songTitle = musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))

                songTitles.add(songTitle)

                val musicImage = ImageView(requireContext())
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(requireContext(), musicUri)
                val byteArray = mediaMetadataRetriever.embeddedPicture

                if (byteArray != null) {
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    musicImage.setImageBitmap(bitmap)
                    songImages.add(musicImage)
                } else {
                    songImages.add(musicImage)
                }
            } while (musicCursor.moveToNext())

        }

        musicCursor?.close()

        for (i in songImages.indices)
        {
            data.add(SongListViewModel(songImages[i], songTitles[i]))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = SongAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}