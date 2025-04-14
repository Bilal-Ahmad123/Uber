package com.example.uber.presentation.riderpresentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.api.backend.rider.socket.location.mapper.UpdateDriverLocation
import com.example.uber.domain.remote.socket.location.model.UpdateLocation
import com.example.uber.domain.remote.socket.location.usecase.ConnectSocketUseCase
import com.example.uber.domain.remote.socket.location.usecase.DisconnectSocketUseCase
import com.example.uber.domain.remote.socket.location.usecase.ObserveConnectedToSocket
import com.example.uber.domain.remote.socket.location.usecase.ObserveDriverLocationUseCase
import com.example.uber.domain.remote.socket.location.usecase.StartObservingDriverLocationUseCase
import com.example.uber.domain.remote.socket.location.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val connectToSocketUseCase: ConnectSocketUseCase,
    private val disconnectFromSocketUseCase: DisconnectSocketUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val startObservingDriversLocationUseCase: StartObservingDriverLocationUseCase,
    private val observeDriversLocationsUseCase: ObserveDriverLocationUseCase,
    private val observeConnectedToSocketUseCase: ObserveConnectedToSocket,
    private val dispatcher: IDispatchers,
) : BaseViewModel(dispatcher) {
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> get() = _messages
    private val _driverLocation = MutableSharedFlow<UpdateDriverLocation>()
    val driverLocation: SharedFlow<UpdateDriverLocation>
        get() = _driverLocation.asSharedFlow()
    private val connectedToSocket = MutableSharedFlow<Boolean>()
    val socketConnected = connectedToSocket.asSharedFlow()
    private val receivedMessages = mutableListOf<String>()
    fun connectToSocket(url: String) {
        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("SocketViewModel", "Received message: $text")
                receivedMessages.add(text)
                _messages.postValue(receivedMessages)
            }
        }
        connectToSocketUseCase(url, listener)
    }
    fun disconnectFromSocket() {
        disconnectFromSocketUseCase()
    }
    fun sendMessage(location: UpdateLocation) {
        sendMessageUseCase(location)
    }

    fun startObservingDriversLocation(){
        startObservingDriversLocationUseCase()
    }

    fun observeDriversLocations(){
        launchOnBack {
            observeDriversLocationsUseCase().collect{
                _driverLocation.emit(it)
            }
        }
    }

    fun observeConnectedToSocket(){
        launchOnBack {
            observeConnectedToSocketUseCase().collect{
                connectedToSocket.emit(it)
            }
        }
    }

}