package com.example.navigationtask.artistdetails

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
import com.example.navigationtask.databinding.FragmentArtistDetailsBinding

class ArtistDetailsFragment : Fragment() {

    private lateinit var binding: FragmentArtistDetailsBinding
    private val viewModel: ArtistDetailsViewModel by viewModels()
    private lateinit var args: ArtistDetailsFragmentArgs
    private lateinit var fanArts: List<String>

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
        args = ArtistDetailsFragmentArgs.fromBundle(requireArguments())
        fanArts = listOf(
            args.artist.firstFanArt,
            args.artist.secondFanArt,
            args.artist.thirdFanArt,
            args.artist.fourthFanArt,
        )

        binding = FragmentArtistDetailsBinding.inflate(layoutInflater)
    }

    private fun initViews() {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val fanArtAdapter = GalleryAdapter(fanArts)
        binding.rvFanArt.adapter = fanArtAdapter

        binding.txtName.text = args.artist.name
        binding.txtGenre.text = args.artist.genre
        Glide.with(this.requireContext())
            .load(args.artist.image)
            .placeholder(R.drawable.ic_loading_details_error)
            .into(binding.imgArtist)

        binding.infoCountry.setBottomLabelText(args.artist.country)
        binding.infoAlbumCount.setBottomLabelText(args.artist.albumCount.toString())
        binding.infoFans.setBottomLabelText(args.artist.fans.toString())
        binding.txtExpandDescription.text = args.artist.description
        val gender = args.artist.gender
        if (gender) {
            binding.infoGender.setBottomLabelText(getString(R.string.male))
        } else {
            binding.infoGender.setBottomLabelText(getString(R.string.female))
        }
    }

    private fun FragmentArtistDetailsBinding.initListeners() {
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