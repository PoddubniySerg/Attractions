package go.skillbox.domain.repositories

import go.skillbox.domain.models.Photo
import go.skillbox.domain.params.PhotoDataRemoveParam
import go.skillbox.domain.params.PhotoDataSaveParam

interface PhotoRepository {

    suspend fun savePhoto(param: PhotoDataSaveParam)
    suspend fun getAllPhotos(): List<Photo>
    suspend fun getPhotoByUrl(url: String): Photo
    suspend fun clearPhotos()
    suspend fun removePhotoByUrl(param: PhotoDataRemoveParam)
}