package com.example.uber.presentation.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.local.entities.Location
import com.example.uber.domain.use_case.socket.ConnectSocketUseCase
import com.example.uber.domain.use_case.socket.DisconnectSocketUseCase
import com.example.uber.domain.use_case.socket.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val connectToSocketUseCase: ConnectSocketUseCase,
    private val disconnectFromSocketUseCase: DisconnectSocketUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val dispatcher: IDispatchers,
) : BaseViewModel(dispatcher) {
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> get() = _messages

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
    fun sendMessage(location:com.example.uber.data.local.models.Location) {
        sendMessageUseCase(location)
    }
}