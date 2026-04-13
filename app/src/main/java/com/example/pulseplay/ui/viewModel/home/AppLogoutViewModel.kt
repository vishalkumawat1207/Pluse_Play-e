package com.example.pulseplay.ui.viewModel.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulseplay.data.repository.PulsePlayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLogoutViewModel @Inject constructor(
    private var repository: PulsePlayRepository,
    private var context: Application,
) : ViewModel() {

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin: SharedFlow<Unit> = _navigateToLogin

    fun goToLogInPage() {
        viewModelScope.launch {
            _navigateToLogin.emit(Unit)
        }
    }
}