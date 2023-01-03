package go.skillbox.customcamera.viewmodels

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import go.skillbox.customcamera.models.PhotoUi
import go.skillbox.customcamera.states.State
import go.skillbox.customcamera.util.Converter
import go.skillbox.domain.models.Photo
import go.skillbox.domain.params.RemovePhotoByUrlParam
import go.skillbox.domain.params.SavePhotoParam
import go.skillbox.domain.usecases.ClearPhotosUseCase
import go.skillbox.domain.usecases.GetAllPhotosUseCase
import go.skillbox.domain.usecases.RemovePhotoByUrlUseCase
import go.skillbox.domain.usecases.SavePhotoUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ListPhotosViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var getAllPhotosUseCase: GetAllPhotosUseCase

    @Inject
    protected lateinit var savePhotoUseCase: SavePhotoUseCase

    @Inject
    protected lateinit var clearPhotosUseCase: ClearPhotosUseCase

    @Inject
    protected lateinit var removePhotoByUrlUseCase: RemovePhotoByUrlUseCase

    @Inject
    protected lateinit var converter: Converter

    private val _photosFlow = Channel<List<PhotoUi>>()
    val photosFlow = _photosFlow.receiveAsFlow()

    private val _onItemClickFlow = Channel<String>()
    val onItemClickFlow = _onItemClickFlow.receiveAsFlow()

    private val _stateFlow = MutableStateFlow(State.SUCCESS)
    val stateFlow = _stateFlow.asStateFlow()

    private val _errorFlow = Channel<Exception>()
    val errorFlow = _errorFlow.receiveAsFlow()

    suspend fun takePhotos(contentResolver: ContentResolver) {
        try {
            _stateFlow.value = State.LOADING
//            remove all data from repository
            clearPhotosUseCase.execute()
//            check media store if images exist add to repository
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )
            contentResolver.query(
                contentUri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val dateIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                while (cursor.moveToNext()) {
                    saveImage(
                        ContentUris.withAppendedId(
                            contentUri,
                            cursor.getLong(idIndex)
                        ).toString(),
                        cursor.getLong(dateIndex)
                    )
                }
            }
//            update list photos in fragment
            getPhotos()
            _stateFlow.value = State.SUCCESS
        } catch (ex: Exception) {
            _stateFlow.value = State.ERROR
            _errorFlow.send(ex)
        }
    }

    fun onItemClick(photo: Photo) {
        viewModelScope.launch { _onItemClickFlow.send(photo.url) }
    }

    fun removeItem(photo: Photo, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                _stateFlow.value = State.LOADING
//                remove image from media storage
                val uri = Uri.parse(photo.url)
                contentResolver.delete(uri, null, null)
//                remove from repository
                removePhotoByUrlUseCase.execute(RemovePhotoByUrlParam(photo.url))
//                update list photos in fragment
                getPhotos()
                _stateFlow.value = State.SUCCESS
            } catch (ex: Exception) {
                _stateFlow.value = State.ERROR
                _errorFlow.send(ex)
            }
        }
    }

    private suspend fun saveImage(url: String, date: Long) {
        savePhotoUseCase.execute(SavePhotoParam(url, date))
    }

    private suspend fun getPhotos() {
        val photos = getAllPhotosUseCase.execute()
        _photosFlow.send(photos.result.map { converter.convert(it) })
    }
}