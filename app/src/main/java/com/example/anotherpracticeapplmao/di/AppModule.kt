package com.example.anotherpracticeapplmao.di

import com.example.anotherpracticeapplmao.common.constants.BASE_URL
import com.example.anotherpracticeapplmao.data.remote.PicSumPhotosApi
import com.example.anotherpracticeapplmao.data.repository.ImagesRepositoryImpl
import com.example.anotherpracticeapplmao.domain.repository.ImagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePicSumApi(): PicSumPhotosApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PicSumPhotosApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImagesRepository(api: PicSumPhotosApi): ImagesRepository {
        return ImagesRepositoryImpl(api)
    }

}