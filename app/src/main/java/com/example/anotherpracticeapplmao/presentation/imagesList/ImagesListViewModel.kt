package com.example.anotherpracticeapplmao.presentation.imagesList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotherpracticeapplmao.common.Resource
import com.example.anotherpracticeapplmao.domain.use_cases.getImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class ImagesListViewModel @Inject constructor (
    private val getImagesUseCase: getImagesUseCase)
    : ViewModel() {

    private val _state = mutableStateOf(ImagesListState())
    val state: State<ImagesListState> = _state

    init {
        getImages()
    }

    private fun getImages(){
        getImagesUseCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = ImagesListState(images = result.data ?: emptyList())
                }
                is Resource.Loading -> {
                    _state.value = ImagesListState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = ImagesListState(error = result.message ?:
                    "An unexpected error occurred")
                }
            }
        }.launchIn(viewModelScope)
    }


}