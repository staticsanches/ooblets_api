package dev.staticsanches.ooblets.wiki.crawler

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.name

object FileHelper {

	fun getFile(name: String, vararg directories: String): File {
		val parent = directories.asSequence()
			.fold(root) { path, directory -> path.resolve(directory) }
			.createDirectories()
		return File(parent.toFile(), name)
	}

	private val root = if (Paths.get("").absolute().name == "wiki_crawler") {
		Paths.get("..").resolve("wiki_crawler_data")
	} else {
		Paths.get("wiki_crawler_data")
	}.absolute()

}
