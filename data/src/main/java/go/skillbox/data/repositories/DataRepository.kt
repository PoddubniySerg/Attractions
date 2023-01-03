package go.skillbox.data.repositories

import go.skillbox.domain.models.Photo
import go.skillbox.domain.params.PhotoDataRemoveParam
import go.skillbox.domain.params.PhotoDataSaveParam
import go.skillbox.domain.repositories.PhotoRepository
import javax.inject.Inject

class DataRepository : PhotoRepository {

    @Inject
    lateinit var devicePhotoRepository: DevicePhotoRepository

    override suspend fun savePhoto(param: PhotoDataSaveParam) {
        devicePhotoRepository.savePhoto(param.url, param.date)
    }

    override suspend fun getAllPhotos(): List<Photo> {
        return devicePhotoRepository.getAllPhotos()
    }

    override suspend fun getPhotoByUrl(url: String): Photo {
        return devicePhotoRepository.getPhotoByUrl(url)
    }

    override suspend fun clearPhotos() {
        devicePhotoRepository.clear()
    }

    override suspend fun removePhotoByUrl(param: PhotoDataRemoveParam) {
        devicePhotoRepository.removeByUrl(param.url)
    }
}