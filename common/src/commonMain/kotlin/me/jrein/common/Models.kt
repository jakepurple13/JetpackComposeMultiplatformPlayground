package me.jrein.common

import io.reactivex.rxjava3.core.Single
import java.io.Serializable

data class ItemModel(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val source: Sources
): Serializable {
    val extras = mutableMapOf<String, Any>()
    fun toInfoModel() = source.getItemInfo(this)
}

data class InfoModel(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val chapters: List<ChapterModel>,
    val genres: List<String>,
    val alternativeNames: List<String>,
    val source: Sources
)

data class ChapterModel(
    val name: String,
    val url: String,
    val uploaded: String,
    val source: Sources
) {
    var uploadedTime: Long? = null
    fun getChapterInfo() = source.getChapterInfo(this)
    val extras = mutableMapOf<String, Any>()
}

class NormalLink(var normal: Normal? = null)
class Normal(var storage: Array<Storage>? = emptyArray())
data class Storage(
    var sub: String? = null,
    var source: String? = null,
    var link: String? = null,
    var quality: String? = null,
    var filename: String? = null
)

enum class Sources(service: ApiService) : ApiService by service {
    NINE_ANIME(NineAnime)
}

interface ApiService: Serializable {
    val baseUrl: String
    val websiteUrl: String get() = baseUrl
    val canScroll: Boolean get() = false
    fun getRecent(page: Int = 1): Single<List<ItemModel>>
    fun getList(page: Int = 1): Single<List<ItemModel>>
    fun getItemInfo(model: ItemModel): Single<InfoModel>
    fun searchList(searchText: CharSequence, page: Int = 1, list: List<ItemModel>): Single<List<ItemModel>> =
        Single.create { it.onSuccess(list.filter { it.title.contains(searchText, true) }) }

    fun getChapterInfo(chapterModel: ChapterModel): Single<List<Storage>>

    suspend fun getSourceByUrl(url: String): ItemModel? = ItemModel("", "", "", "", Sources.NINE_ANIME)

    val serviceName: String get() = this::class.java.name
}