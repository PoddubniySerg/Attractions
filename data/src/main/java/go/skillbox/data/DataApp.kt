package go.skillbox.data

import android.app.Application
import androidx.room.Room
import go.skillbox.data.room.AppDatabase

open class DataApp: Application() {

    open lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db"
        ).build()
    }
}