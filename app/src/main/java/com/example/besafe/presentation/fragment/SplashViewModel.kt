package com.example.besafe.presentation.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val DELAY_TIME_IN_MILLS = 1000L
    }

    private val _splashStateLiveData = MutableLiveData<SplashState>()
    val splashStateLiveData: LiveData<SplashState>
        get() = _splashStateLiveData

    init {
        GlobalScope.launch {
            delay(DELAY_TIME_IN_MILLS)
            _splashStateLiveData.postValue(SplashState.SplashScreen)
        }
    }
}

sealed class SplashState {
    object SplashScreen : SplashState()
}