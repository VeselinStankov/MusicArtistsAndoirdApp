package com.example.navigationtask.songdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.navigationtask.R
import com.example.navigationtask.common.ui.GalleryAdapter
import com.example.navigationtask.databinding.FragmentSongDetailsBinding

class SongDetailsFragment : Fragment() {

    private lateinit var binding: FragmentSongDetailsBinding
    private val viewModel: SongDetailsViewModel by viewModels()
    private lateinit var args: SongDetailsFragmentArgs
    private lateinit var fanCovers: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initFields()
        initViews()
        binding.initListeners()
        initObservers()

        return binding.root
    }

    private fun initFields() {
        args = SongDetailsFragmentArgs.fromBundle(requireArguments())
        fanCovers = listOf(
            args.song.firstFanCover,
            args.song.secondFanCover,
            args.song.thirdFanCover,
            args.song.fourthFanCover,
        )

        binding = FragmentSongDetailsBinding.inflate(layoutInflater)
    }

    private fun initViews() {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val fanCoverAdapter = GalleryAdapter(fanCovers)
        binding.rvCoverArt.adapter = fanCoverAdapter

        binding.txtTitle.text = args.song.songTitle
        binding.txtArtistName.text = args.song.artistName
        Glide.with(this.requireContext())
            .load(args.song.songCover)
            .placeholder(R.drawable.ic_loading_details_error)
            .into(binding.imgCover)

        binding.infoLikes.setBottomLabelText(args.song.likes.toString())
        binding.infoViews.setBottomLabelText(args.song.views.toString())
        binding.infoComments.setBottomLabelText(args.song.comments.toString())
        binding.infoGenre.setBottomLabelText(args.song.genre)
        binding.txtExpandLyrics.text = args.song.lyrics
    }

    private fun FragmentSongDetailsBinding.initListeners() {
        btnBack.setOnClickListener {
            viewModel.onBackButtonClicked()
        }
    }

    private fun initObservers() {
        viewModel.shouldNavigateBack.consume(viewLifecycleOwner) { shouldNavigateBack ->
            if (shouldNavigateBack) {
                findNavController().popBackStack()
            }
        }
    }
}