package com.example.anotherpracticeapplmao.data.remote

import com.example.anotherpracticeapplmao.data.remote.dto.ImageDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface PicSumPhotosApi {

    @GET("/v2/list")
    suspend fun getImages(@Query("page") page: Int,
                          @Query("limit") limit: Int): List<ImageDTO>

}