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
    private val observeConnectedToSocketUseCase: ObserveConnectedToSocket,
    private val dispatcher: IDispatchers,
) : BaseViewModel(dispatcher) {

    private val connectedToSocket = MutableSharedFlow<Boolean>()
    val socketConnected = connectedToSocket.asSharedFlow()
    private val receivedMessages = mutableListOf<String>()

    fun connectToSocket(url: String) {
        connectToSocketUseCase(url)
    }
    fun disconnectFromSocket() {
        disconnectFromSocketUseCase()
    }
    fun sendMessage(location: UpdateLocation) {
        sendMessageUseCase(location)
    }



    fun observeConnectedToSocket(){
        launchOnBack {
            observeConnectedToSocketUseCase().collect{
                connectedToSocket.emit(it)
            }
        }
    }

}