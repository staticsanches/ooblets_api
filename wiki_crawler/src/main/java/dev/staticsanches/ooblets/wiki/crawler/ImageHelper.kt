package dev.staticsanches.ooblets.wiki.crawler

import org.jsoup.nodes.Element
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

object ImageHelper {

	fun extractImageUrl(imgElement: Element?, extension: String = ".png"): String? {
		if (imgElement == null) {
			return null
		}
		return if (imgElement.hasAttr("data-src")) {
			imgElement.attr("data-src")
		} else {
			imgElement.attr("src")
		}.substringBeforeLast(extension) + extension
	}

	fun saveImage(file: File, imageURL: String, imageFormat: String = "png") =
		ImageIO.write(ImageIO.read(URL(imageURL)), imageFormat, file)

}
