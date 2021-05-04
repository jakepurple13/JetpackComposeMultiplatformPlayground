package me.jrein.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect fun getPlatformName(): String

@Composable
expect fun <T> BoxScope.VerticalBar(listState: LazyListState, currentList: List<T>)

@Composable
expect fun ItemCover(url: String, modifier: Modifier)

expect fun MangaRead(chapterModel: ChapterModel)