package com.example.besafe.presentation.fragment.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.activityViewModels
import com.example.besafe.R
import com.example.besafe.base.BaseFragment
import com.example.besafe.databinding.FragmentSettingsBinding
import com.example.besafe.presentation.fragment.ChatViewModel
import com.example.besafe.presentation.fragment.ValidateError
import com.example.besafe.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {

    @Inject
    lateinit var dataStore: DataStore<Preferences>
    private val viewModel: ChatViewModel by activityViewModels()

    override val defineBindingVariables: ((FragmentSettingsBinding) -> Unit)?
        get() = { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            linkTv.setOnClickListener {
                val url = "https://platform.openai.com/account/api-keys"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }

            saveBtn.setOnClickListener {
                val api_key = etApiKey.text.toString()
                if (api_key.isEmpty()) {
                    binding.etApiKey.error = "Api must not empty."
                } else {
                    runBlocking {
                        dataStore.edit { preferences ->
                            preferences[ChatViewModel.API_KEY] = api_key
                        }
                    }
                    navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
                }

            }
        }


    }





}