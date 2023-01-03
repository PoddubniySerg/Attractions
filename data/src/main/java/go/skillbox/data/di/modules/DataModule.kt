package go.skillbox.data.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import go.skillbox.data.DataApp
import go.skillbox.data.repositories.DataRepository
import go.skillbox.data.repositories.DevicePhotoRepository
import go.skillbox.data.room.PhotosDao
import go.skillbox.data.room.PhotosDaoDataSource
import go.skillbox.domain.repositories.PhotoRepository

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun providePhotoRepository(
        devicePhotoRepository: DevicePhotoRepository
    ): PhotoRepository {
        val repository = DataRepository()
        repository.devicePhotoRepository = devicePhotoRepository
        return repository
    }

    @Provides
    fun provideDevicePhotoRepository(photosDao: PhotosDao): DevicePhotoRepository =
        PhotosDaoDataSource(photosDao)

    @Provides
    fun providePhotosDao(application: Application): PhotosDao =
        (application as DataApp).database.photosDao()
}