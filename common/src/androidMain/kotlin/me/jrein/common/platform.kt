package me.jrein.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.accompanist.glide.rememberGlidePainter

actual fun getPlatformName(): String {
    return "Android"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun <T> BoxScope.VerticalBar(listState: LazyListState, currentList: List<T>) {

}

@Composable
actual fun ItemCover(url: String, modifier: Modifier) = Image(
    painter = rememberGlidePainter(url),
    contentDescription = "",
    modifier = Modifier
        .size(WIDTH_DEFAULT.dp, HEIGHT_DEFAULT.dp)
        .border(BorderStroke(1.dp, theme.value.background), shape = RoundedCornerShape(5.dp))
        .then(modifier)
)

actual fun MangaRead(chapterModel: ChapterModel) {

}