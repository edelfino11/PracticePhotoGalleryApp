package com.example.anotherpracticeapplmao.presentation.imagesList

import com.example.anotherpracticeapplmao.domain.model.Image

data class ImagesListState (
    val isLoading: Boolean = false,
    val images: List<Image> = emptyList(),
    val error: String = ""
)