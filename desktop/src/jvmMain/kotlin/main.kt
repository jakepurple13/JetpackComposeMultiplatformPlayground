import androidx.compose.desktop.Window
import me.jrein.common.InfoLayout
import me.jrein.common.MangaRead
import me.jrein.common.uiViewer

fun main() = Window { uiViewer { Window { InfoLayout(it) { MangaRead(it) } } } }
