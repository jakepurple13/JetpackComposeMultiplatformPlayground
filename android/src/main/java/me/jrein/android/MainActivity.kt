package me.jrein.android

import android.content.Intent
import me.jrein.common.App
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.programmersbox.gsonutils.toJson
import me.jrein.common.InfoLayout
import me.jrein.common.uiViewer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            uiViewer {
                runOnUiThread {
                    /*MangaInfoFragment { InfoLayout(it) }
                        .also { it.showsDialog = false }
                        .showNow(supportFragmentManager, "")*/
                    startActivity(Intent(this@MainActivity, InfoActivity::class.java).apply {
                        putExtra("info", it.toJson())
                    })
                }
            }
        }
    }
}

class MangaInfoFragment(private val block: @Composable () -> Unit) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        LinearLayout(requireContext()).apply {
            addView(ComposeView(requireContext()).apply {
                setContent {
                    block()
                }
            })
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}