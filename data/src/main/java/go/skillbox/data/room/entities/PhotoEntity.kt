package go.skillbox.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import go.skillbox.domain.models.Photo

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    override val url: String,

    @ColumnInfo(name = "date")
    override val date: String
    ) : Photo
