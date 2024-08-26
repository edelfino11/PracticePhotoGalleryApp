package com.example.anotherpracticeapplmao.presentation.imagesList

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotherpracticeapplmao.presentation.imagesList.common.FullScreenImage
import com.example.anotherpracticeapplmao.presentation.imagesList.common.ImageColumn
import com.example.anotherpracticeapplmao.presentation.imagesList.common.ImageGrid
import com.example.anotherpracticeapplmao.presentation.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ImageListScreen (
    viewModel: ImagesListViewModel = hiltViewModel()
){
    val state = viewModel.state.value
    var activeId by rememberSaveable { mutableStateOf<Int?>(null) }
    val gridState = rememberLazyGridState()
    var autoScrollSpeed by remember { mutableStateOf(0f) }
    val scrim = remember(activeId) { FocusRequester() }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp, vertical = 50.dp)){

        Text(
            text = "Photo Gallery",
            fontStyle = Typography.titleLarge.fontStyle,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        BoxWithConstraints {
            constraints
            when {
                maxWidth <= 200.dp -> {
                    ImageColumn(
                        images = state.images,
                        state = gridState,
                        setAutoScrollSpeed = { autoScrollSpeed = it },
                        navigateToPhoto = { activeId = it },
                        modifier = Modifier
                            .focusProperties { canFocus = activeId == null }
                    )
                }
                else -> {
                    ImageGrid(
                        images = state.images,
                        state = gridState,
                        setAutoScrollSpeed = { autoScrollSpeed = it },
                        navigateToPhoto = { activeId = it },
                        modifier = Modifier
                            .focusProperties { canFocus = activeId == null }
                        )
                }
            }
        }

        if (activeId != null) {
            FullScreenImage(
                image = state.images.first { it.id == activeId },
                onDismiss = { activeId = null },
                modifier = Modifier
                    .focusRequester(scrim)
            )

            LaunchedEffect(activeId) {
                scrim.requestFocus()
            }
        }

        LaunchedEffect(autoScrollSpeed) {
            if (autoScrollSpeed != 0f) {
                while (isActive) {
                    gridState.scrollBy(autoScrollSpeed)
                    delay(10)
                }
            }
        }

        if(state.error.isNotBlank()){
            Text(
                text= state.error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if(state.isLoading){
            CircularProgressIndicator(modifier = Modifier
                .align(Alignment.Center))
        }
    }
}



