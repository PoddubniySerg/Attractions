package go.skillbox.domain.usecases

import go.skillbox.domain.exceptions.GetAllPhotosException
import go.skillbox.domain.repositories.PhotoRepository
import go.skillbox.domain.results.SavedPhotos
import javax.inject.Inject

open class GetAllPhotosUseCase @Inject constructor() {

    @Inject
    protected lateinit var repository: PhotoRepository

    suspend fun execute(): SavedPhotos {
        try {
            return SavedPhotos(repository.getAllPhotos())
        } catch (ex: Exception) {
            throw GetAllPhotosException("Error get all photos from repository")
        }
    }
}