package me.jrein.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.accompanist.glide.rememberGlidePainter
import com.programmersbox.gsonutils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jrein.common.ChapterModel
import java.net.URL

class ReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val alpha = remember { mutableStateOf(1f) }
            val pageList = remember { mutableStateOf(emptyList<String?>()) }
            MaterialTheme(colors = me.jrein.common.theme.value) {
                Box(modifier = Modifier.background(me.jrein.common.theme.value.background).padding(5.dp).fillMaxSize()) {
                    val listState = rememberLazyListState()
                    CircularProgressIndicator(Modifier.alpha(alpha.value).align(Alignment.Center))
                    LazyColumn(
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(5.dp)
                            .fillMaxWidth()
                            .background(me.jrein.common.theme.value.background)
                    ) {
                        items(pageList.value) {
                            Image(
                                painter = rememberGlidePainter(it),
                                modifier = Modifier.padding(vertical = 3.dp).border(1.dp, me.jrein.common.theme.value.onBackground),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }

            remember {
                GlobalScope.launch {
                    val pages = withContext(Dispatchers.Default) {
                        intent.getStringExtra("model")?.fromJson<ChapterModel>()
                            ?.getChapterInfo()
                            ?.blockingGet()
                            .also { println(it) }
                            ?.map { it.link }
                            .orEmpty()
                    }
                    pageList.value = pages
                    alpha.value = 0f
                }
            }

        }
    }
}