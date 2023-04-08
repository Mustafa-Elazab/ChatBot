package com.example.besafe.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.viewModels
import com.example.besafe.R
import com.example.besafe.base.BaseFragment
import com.example.besafe.databinding.FragmentSplashBinding
import com.example.besafe.presentation.fragment.ChatViewModel.Companion.API_KEY
import com.example.besafe.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override val defineBindingVariables: ((FragmentSplashBinding) -> Unit)?
        get() = { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.splashStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is SplashState.SplashScreen -> {
                    if (getApiKey().isEmpty() || getApiKey() == "") {
                        navigate(SplashFragmentDirections.actionSplashFragmentToSettingsFragment())

                    } else {

                        navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                    }
                }
            }
        }

    }


    private fun getApiKey() = runBlocking {
        dataStore.data.first()[API_KEY] ?: ""

    }

}

//