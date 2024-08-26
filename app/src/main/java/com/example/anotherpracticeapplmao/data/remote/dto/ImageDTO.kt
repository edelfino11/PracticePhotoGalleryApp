package com.example.anotherpracticeapplmao.data.remote.dto

import com.example.anotherpracticeapplmao.domain.model.Image
import com.google.gson.annotations.SerializedName

data class ImageDTO(
    val author: String,
    @SerializedName("download_url")
    val download_url: String,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int
)

fun ImageDTO.toImage () : Image {
    return Image(
        author = author,
        height = height,
        id = id.toInt(),
        url = download_url,
        width = width
    )
}