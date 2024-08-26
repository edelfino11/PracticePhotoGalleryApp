package com.example.anotherpracticeapplmao.data.repository

import com.example.anotherpracticeapplmao.data.remote.PicSumPhotosApi
import com.example.anotherpracticeapplmao.data.remote.dto.ImageDTO
import com.example.anotherpracticeapplmao.domain.repository.ImagesRepository
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor(
    private val api: PicSumPhotosApi
) : ImagesRepository {

    override suspend fun getImages(): List<ImageDTO> {
        return api.getImages(2,15)
    }

}
