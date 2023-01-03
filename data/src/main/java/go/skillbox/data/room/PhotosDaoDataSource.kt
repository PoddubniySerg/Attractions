package go.skillbox.data.room

import go.skillbox.data.repositories.DevicePhotoRepository
import go.skillbox.data.room.entities.PhotoEntity
import go.skillbox.domain.models.Photo

open class PhotosDaoDataSource(private val photosDao: PhotosDao) : DevicePhotoRepository {

    override suspend fun savePhoto(url: String, date: String) {
        photosDao.save(PhotoEntity(url, date))
    }

    override suspend fun getAllPhotos(): List<Photo> {
        return photosDao.getAllPhotos()
    }

    override suspend fun getPhotoByUrl(url: String): PhotoEntity {
        return photosDao.getPhotoByUrl(url)
    }

    override suspend fun clear() {
        photosDao.clear()
    }

    override suspend fun removeByUrl(url: String) {
        photosDao.remove(url)
    }
}