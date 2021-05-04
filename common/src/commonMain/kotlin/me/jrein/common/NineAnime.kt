package me.jrein.common

import io.reactivex.rxjava3.core.Single
import org.jsoup.Jsoup
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object NineAnime : ApiService {

    override val baseUrl = "https://www.nineanime.com"

    override val serviceName: String get() = "NINE_ANIME"

    val headers: List<Pair<String, String>> = listOf(
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) Gecko/20100101 Firefox/77",
        "Accept-Language" to "en-US,en;q=0.5"
    )

    override fun searchList(searchText: CharSequence, page: Int, list: List<ItemModel>): Single<List<ItemModel>> = try {
        if (searchText.isBlank()) throw Exception("No search necessary")
        Single.create { emitter ->
            Jsoup.connect("$baseUrl/search/?name=$searchText&page=$page.html").followRedirects(true).get()
                .select("div.post").map {
                    ItemModel(
                        title = it.select("p.title a").text(),
                        description = "",
                        url = it.select("p.title a").attr("abs:href"),
                        imageUrl = it.select("img").attr("abs:src"),
                        source = Sources.NINE_ANIME
                    )
                }
                .let { emitter.onSuccess(it) }
        }

    } catch (e: Exception) {
        super.searchList(searchText, page, list)
    }

    override fun getList(page: Int): Single<List<ItemModel>> = Single.create {
        it.onSuccess(
            Jsoup.connect("$baseUrl/category/index_$page.html").followRedirects(true).get()
                .select("div.post").map {
                    ItemModel(
                        title = it.select("p.title a").text(),
                        description = "",
                        url = it.select("p.title a").attr("abs:href"),
                        imageUrl = it.select("img").attr("abs:src"),
                        source = Sources.NINE_ANIME
                    )
                }
        )
    }

    override fun getItemInfo(model: ItemModel): Single<InfoModel> = Single.create { emitter ->
        val doc = try {
            Jsoup.connect("${model.url}?waring=1").followRedirects(true).get()
        } catch (e: Exception) {
            emitter.onError(e)
            return@create
        }
        val genreAndDescription = doc.select("div.manga-detailmiddle")
        emitter.onSuccess(
            InfoModel(
                title = model.title,
                description = genreAndDescription.select("p.mobile-none").text(),
                url = model.url,
                imageUrl = model.imageUrl,
                chapters = doc.select("ul.detail-chlist li").map {
                    ChapterModel(
                        name = it.select("a").select("span").firstOrNull()?.text() ?: it.text() ?: it.select("a").text(),
                        url = it.select("a").attr("abs:href"),
                        uploaded = it.select("span.time").text(),
                        source = Sources.NINE_ANIME
                    ).apply { uploadedTime = uploaded.toDate() }
                },
                genres = genreAndDescription.select("p:has(span:contains(Genre)) a").map { it.text() },
                alternativeNames = doc.select("div.detail-info").select("p:has(span:contains(Alternative))").text()
                    .removePrefix("Alternative(s):").split(";"),
                source = Sources.NINE_ANIME
            )
        )
    }

    override suspend fun getSourceByUrl(url: String): ItemModel? = try {
        val doc = Jsoup.connect(url).get()
        val genreAndDescription = doc.select("div.manga-detailmiddle")
        ItemModel(
            title = doc.select("div.manga-detail > h1").select("h1").text(),
            description = genreAndDescription.select("p.mobile-none").text(),
            url = url,
            imageUrl = doc.select("img.detail-cover").attr("abs:src"),
            source = Sources.NINE_ANIME
        )
    } catch (e: Exception) {
        null
    }

    override fun getChapterInfo(chapterModel: ChapterModel): Single<List<Storage>> = Single.create { emitter ->
        try {
            val doc = Jsoup.connect(chapterModel.url)
                .header("Referer", "$baseUrl/manga/").followRedirects(true).get()
            val script = doc.select("script:containsData(all_imgs_url)").firstOrNull()?.data() ?: throw Exception("all_imgsurl not found")
            emitter.onSuccess(
                Regex(""""(http.*)",""").findAll(script).map { it.groupValues[1] }
                    .map { Storage(link = it, source = chapterModel.url, quality = "Good", sub = "Yes") }
                    .toList()
            )
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    /*override fun getPageInfo(chapterModel: ChapterModel): PageModel {
        val doc = Jsoup.connect(chapterModel.url).header("Referer", "$baseUrl/manga.").followRedirects(true).get()
        val script = doc.select("script:containsData(all_imgs_url)").firstOrNull()?.data() ?: return PageModel(emptyList())
        return PageModel(Regex(""""(http.*)",""").findAll(script).map { it.groupValues[1] }.toList())
    }*/

    override val canScroll: Boolean get() = true

    override fun getRecent(page: Int): Single<List<ItemModel>> = Single.create {
        it.onSuccess(
            Jsoup.connect("$baseUrl/category/index_$page.html?sort=updated").followRedirects(true).get()
                .select("div.post").map {
                    ItemModel(
                        title = it.select("p.title a").text(),
                        description = "",
                        url = it.select("p.title a").attr("abs:href"),
                        imageUrl = it.select("img").attr("abs:src"),
                        source = Sources.NINE_ANIME
                    )
                }
        )
    }

    /*private fun getUrlWithoutDomain(orig: String): String {
        return try {
            val uri = URI(orig)
            var out = uri.path
            if (uri.query != null) {
                out += "?" + uri.query
            }
            if (uri.fragment != null) {
                out += "#" + uri.fragment
            }
            out
        } catch (e: URISyntaxException) {
            orig
        }
    }
*/
    private fun String.toDate(): Long {
        return try {
            if (this.contains("ago")) {
                val split = this.split(" ")
                val cal = Calendar.getInstance()
                when {
                    split[1].contains("minute") -> cal.apply { add(Calendar.MINUTE, split[0].toInt()) }.timeInMillis
                    split[1].contains("hour") -> cal.apply { add(Calendar.HOUR, split[0].toInt()) }.timeInMillis
                    else -> 0
                }
            } else {
                SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).parse(this)?.time ?: 0
            }
        } catch (_: ParseException) {
            0
        }
    }
}