package com.example.besafe.presentation.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.data.remote.dto.CompletionResponse
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.databinding.ActivityMainBinding
import com.example.besafe.utils.printToLog
import com.google.android.material.internal.ViewUtils.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    val messagesAdapter: ChatAdapter = ChatAdapter()

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // initBottomNavigationView()
        setUpRecyclerView()
        observeSendMessage()

        binding.apply {
            btnSend.setOnClickListener {
                if (etTypeAMessage.text.toString().isEmpty()) {
                    etTypeAMessage.error = "required field"
                } else {
                    viewModel.setState(CompletionRequest(prompt = etTypeAMessage.text.toString()))
                    messagesAdapter?.addMessage(
                        CompletionResponse(
                            my_message = etTypeAMessage.text.toString()
                        )
                    )
                    reachAdapterBottom()
                    etTypeAMessage.setText("")
                }
            }
        }

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            if (heightDiff > dpToPx(
                    this@MainActivity,
                    200F
                )
            ) { // adjust this threshold to your needs
                // Keyboard is open
                reachAdapterBottom()
                // Do something here, such as adjusting the layout to make room for the keyboard
            } else {
                // Keyboard is closed
                // Do something here, such as resetting the layout to its original state
            }
        }




    }
    private fun dpToPx(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).roundToInt()
    }
    private fun setUpRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = false
        binding.rvMessageRecyclerview.apply {
            adapter = messagesAdapter
            layoutManager = mLayoutManager
        }

    }
    private fun reachAdapterBottom() {
        try {
            binding.rvMessageRecyclerview.scrollToPosition( 0)
        } catch (e: Exception) { e.printStackTrace() }
    }


    private fun observeSendMessage() {

        lifecycleScope.launchWhenCreated {
            viewModel.chatState.collectLatest {
                when (it) {
                    is NetworkResponse.Success -> {
                        binding.tvTyping.visibility = View.GONE
                        printToLog(it.body.toString())
                        messagesAdapter?.addMessage(it.body)
                        reachAdapterBottom()
                    }
                    is NetworkResponse.ApiError -> {
                        binding.tvTyping.visibility = View.GONE
                    }
                    NetworkResponse.Loading -> {
                        binding.tvTyping.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            for (i in 1..50) {
                                binding.tvTyping.text = "typing ."
                                delay(500L)
                                binding.tvTyping.text = "typing .."
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


//
//    private fun initBottomNavigationView() {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//        val navController = navHostFragment.findNavController()
//        navController.addOnDestinationChangedListener(this)
//        binding.bottomNav.setupWithNavController(navController)
//    }
//
//    override fun onDestinationChanged(
//        controller: NavController,
//        destination: NavDestination,
//        arguments: Bundle?
//    ) {
//
//        when (destination.id) {
//            R.id.homeFragment, R.id.profileFragment -> showBottomNav()
//            else -> hideBottomNav()
//        }
//    }
//
//    private fun showBottomNav() {
//        val navigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
//        if (!navigation.isShown)
//            navigation.visible()
//    }
//
//    private fun hideBottomNav() {
//        val navigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
//        navigation.invisible()
//    }
}