package com.example.besafe.presentation.fragment.image

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.besafe.R
import com.example.besafe.base.BaseFragment
import com.example.besafe.data.remote.dto.ImageGenerationRequest
import com.example.besafe.data.remote.dto.Message
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.databinding.FragmentImageBinding
import com.example.besafe.presentation.fragment.ChatViewModel
import com.example.besafe.presentation.fragment.adapter.ImageAdapter
import com.example.besafe.presentation.fragment.adapter.OnItemImageClickListener
import com.example.besafe.utils.checkPermissionAndDownload
import com.example.besafe.utils.printToLog
import com.example.besafe.utils.shareContent
import com.example.besafe.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


@AndroidEntryPoint
class ImageFragment : BaseFragment<FragmentImageBinding>(R.layout.fragment_image),
    OnItemImageClickListener {

    private val viewModel: ChatViewModel by activityViewModels()
    private var imageAdapter = ImageAdapter(this)


    override val defineBindingVariables: ((FragmentImageBinding) -> Unit)?
        get() = { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpRecyclerView()
        observeChatSendMessage()


        binding.apply {
            btnSend.setOnClickListener {
                if (etTypeAMessage.text.toString().isEmpty()) {
                    etTypeAMessage.error = "required field"
                } else {
                    viewModel.generateImages(
                        imageGenerationRequest = ImageGenerationRequest(
                            prompt = etTypeAMessage.text.toString(),
                        )
                    )

                    imageAdapter.addMessage(
                        message = Message(
                            content = etTypeAMessage.text.toString(),
                            role = "user"
                        )
                    )

                    reachAdapterBottom()
                    etTypeAMessage.setText("")
                }
            }

            btnListen.setOnClickListener {
                val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                sttIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

                try {
                    startActivityForResult(sttIntent, REQUEST_CODE_STT)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    toast("Your device does not support STT.")
                }
            }

            toolbarSupport.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

        }

    }

    private fun setUpRecyclerView() {
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = false
        binding.rvMessageRecyclerview.apply {
            adapter = imageAdapter
            layoutManager = mLayoutManager
        }

    }

    private fun observeChatSendMessage() {

        lifecycleScope.launchWhenCreated {
            viewModel.imageCompletionState.collectLatest {
                when (it) {
                    is NetworkResponse.Success -> {
                        binding.tvTyping.visibility = View.GONE
                        printToLog(it.body.toString())
                        imageAdapter.addMessage(message = Message(content = it.body.data!![0]!!.url))
                        reachAdapterBottom()
                    }
                    is NetworkResponse.ApiError -> {
                        binding.tvTyping.visibility = View.GONE
                    }
                    NetworkResponse.Loading -> {
                        binding.tvTyping.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            for (i in 1..50) {
                                binding.tvTyping.text = "generate."
                                delay(500L)
                                binding.tvTyping.text = "generate.."
                                delay(500L)
                                binding.tvTyping.text = "generate..."
                                delay(500L)
                                binding.tvTyping.text = "generate...."
                                delay(500L)
                                binding.tvTyping.text = "generate....."
                            }
                        }
                    }
                    is NetworkResponse.NetworkError -> {
                        binding.tvTyping.visibility = View.GONE
                    }
                    is NetworkResponse.UnknownError -> {
                        binding.tvTyping.visibility = View.GONE

                    }
                    else -> {}
                }
            }

        }
    }


    private fun reachAdapterBottom() {
        try {
            binding.rvMessageRecyclerview.scrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun contentShare(message: Message) {

        shareContent(message.content!!)
    }

    override fun contentDownload(message: Message) {

//        downloadImage(message.content!!)
//        checkPermissionAndDownload(message.content!!)
    }


    companion object {
        private const val REQUEST_CODE_STT = 1
    }


    fun downloadImage(imageUrl: String) {
        val filename = "image.png"
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val directory =
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val file = File(directory, filename)
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Notify the user that the image has been downloaded

                Toast.makeText(
                    requireContext(),
                    "Image downloaded successfully",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                e.printStackTrace()
                // Notify the user that an error occurred while downloading the image

                Toast.makeText(
                    requireContext(),
                    "Failed to download image",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


}

