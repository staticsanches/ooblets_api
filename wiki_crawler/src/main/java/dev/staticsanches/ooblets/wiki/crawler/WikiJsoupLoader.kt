package dev.staticsanches.ooblets.wiki.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object WikiJsoupLoader {

    fun findAllOobletsNames(): List<String> =
        getDocument("Category:Ooblets_(Pets)")
            .select(".mw-category li > a").asSequence()
            .map { it.text().trim() }
            .filter { it != "Nullweirdos" } // not an ooblet
            .toList()

    fun loadOobletPage(name: String) =
        getDocument(name.replace(" ", "_"))

    fun findAllLocations(): List<String> =
        getDocument("Category:Regions")
            .select(".mw-category li > a")
            .map { it.text().trim() }
            .filter { it != "BungleHQ" } // not a valid location
            .toList()

    fun loadLocationPage(name: String) =
        getDocument(name.replace(" ", "_"))

    private val cache = mutableMapOf<String, Document>()

    private fun getDocument(path: String) =
        cache.getOrPut(path) { Jsoup.connect("https://ooblets.fandom.com/wiki/$path").get() }.clone()

}
