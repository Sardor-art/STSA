package hamidov.sardor.stsa.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

open class JsoupHelper {
    fun getElements(baseUrl: String, tag: String): Elements {
        val doc: Document = Jsoup.connect(baseUrl).get()
        return doc.select(tag)
    }

    fun getAttr(baseUrl: String, tag: String, attr: String): String {
        val doc: Document = Jsoup.connect(baseUrl).get()
        val data: Elements = doc.select(tag)
        return data.attr(attr)
    }

    fun getAttrs(baseUrl: String, tag: String, attr: String): ArrayList<String> {
        var datas: ArrayList<String> = ArrayList()
        val doc: Document = Jsoup.connect(baseUrl).get()
        val data: Elements = doc.select(tag)
        data.forEach { element ->
            datas.add(element.attr(attr))
        }
        return datas
    }
}