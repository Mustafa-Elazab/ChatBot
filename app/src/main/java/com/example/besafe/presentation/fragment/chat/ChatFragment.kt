package com.example.besafe.presentation.fragment.chat

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.besafe.R
import com.example.besafe.base.BaseFragment
import com.example.besafe.data.remote.dto.ChatCompletionRequestDTO
import com.example.besafe.data.remote.dto.Message
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.databinding.FragmentChatBinding
import com.example.besafe.presentation.fragment.ChatViewModel
import com.example.besafe.presentation.fragment.adapter.ChatAdapter
import com.example.besafe.presentation.fragment.adapter.OnItemChatClickListener
import com.example.besafe.utils.copyText
import com.example.besafe.utils.printToLog
import com.example.besafe.utils.shareContent
import com.example.besafe.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat),
    OnItemChatClickListener {

    private val viewModel: ChatViewModel by activityViewModels()
    private val messagesAdapter: ChatAdapter = ChatAdapter(this)

    private lateinit var tts: TextToSpeech


    override val defineBindingVariables: ((FragmentChatBinding) -> Unit)?
        get() = { binding ->
            binding.lifecycleOwner = viewLifecycleOwner

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        observeChatSendMessage()
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set the OnUtteranceProgressListener
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        // Called when TTS engine starts speaking
                        messagesAdapter.isClicked = true
                    }

                    override fun onDone(utteranceId: String) {
                        messagesAdapter.isClicked = false

                        // Called when TTS engine finishes speaking
                        // You can perform any actions that you want to execute when the TTS engine is finished here
                    }

                    override fun onError(utteranceId: String) {
                        messagesAdapter.isClicked = false
                        toast(utteranceId)
                        // Called if there was an error in TTS engine
                    }
                })
            }
        }

        binding.apply {
            btnSend.setOnClickListener {
                if (etTypeAMessage.text.toString().isEmpty()) {
                    etTypeAMessage.error = "required field"
                } else {
                    viewModel.sendMessage(
                        chatCompletionRequestDTO = ChatCompletionRequestDTO(
                            messages = listOf<Message>(
                                Message(
                                    content = etTypeAMessage.text.toString(),
                                    role = "user"
                                )
                            )
                        )
                    )
                    messagesAdapter?.addMessage(
                        Message(
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
            adapter = messagesAdapter
            layoutManager = mLayoutManager
        }

    }

    private fun observeChatSendMessage() {

        lifecycleScope.launchWhenCreated {
            viewModel.chatCompletionState.collectLatest {
                when (it) {
                    is NetworkResponse.Success -> {
                        binding.tvTyping.visibility = View.GONE
                        printToLog(it.body.toString())
                        messagesAdapter?.addMessage(it.body.choices!![0]!!.message!!)
                        messagesAdapter.notifyDataSetChanged()
                        reachAdapterBottom()
                    }
                    is NetworkResponse.ApiError -> {
                        binding.tvTyping.visibility = View.GONE
                        if (it.code == 401) {
                            toast("You didn't provide an API key. You need to provide " +
                                    "your API key in an Authorization header using Bearer" +
                                    " auth (i.e. Authorization: Bearer YOUR_KEY), or as " +
                                    "the password field (with blank username) if you're " +
                                    "accessing the API from your browser and are prompted for " +
                                    "a username and password. You can obtain an API key from" +
                                    " https://platform.openai.com/account/api-keys.")
                        }

                    }
                    NetworkResponse.Loading -> {
                        binding.tvTyping.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            for (i in 1..50) {
                                binding.tvTyping.text = "typing."
                                delay(500L)
                                binding.tvTyping.text = "typing.."
                                delay(500L)
                                binding.tvTyping.text = "typing..."
                                delay(500L)
                                binding.tvTyping.text = "typing...."
                                delay(500L)
                                binding.tvTyping.text = "typing....."
                            }
                        }
                    }
                    is NetworkResponse.NetworkError -> {
                        binding.tvTyping.visibility = View.GONE
                        toast(it.error.message.toString())
                    }
                    is NetworkResponse.UnknownError -> {
                        binding.tvTyping.visibility = View.GONE
                        toast(it.error!!.message.toString())

                    }
                    else -> {}
                }
            }

        }
    }

    override fun contentShare(message: Message) {
        shareContent(message.content!!)
    }

    override fun contentListen(message: Message) {

        if (messagesAdapter.isClicked) {
            // Stop the Text-to-Speech engine
            tts.stop()
            messagesAdapter.isClicked = false
        } else {
            // Start the Text-to-Speech engine
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "text")
            tts.speak(message.content, TextToSpeech.QUEUE_FLUSH, params, "text")
            messagesAdapter.isClicked = true
        }

    }


    override fun contentCopy(message: Message) {
        copyText(message.content!!, requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        binding.etTypeAMessage.setText(recognizedText)
                    }
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


    override fun onPause() {
        tts.stop()
        super.onPause()
    }

    override fun onDestroy() {
        tts.shutdown()
        tts.stop()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_STT = 1
    }
}