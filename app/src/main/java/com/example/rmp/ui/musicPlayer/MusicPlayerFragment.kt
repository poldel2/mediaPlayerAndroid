package com.example.rmp.ui.musicPlayer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rmp.databinding.FragmentMusicPlayerBinding


class MusicPlayerFragment : Fragment() {

    private var _binding: FragmentMusicPlayerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mediaPlayers: MutableList<MediaPlayer>
    private lateinit var songTitles: MutableList<String>
    private var currentSongIndex = 0
    private lateinit var seekBar: SeekBar
    private lateinit var chronometerTotal: Chronometer
    private lateinit var chronometerCurrent: Chronometer
    private lateinit var musicImage: ImageView
    private lateinit var musicNameTextView: TextView
    private lateinit var nextMusicButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val musicPlayerViewModel =
            ViewModelProvider(this).get(MusicPlayerViewModel::class.java)

        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)

        val root: View = binding.root

        if (arguments?.isEmpty == false)
            currentSongIndex = arguments?.getInt("song_number")!!

        val playMusicButton: ImageButton = binding.playMusicButton
        nextMusicButton = binding.nextMusicButton
        val previousMusicButton: ImageButton = binding.previousMusicButton
        val rewindBackButton: ImageButton = binding.rewindBack
        val rewindForwardButton: ImageButton = binding.rewindForward
        seekBar= binding.seekBar
        chronometerCurrent = binding.chronometerCurrent
        chronometerTotal = binding.chronometerTotal
        musicImage = binding.musicImage
        musicNameTextView = binding.musicNameTextView

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        } else {
            prepareMediaPlayers()
        }

        playMusicButton.setOnClickListener {
            if (mediaPlayers[currentSongIndex].isPlaying) {
                mediaPlayers[currentSongIndex].pause()
                println("Paused: ${songTitles[currentSongIndex]}")
                chronometerCurrent.stop()
            } else {
                mediaPlayers[currentSongIndex].start()
                println("Playing: ${songTitles[currentSongIndex]}")
                setCurrentChronometer()
                chronometerCurrent.start()
            }
        }

        nextMusicButton.setOnClickListener {
            mediaPlayers[currentSongIndex].seekTo(0)
            if (mediaPlayers[currentSongIndex].isPlaying)
                mediaPlayers[currentSongIndex].stop()

            currentSongIndex = (currentSongIndex + 1) % mediaPlayers.size

            seekBar.max = mediaPlayers[currentSongIndex].duration
            val duration = 0
            chronometerCurrent.base = SystemClock.elapsedRealtime() - duration
            seekBar.progress = 0
            if(chronometerCurrent.isActivated)
                chronometerCurrent.stop()

            setTotalChronometer()
            getMusicImage()
            getMusicName()
        }

        previousMusicButton.setOnClickListener {
            mediaPlayers[currentSongIndex].seekTo(0)
            if (mediaPlayers[currentSongIndex].isPlaying)
                mediaPlayers[currentSongIndex].stop()

            currentSongIndex = (currentSongIndex - 1 + mediaPlayers.size) % mediaPlayers.size

            seekBar.max = mediaPlayers[currentSongIndex].duration
            val duration = 0
            chronometerCurrent.base = SystemClock.elapsedRealtime() - duration
            seekBar.progress = 0
            if(chronometerCurrent.isActivated)
                chronometerCurrent.stop()

            setTotalChronometer()
            getMusicImage()
            getMusicName()
        }

        rewindBackButton.setOnClickListener {
            val currentPosition = mediaPlayers[currentSongIndex].currentPosition
            mediaPlayers[currentSongIndex].seekTo(currentPosition - 10000)
            setCurrentChronometer()
        }

        rewindForwardButton.setOnClickListener {
            val currentPosition = mediaPlayers[currentSongIndex].currentPosition
            mediaPlayers[currentSongIndex].seekTo(currentPosition + 10000)
            setCurrentChronometer()
        }

        chronometerCurrent.setOnChronometerTickListener {
            seekBar.progress = mediaPlayers[currentSongIndex].currentPosition
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayers[currentSongIndex].seekTo(progress)
                    setCurrentChronometer()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return root
    }

    private fun prepareMediaPlayers() {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE)
        val musicCursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        mediaPlayers = mutableListOf()
        songTitles = mutableListOf()

        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()
                    .appendPath(musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)))
                    .build()
                val songTitle = musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val mediaPlayer = MediaPlayer.create(requireContext(), musicUri)
                mediaPlayers.add(mediaPlayer)
                songTitles.add(songTitle)
            } while (musicCursor.moveToNext())


            for (i in 0 until mediaPlayers.size) {
                mediaPlayers[i].setOnCompletionListener {
                    nextMusicButton.performClick()
                }
            }
            seekBar.max = mediaPlayers[currentSongIndex].duration

            val duration = mediaPlayers[currentSongIndex].duration.toLong()
            chronometerTotal.base = SystemClock.elapsedRealtime() - duration
            getMusicImage()
            getMusicName()
        }

        musicCursor?.close()
    }

    private fun getMusicImage() {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE)
        val musicCursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        if (musicCursor != null && musicCursor.moveToPosition(currentSongIndex)) {
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()

                .appendPath(musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)))
                .build()

            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(requireContext(), musicUri)
            val byteArray = mediaMetadataRetriever.embeddedPicture

            if (byteArray != null) {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                musicImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun getMusicName() {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE)
        val musicCursor = requireActivity().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        if (musicCursor != null && musicCursor.moveToPosition(currentSongIndex)) {
            val songTitle = musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            musicNameTextView.text = songTitle
        }

        musicCursor?.close()

    }

    private fun setCurrentChronometer() {
        if(mediaPlayers[currentSongIndex].isPlaying) {
            val duration = mediaPlayers[currentSongIndex].currentPosition.toLong()
            chronometerCurrent.base = SystemClock.elapsedRealtime() - duration
            seekBar.progress = mediaPlayers[currentSongIndex].currentPosition
        }
    }

    private fun setTotalChronometer() {
        val duration = mediaPlayers[currentSongIndex].duration.toLong()
        chronometerTotal.base = SystemClock.elapsedRealtime() - duration
    }

    override fun onDestroyView() {
        mediaPlayers.forEach { it.release() }
        super.onDestroyView()
        _binding = null
    }
}