package go.skillbox.customcamera.models

import go.skillbox.domain.models.Photo

data class PhotoUi(override val url: String, override val date: String) : Photo
