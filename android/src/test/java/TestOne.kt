import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.programmersbox.gsonutils.fromJson
import me.jrein.common.InfoModel
import org.junit.Test


class TestOne {

    @Test
    fun asdf() {
        val s = "{\"alternativeNames\":[\"\"],\"chapters\":[{\"extras\":{},\"name\":\"Good-for-Nothing Defies Common Sense: The Evil Emperor\\u0027s Wild Consort (Novel) Ch.066\",\"source\":\"NINE_ANIME\",\"uploaded\":\"5 minutes ago\",\"uploadedTime\":1620157778652,\"url\":\"https://www.nineanime.com/chapter/Good_for_Nothing_Defies_Common_Sense_The_Evil_Emperor_s_Wild_Consort_Novel_Ch_066/3633158/\"}],\"description\":\"Read Now Add to Library\",\"genres\":[\"Action\",\"Fantasy\",\"Romance\",\"Josei\",\"Historical\"],\"imageUrl\":\"https://img3.nineanime.com/files/img/logo/202003/19/202003131350118746.jpg\",\"source\":\"NINE_ANIME\",\"title\":\"Good-for-Nothing Defies Common Sense: The Evil Emperor\\u0027s Wild Consort (Novel)\",\"url\":\"https://www.nineanime.com/manga/Good_for_Nothing_Defies_Common_Sense_The_Evil_Emperor_s_Wild_Consort_Novel.html\"}"

        val f1 = s.fromJson2<InfoModel>()

        println(f1)

    }

    inline fun <reified T> String?.fromJson2(): T? = try {
        Gson().fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}