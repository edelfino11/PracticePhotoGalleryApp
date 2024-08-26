package com.example.anotherpracticeapplmao.presentation.imagesList.common

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.example.anotherpracticeapplmao.domain.model.Image

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGrid(
    images: List<Image>,
    state: LazyGridState,
    modifier: Modifier = Modifier,
    setAutoScrollSpeed: (Float) -> Unit = { },
    navigateToPhoto: (Int) -> Unit = { }
){

    var selectedIds by rememberSaveable {
        mutableStateOf(emptySet<Int>())
    }

    val inSelectionMode by remember {
        derivedStateOf { selectedIds.isNotEmpty() }
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Adaptive(128.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .padding(vertical = 50.dp)
            .photoGridDragHandler(
            lazyGridState = state,
            selectedIds = { selectedIds },
            setSelectedIds = { selectedIds = it },
            setAutoScrollSpeed = setAutoScrollSpeed,
            autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }
        )
    ) {
        items(images, key = {it.id}) { img ->

            val selected by remember {
                derivedStateOf { selectedIds.contains(img.id) }
            }

            PhotoItem(
                img,
                inSelectionMode,
                selected,
                Modifier
                    .semantics {
                        if (!inSelectionMode) {
                            onLongClick("Select") {
                                selectedIds += img.id
                                true
                            }
                        }
                    }
                    .then(
                        if (inSelectionMode) {
                            Modifier.toggleable(
                                value = selected,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onValueChange = {
                                    if (it) {
                                        selectedIds += img.id
                                    } else {
                                        selectedIds -= img.id
                                    }
                                }
                            )
                        } else {
                            Modifier.combinedClickable(
                                onClick = { navigateToPhoto(img.id) },
                                onLongClick = { selectedIds += img.id }
                            )
                        }
                    )
            )
        }
    }
}

fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int


fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: () -> Set<Int>,
    autoScrollThreshold: Float,
    setSelectedIds: (Set<Int>) -> Unit = { },
    setAutoScrollSpeed: (Float) -> Unit = { },
): Modifier = this.then(Modifier.pointerInput(autoScrollThreshold, setSelectedIds, setAutoScrollSpeed) {

    fun photoIdAtOffset(hitPoint: Offset): Int? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    var initialPhotoId: Int? = null
    var currentPhotoId: Int? = null

    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            photoIdAtOffset(offset)?.let { key ->
                if (!selectedIds().contains(key)) {
                    initialPhotoId = key
                    currentPhotoId = key
                    setSelectedIds(selectedIds() + key)
                }
            }
        },
        onDragCancel = { setAutoScrollSpeed(0f); initialPhotoId = null },
        onDragEnd = { setAutoScrollSpeed(0f); initialPhotoId = null },
        onDrag = { change, _ ->
            if (initialPhotoId != null) {
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y

                setAutoScrollSpeed(
                    when {
                        distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                        distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                        else -> 0f
                    }
                )

                photoIdAtOffset(change.position)?.let { pointerPhotoId ->
                    if (currentPhotoId != pointerPhotoId) {
                        setSelectedIds(
                            selectedIds().addOrRemoveUpTo(pointerPhotoId, currentPhotoId, initialPhotoId)
                        )
                        currentPhotoId = pointerPhotoId
                    }
                }
            }
        }
    )
})

private fun Set<Int>.addOrRemoveUpTo(
    pointerKey: Int?,
    previousPointerKey: Int?,
    initialKey: Int?
): Set<Int> {
    return if (pointerKey == null || previousPointerKey == null || initialKey == null) {
        this
    } else {
        this
            .minus(initialKey..previousPointerKey)
            .minus(previousPointerKey..initialKey)
            .plus(initialKey..pointerKey)
            .plus(pointerKey..initialKey)
    }
}
