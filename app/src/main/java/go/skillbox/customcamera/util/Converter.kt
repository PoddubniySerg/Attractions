package go.skillbox.customcamera.util

import go.skillbox.customcamera.models.PhotoUi
import go.skillbox.domain.models.Photo
import javax.inject.Inject

class Converter @Inject constructor(){

    fun convert(photo: Photo): PhotoUi {
        return PhotoUi(
            photo.url,
            photo.date
        )
    }
}