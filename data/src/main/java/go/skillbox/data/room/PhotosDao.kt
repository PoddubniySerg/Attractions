package go.skillbox.data.room

import androidx.room.*
import go.skillbox.data.room.entities.PhotoEntity

@Dao
interface PhotosDao {
    @Query("SELECT * FROM photos")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE url = :url")
    suspend fun getPhotoByUrl(url: String): PhotoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(photo: PhotoEntity)

    @Query("DELETE FROM photos")
    suspend fun clear()

    @Query("DELETE FROM photos WHERE url = :url")
    suspend fun remove(url: String)
}