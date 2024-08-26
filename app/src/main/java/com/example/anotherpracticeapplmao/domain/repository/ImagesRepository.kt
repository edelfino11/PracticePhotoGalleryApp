package com.example.anotherpracticeapplmao.domain.repository

import com.example.anotherpracticeapplmao.data.remote.dto.ImageDTO

interface ImagesRepository {

    suspend fun getImages(): List<ImageDTO>
}