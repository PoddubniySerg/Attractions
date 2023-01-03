package go.skillbox.customcamera.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import go.skillbox.customcamera.states.State
import go.skillbox.domain.params.SavePhotoParam
import go.skillbox.domain.usecases.GetPhotoByUrlUseCase
import go.skillbox.domain.usecases.SavePhotoUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CameraViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var savePhotoUseCase: SavePhotoUseCase

    @Inject
    protected lateinit var getPhotoByUrlUseCase: GetPhotoByUrlUseCase

    private val _photoFlow = Channel<String>()
    val photoFlow = _photoFlow.receiveAsFlow()

    private val _stateFlow = MutableStateFlow(State.SUCCESS)
    val stateFlow = _stateFlow.asStateFlow()

    private val _errorFlow = Channel<Exception>()
    val errorFlow = _errorFlow.receiveAsFlow()

    fun savePhoto(uri: Uri?, date: Long) {
        uri ?: return
        viewModelScope.launch {
            try {
                _stateFlow.value = State.LOADING
                savePhotoUseCase.execute(SavePhotoParam(uri.toString(), date))
                _photoFlow.send(uri.toString())
                _stateFlow.value = State.SUCCESS
            } catch (ex: Exception) {
                _stateFlow.value = State.ERROR
                _errorFlow.send(ex)
            }
        }
    }
}