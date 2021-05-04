package me.jrein.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.net.URL

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, ${getPlatformName()}"
        }) {
            Text(text)
        }
    }
}

val theme = mutableStateOf(darkColors())

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun uiViewer(openInfo: (InfoModel) -> Unit = {}) {
    val disposable = CompositeDisposable()
    val textValue = remember { mutableStateOf(TextFieldValue("")) }
    var page = 1
    val progressAlpha = remember { mutableStateOf(1f) }
    val currentList = remember { mutableStateOf<List<ItemModel>>(emptyList()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    remember {
        disposable.add(
            Sources.NINE_ANIME.getRecent(page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ currentList.value = it }, {})
        )
    }
    val showButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    MaterialTheme(colors = theme.value) {
        Scaffold(
            topBar = {
                Row(Modifier.background(theme.value.background).fillMaxWidth().padding(top = 5.dp)) {
                    TextField(
                        value = textValue.value,
                        textStyle = LocalTextStyle.current.copy(color = theme.value.onBackground),
                        modifier = Modifier.padding(16.dp).weight(9f),
                        onValueChange = { textValue.value = it },
                        trailingIcon = { Icon(Icons.Filled.Search, "Search") },
                        label = { Text("${currentList.value.size} Search") },
                        singleLine = true,
                        placeholder = { Text("Search") }
                    )
                    /*Image(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "",
                        modifier = Modifier
                            //.clickable { drawerState.open() }
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(5.dp),
                        colorFilter = ColorFilter.tint(theme.value.onBackground)
                    )*/
                }
            },
            bottomBar = {
                Button(
                    onClick = {
                        progressAlpha.value = 1f
                        GlobalScope.launch {
                            //fetch more items here
                            currentList.value =
                                currentList.value.toMutableList().apply { addAll(NineAnime.getRecent(++page).blockingGet()) }
                            println("New items - $page - ${currentList.value.size}")
                            progressAlpha.value = 0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load More")
                }
            },
            floatingActionButton = {
                AnimatedVisibility(visible = showButton.value) {
                    FloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(5.dp),
                            tint = theme.value.onBackground
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            Column(Modifier.background(theme.value.background).padding(it)) {
                CircularProgressIndicator(Modifier.alpha(progressAlpha.value))
                progressAlpha.value = 0f
                Box {
                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(180.dp),
                        state = listState
                    ) {
                        items(currentList.value.filter { it.title.contains(textValue.value.text, true) }) {
                            RowItem(it, openInfo)
                        }
                    }
                    VerticalBar(listState, currentList.value)
                }
            }
        }
    }
}

@Composable
fun <T> LazyGridFor(
    items: List<T>,
    rowSize: Int = 1,
    modifier: Modifier = Modifier,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = items.chunked(rowSize)
    LazyColumn {
        items(rows) { row ->
            Row(Modifier.fillParentMaxWidth()) {
                for ((index, item) in row.withIndex()) {
                    Box(
                        Modifier.fillMaxWidth(1f / (rowSize - index)).then(modifier),
                        contentAlignment = Alignment.TopCenter
                    ) { itemContent(item) }
                }
            }
        }
    }
}

@Composable
fun RowItem(item: ItemModel, openInfo: (InfoModel) -> Unit) = Card(
    shape = RoundedCornerShape(4.dp),
    border = cardBorder(),
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { GlobalScope.launch { item.toInfoModel().blockingGet().let { openInfo(it) /*{ InfoLayout(it) }*/ } } }
) {
    Box {
        ItemCover(item.imageUrl, Modifier)
        Text(
            item.title,
            style = MaterialTheme
                .typography
                .h6
                .copy(textAlign = TextAlign.Center, fontSize = 12.sp),
            modifier = Modifier
                //.padding(16.dp)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xaa000000))
        )

    }

}

const val WIDTH_DEFAULT = 360 / 2
const val HEIGHT_DEFAULT = 480 / 2

@Composable
fun InfoLayout(item: InfoModel, readData: (ChapterModel) -> Unit) {
    val favorite = remember { mutableStateOf(false) }
    MaterialTheme(colors = theme.value) {
        Scaffold(
            topBar = {
                Row(
                    Modifier.background(theme.value.background).fillMaxWidth().padding(top = 5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                favorite.value = !favorite.value
                            }
                    ) {
                        Image(
                            imageVector = if (favorite.value) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "",
                            modifier = Modifier.padding(5.dp),
                            colorFilter = ColorFilter.tint(theme.value.onBackground)
                        )
                        Text(
                            if (favorite.value) "Unfavorite" else "Favorite",
                            modifier = Modifier.padding(5.dp).align(Alignment.CenterVertically),
                            style = MaterialTheme.typography.button
                        )
                    }
                    Image(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(5.dp),
                        colorFilter = ColorFilter.tint(theme.value.onBackground)
                    )
                }
            }
        ) {
            Column(Modifier.background(theme.value.background)) {
                TitleArea(item)
                ItemRows(item, readData)
            }
        }
    }
}

@Composable
fun TitleArea(item: InfoModel) = Card(modifier = Modifier.padding(5.dp), border = cardBorder()) {
    Row(modifier = Modifier.padding(5.dp)) {
        ItemCover(item.imageUrl, Modifier)
        Column(modifier = Modifier.padding(5.dp).height(HEIGHT_DEFAULT.dp)) {
            Text(
                item.title,
                style = MaterialTheme
                    .typography
                    .h3
                    .copy(textAlign = TextAlign.Center)
            )
            Text(
                item.url,
                modifier = Modifier.clickable { URI.create(item.url) },
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme
                    .typography
                    .subtitle1
                    .copy(textAlign = TextAlign.Center, color = Color.Cyan)
            )
            LazyRow(modifier = Modifier.padding(5.dp)) {
                items(item.genres) { Text(it, modifier = Modifier.padding(5.dp), style = MaterialTheme.typography.subtitle2) }
            }
            Text(item.description, style = MaterialTheme.typography.body1)
            //ScrollableColumn { Text(item.description, style = MaterialTheme.typography.body1) }
        }
    }
}

@Composable
fun ItemRows(item: InfoModel, readData: (ChapterModel) -> Unit) = Box(modifier = Modifier.padding(5.dp)) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState, modifier = Modifier.fillMaxHeight()) {
        items(item.chapters) {
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                border = cardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            //MangaRead(it)
                            readData(it)
                        }
                ) {
                    Text(it.name)
                    Text(it.uploaded, modifier = Modifier.align(Alignment.End))
                }
            }
        }
    }
    VerticalBar(listState, item.chapters)
}

@Composable
fun cardBorder() = BorderStroke(1.dp, theme.value.onBackground)