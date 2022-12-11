package dev.staticsanches.ooblets.wiki.crawler

import org.jsoup.nodes.Document
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
	val allLocations = WikiJsoupLoader.findAllLocations()

	while (true) {
		listLocations(allLocations)
		print("Select a location or q to quit or a to all [q]: ")
		val userInput = readlnOrNull()?.trim()?.lowercase()?.ifEmpty { null } ?: "q"
		if (userInput == "q") {
			break
		}
		val duration = measureTime {
			if (userInput == "a") {
				allLocations.forEach { exportLocationData(it) }
			} else {
				val location = allLocations[userInput.toInt() - 1]
				exportLocationData(location)
			}
		}
		println("Export took $duration")
	}
}

private fun listLocations(allLocations: Collection<String>, maxPerLine: Int = 3) {
	val indexMaxLength = allLocations.size.toString().length
	val nameMaxLength = allLocations.asSequence().map { it.length }.max() + 1
	for ((index, ooblet) in allLocations.withIndex()) {
		print(String.format("%${indexMaxLength}s) %-${nameMaxLength}s", index + 1, ooblet))
		if ((index + 1) % maxPerLine == 0) {
			println()
		}
	}
	if (allLocations.size % maxPerLine != 0) {
		println()
	}
}

private fun exportLocationData(name: String) {
	val document = WikiJsoupLoader.loadLocationPage(name)

	val id = name.toID()
	FileHelper.getFile("data.yaml", "locations", id).writeText(
		"""
			id: $id
			
			name:
			  default: $name
			
			description:
			  default: >-
			    ${extractLocationDescription(document)}
			
		""".trimIndent()
	)

	exportLocationImage(document, id)
}

private fun extractLocationDescription(document: Document) =
	document.selectFirst("table.infobox ~ p")!!.text()

private fun exportLocationImage(document: Document, id: String) {
	val imageURL = ImageHelper.extractImageUrl(
		document.selectFirst("table.infobox img")
	)
	if (imageURL != null) {
		ImageHelper.saveImage(FileHelper.getFile("image.png", "locations", id), imageURL)
	} else {
		println("Missing image of $id")
	}
}
