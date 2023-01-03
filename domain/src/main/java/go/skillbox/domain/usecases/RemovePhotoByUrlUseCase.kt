package go.skillbox.domain.usecases

import go.skillbox.domain.exceptions.RemovePhotoByUrlException
import go.skillbox.domain.params.PhotoDataRemoveParam
import go.skillbox.domain.params.RemovePhotoByUrlParam
import go.skillbox.domain.repositories.PhotoRepository
import javax.inject.Inject

open class RemovePhotoByUrlUseCase @Inject constructor() {

    @Inject
    protected lateinit var repository: PhotoRepository

    suspend fun execute(param: RemovePhotoByUrlParam) {
        try {
            repository.removePhotoByUrl(PhotoDataRemoveParam(param.url))
        } catch (ex: Exception) {
            throw RemovePhotoByUrlException("Error remove photo by url from repository")
        }
    }
}