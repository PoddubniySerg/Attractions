package go.skillbox.customcamera.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import go.skillbox.customcamera.states.State
import go.skillbox.domain.params.RemovePhotoByUrlParam
import go.skillbox.domain.usecases.RemovePhotoByUrlUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class FullscreenViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var removePhotoByUrlUseCase: RemovePhotoByUrlUseCase

    private val _stateFlow = MutableStateFlow(State.LOADING)
    val stateFlow = _stateFlow.asStateFlow()

    private val _errorFlow = Channel<Exception>()
    val errorFlow = _errorFlow.receiveAsFlow()

    fun removePhoto(url: String, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                _stateFlow.value = State.LOADING
//            remove image from media storage and from repository
                val uri = Uri.parse(url)
                removePhotoByUrlUseCase.execute(RemovePhotoByUrlParam(url))
                contentResolver.delete(uri, null, null)

                _stateFlow.value = State.SUCCESS
            } catch (ex: Exception) {
                _stateFlow.value = State.ERROR
                _errorFlow.send(ex)
            }
        }
    }
}