package go.skillbox.domain.usecases

import go.skillbox.domain.exceptions.GetPhotoByUrlException
import go.skillbox.domain.params.GetPhotoByUrlParam
import go.skillbox.domain.repositories.PhotoRepository
import go.skillbox.domain.results.SavedPhoto
import javax.inject.Inject

open class GetPhotoByUrlUseCase @Inject constructor() {

    @Inject
    protected lateinit var repository: PhotoRepository

    suspend fun execute(param: GetPhotoByUrlParam): SavedPhoto {
        try {
            return SavedPhoto(repository.getPhotoByUrl(param.url))
        } catch (ex: Exception) {
            throw GetPhotoByUrlException("Error get photo by url from repository")
        }
    }
}