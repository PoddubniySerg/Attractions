package go.skillbox.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import go.skillbox.data.room.entities.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao
}