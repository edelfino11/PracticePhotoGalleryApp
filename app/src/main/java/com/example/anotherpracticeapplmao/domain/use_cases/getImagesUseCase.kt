package com.example.anotherpracticeapplmao.domain.use_cases

import com.example.anotherpracticeapplmao.common.Resource
import com.example.anotherpracticeapplmao.data.remote.dto.toImage
import com.example.anotherpracticeapplmao.domain.model.Image
import com.example.anotherpracticeapplmao.domain.repository.ImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class getImagesUseCase @Inject constructor(
    private val repository: ImagesRepository
) {
    operator fun invoke(): Flow<Resource<List<Image>>> = flow {
        try {
            emit(Resource.Loading())
            val coins = repository.getImages().map { it.toImage()}
            emit(Resource.Success(coins))
        } catch(e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection"))
        }
    }
}