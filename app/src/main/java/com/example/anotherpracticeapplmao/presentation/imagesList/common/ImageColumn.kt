package com.example.anotherpracticeapplmao.presentation.imagesList.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.anotherpracticeapplmao.domain.model.Image

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageColumn(
    images: List<Image>,
    state: LazyGridState,
    modifier: Modifier = Modifier,
    setAutoScrollSpeed: (Float) -> Unit = { },
    navigateToPhoto: (Int) -> Unit = { }
){
    var activateId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val haptics = LocalHapticFeedback.current

    var contextMenuPhotoId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var selectedIds by rememberSaveable {
        mutableStateOf(emptySet<Int>())
    }

    val inSelectionMode by remember {
        derivedStateOf { selectedIds.isNotEmpty() }
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp),
    ) {
        items(images, key = {it.id}) { img ->

            val selected by remember {
                derivedStateOf { img.id in selectedIds }
            }

            PhotoItem(
                img,
                selected,
                inSelectionMode,
                Modifier
                    .clickable { activateId = img.id }
                    .combinedClickable(
                        onClick = { activateId = img.id },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            contextMenuPhotoId = img.id
                        },
                        onLongClickLabel = "menuphoto",
                    )
                    .fillParentMaxWidth()
                    .padding(all = 5.dp)
                    .height(200.dp))
        }
    }

    if (contextMenuPhotoId != null) {
        PhotoActionsSheet(
            image = images.first { it.id == contextMenuPhotoId },
            onDismissSheet = { contextMenuPhotoId = null }
        )
    }

    if(activateId != null) {
        FullScreenImage(
            image = images.first {it.id == activateId},
            onDismiss = {activateId = null}
        )
    }
}