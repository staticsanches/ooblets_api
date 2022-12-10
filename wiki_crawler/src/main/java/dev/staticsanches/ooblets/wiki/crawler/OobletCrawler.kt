package dev.staticsanches.ooblets.wiki.crawler

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
	val allOoblets = WikiJsoupLoader.findAllOobletsNames()

	while (true) {
		listOoblets(allOoblets)
		print("Select an ooblet or q to quit or a to all [q]: ")
		val userInput = readlnOrNull()?.trim()?.lowercase()?.ifEmpty { null } ?: "q"
		if (userInput == "q") {
			break
		}
		val duration = measureTime {
			if (userInput == "a") {
				allOoblets.forEach { exportOobletData(it) }
			} else {
				val ooblet = allOoblets[userInput.toInt() - 1]
				exportOobletData(ooblet)
			}
		}
		println("Export took $duration")
	}
}

private fun listOoblets(allOoblets: Collection<String>, maxPerLine: Int = 6) {
	val indexMaxLength = allOoblets.size.toString().length
	val oobletMaxLength = allOoblets.asSequence().map { it.length }.max() + 1
	for ((index, ooblet) in allOoblets.withIndex()) {
		print(String.format("%${indexMaxLength}s) %-${oobletMaxLength}s", index + 1, ooblet))
		if ((index + 1) % maxPerLine == 0) {
			println()
		}
	}
	if (allOoblets.size % maxPerLine != 0) {
		println()
	}
}

private fun exportOobletData(name: String) {
	val document = WikiJsoupLoader.loadOobletPage(name)

	val firstMove = extractOobletMove(MoveLearningOrder.FIRST, document)
	val secondMove = extractOobletMove(MoveLearningOrder.SECOND, document)
	val thirdMove = extractOobletMove(MoveLearningOrder.THIRD, document)

	exportOobletMoveData(firstMove)
	exportOobletMoveData(secondMove)
	exportOobletMoveData(thirdMove)

	val id = name.toID()
	FileHelper.getFile("data.yaml", "ooblets", id).writeText(
		"""
			id: $id
			
			name:
			  default: $name
			
			quote:
			  default: >-
			    ${extractOobletQuote(document)}
			
			location: ${extractOobletLocation(document)}
			
			items:
			  - item: ${extractOobletItem(document)}
			    quantity: ${extractOobletItemQuantity(document)}
			
			signatureMoves:
			  - move: ${firstMove.name.toID()}
			    level: ${firstMove.level}
			  - move: ${secondMove.name.toID()}
			    level: ${secondMove.level}
			  - move: ${thirdMove.name.toID()}
			    level: ${thirdMove.level}
			
		""".trimIndent()
	)

	exportOobletImage(document, OobletVariant.COMMON, id)
	exportOobletImage(document, OobletVariant.UNUSUAL, id)
	exportOobletImage(document, OobletVariant.GLEAMY, id)
}

private fun exportOobletImage(document: Document, variant: OobletVariant, id: String) {
	val imageURL = extractImageUrl(
		document.selectFirst("table tr:nth-child(2) div:nth-child(${2 + variant.ordinal}) img")
	)
	if (imageURL != null) {
		saveImage(FileHelper.getFile("${variant.name.lowercase()}.png", "ooblets", id), imageURL)
	} else {
		println("Missing $variant image of $id")
	}
}

private fun extractOobletQuote(document: Document): String =
	document.selectFirst("tr td:matches(“) + td")!!.text()

private fun extractOobletLocation(document: Document): String =
	document.selectFirst("tr th:matches(Location) + td > a")?.text()?.toID()
		?: document.selectFirst("tr th:matches(Location) + td")!!.text().toID()

private fun extractOobletItem(document: Document): String =
	document.selectFirst("tr th:matches(Item) + td > a")!!.text().toID()

private fun extractOobletItemQuantity(document: Document): Int =
	document.selectFirst("tr th:matches(Item) + td:has(a)")!!.ownText()
		.removePrefix("x").trim().ifEmpty { "1" }.toInt()

private fun String.toID() = this.trim().lowercase().replace(" ", "_")

private fun extractOobletMove(learningOrder: MoveLearningOrder, document: Document): Move {
	val level = document.selectFirst(
		"h2:matches((?i)Signature Moves) ~ table tr:nth-child(1) > th:nth-child(${learningOrder.ordinal + 1}):matches((?i)Level \\d+)"
	)!!.text().lowercase().removePrefix("level").trim().toInt()
	val cost = document.selectFirst(
		"h2:matches((?i)Signature Moves) ~ table tr:nth-child(3) > td:nth-child(${1 + 2 * learningOrder.ordinal})"
	)!!.text().toInt()
	val name = document.selectFirst(
		"h2:matches((?i)Signature Moves) ~ table tr:nth-child(3) > td:nth-child(${2 * (learningOrder.ordinal + 1)})"
	)!!.text()
	val description = document.selectFirst(
		"h2:matches((?i)Signature Moves) ~ table tr:nth-child(4) > td:nth-child(${learningOrder.ordinal + 1})"
	)!!.text()
	val imageURL = extractImageUrl(
		document.selectFirst(
			"h2:matches((?i)Signature Moves) ~ table tr:nth-child(2) > td:nth-child(${learningOrder.ordinal + 1}) > a > img"
		)
	)

	return Move(level, cost, name, description, imageURL)
}

private fun extractImageUrl(imgElement: Element?, extension: String = ".png"): String? {
	if (imgElement == null) {
		return null
	}
	return if (imgElement.hasAttr("data-src")) {
		imgElement.attr("data-src")
	} else {
		imgElement.attr("src")
	}.substringBeforeLast(extension) + extension
}

private fun exportOobletMoveData(move: Move) {
	val id = move.name.toID()
	FileHelper.getFile("data.yaml", "moves", id).writeText(
		"""
			id: $id
			
			name:
			  default: ${move.name}
			
			description:
			  default: >-
			    ${move.description}
			
			cost: ${move.cost}
			
		""".trimIndent()
	)
	if (move.imageURL != null) {
		saveImage(FileHelper.getFile("image.png", "moves", id), move.imageURL)
	} else {
		println("Missing image of move ${move.name}")
	}
}

private fun saveImage(file: File, imageURL: String, imageFormat: String = "png") =
	ImageIO.write(ImageIO.read(URL(imageURL)), imageFormat, file)

private enum class OobletVariant { COMMON, UNUSUAL, GLEAMY }

private enum class MoveLearningOrder { FIRST, SECOND, THIRD }

private data class Move(
	val level: Int, val cost: Int, val name: String, val description: String, val imageURL: String?
)
