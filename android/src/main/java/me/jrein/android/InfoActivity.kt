package me.jrein.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.programmersbox.gsonutils.fromJson
import com.programmersbox.gsonutils.toJson
import me.jrein.common.InfoLayout
import me.jrein.common.InfoModel

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            intent.getStringExtra("info")?.fromJson<InfoModel>()?.let {
                InfoLayout(it) {
                    startActivity(Intent(this@InfoActivity, ReadActivity::class.java).apply {
                        putExtra("model", it.toJson())
                    })
                }
            } ?: finish()
        }
    }
}