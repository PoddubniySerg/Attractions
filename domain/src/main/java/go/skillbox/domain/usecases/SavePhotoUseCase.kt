package go.skillbox.domain.usecases

import go.skillbox.domain.exceptions.SavePhotoException
import go.skillbox.domain.params.PhotoDataSaveParam
import go.skillbox.domain.params.SavePhotoParam
import go.skillbox.domain.repositories.PhotoRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class SavePhotoUseCase @Inject constructor() {

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd\nhh:mm:ss"
    }

    @Inject
    protected lateinit var repository: PhotoRepository

    suspend fun execute(param: SavePhotoParam) {
        try {
            val dateString = SimpleDateFormat(
                DATE_FORMAT,
                Locale.getDefault()
            ).format(param.date)
            repository.savePhoto(PhotoDataSaveParam(param.url, dateString))
        } catch (ex: Exception) {
            throw SavePhotoException("Error save photo to repository")
        }
    }
}