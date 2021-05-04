package me.jrein.common

import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

actual fun getPlatformName(): String {
    return "Desktop"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun <T> BoxScope.VerticalBar(listState: LazyListState, currentList: List<T>) = VerticalScrollbar(
    style = ScrollbarStyleAmbient.current.copy(
        hoverColor = theme.value.onBackground,
        unhoverColor = theme.value.onBackground.copy(alpha = 0.5f),
        hoverDurationMillis = 250
    ),
    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
    adapter = rememberScrollbarAdapter(
        scrollState = listState,
        itemCount = currentList.size,
        averageItemSize = 180.dp
    )
)

@Composable
actual fun ItemCover(url: String, modifier: Modifier) = Image(
    bitmap = org.jetbrains.skija.Image.makeFromEncoded(
        URL(url).openConnection().getInputStream().readAllBytes()
    ).asImageBitmap(),
    contentDescription = "",
    modifier = Modifier
        .size(WIDTH_DEFAULT.dp, HEIGHT_DEFAULT.dp)
        .border(BorderStroke(1.dp, theme.value.background), shape = RoundedCornerShape(5.dp))
        .then(modifier)
)

@OptIn(ExperimentalFoundationApi::class)
actual fun MangaRead(chapterModel: ChapterModel) = Window(title = chapterModel.name) {
    val alpha = remember { mutableStateOf(1f) }
    val pageList = remember { mutableStateOf(emptyList<ImageBitmap>()) }
    MaterialTheme(colors = theme.value) {
        Box(modifier = Modifier.background(theme.value.background).padding(5.dp).fillMaxSize()) {
            val listState = rememberLazyListState()
            CircularProgressIndicator(Modifier.alpha(alpha.value).align(Alignment.Center))
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(theme.value.background)
            ) {
                items(pageList.value) {
                    Image(
                        bitmap = it,
                        modifier = Modifier.padding(vertical = 3.dp).border(1.dp, theme.value.onBackground),
                        contentDescription = ""
                    )
                }
            }
            VerticalScrollbar(
                style = ScrollbarStyleAmbient.current.copy(
                    hoverColor = theme.value.onBackground,
                    unhoverColor = theme.value.onBackground.copy(alpha = 0.5f),
                    hoverDurationMillis = 250
                ),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState,
                    itemCount = pageList.value.size,
                    averageItemSize = if(pageList.value.isEmpty()) 100.dp else pageList.value.minOf { it.height }.dp
                )
            )
        }
    }

    remember {
        GlobalScope.launch {
            val pages = withContext(Dispatchers.Default) {
                chapterModel.getChapterInfo()
                    .blockingGet()
                    .also { println(it) }
                    .map { org.jetbrains.skija.Image.makeFromEncoded(URL(it.link).openConnection().getInputStream().readAllBytes()).asImageBitmap() }
            }
            pageList.value = pages
            alpha.value = 0f
        }
    }
}