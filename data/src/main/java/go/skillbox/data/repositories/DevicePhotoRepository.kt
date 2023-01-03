package go.skillbox.data.repositories

import go.skillbox.domain.models.Photo

interface DevicePhotoRepository {

    suspend fun savePhoto(url: String, date: String)
    suspend fun getAllPhotos(): List<Photo>
    suspend fun getPhotoByUrl(url: String): Photo
    suspend fun clear()
    suspend fun removeByUrl(url: String)
}