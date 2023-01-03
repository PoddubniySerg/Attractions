package go.skillbox.domain.usecases

import go.skillbox.domain.exceptions.ClearPhotoException
import go.skillbox.domain.repositories.PhotoRepository
import javax.inject.Inject

open class ClearPhotosUseCase @Inject constructor() {

    @Inject
    protected lateinit var repository: PhotoRepository

    suspend fun execute() {
        try {
            repository.clearPhotos()
        } catch (ex: Exception) {
            throw ClearPhotoException("Error clear photos from repository")
        }
    }
}