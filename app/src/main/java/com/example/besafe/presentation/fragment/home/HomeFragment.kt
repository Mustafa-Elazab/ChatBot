package com.example.besafe.presentation.fragment.home

import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.fragment.findNavController
import com.example.besafe.R
import com.example.besafe.base.BaseFragment
import com.example.besafe.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {


    override val defineBindingVariables: ((FragmentHomeBinding) -> Unit)?
        get() = { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            chatBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
            }
            imageBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_imageFragment)
            }
            settingBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }
        }
    }
}