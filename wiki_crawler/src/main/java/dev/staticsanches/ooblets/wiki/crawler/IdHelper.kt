package dev.staticsanches.ooblets.wiki.crawler

import java.text.Normalizer

fun String.toID() = IdHelper.toID(this)

object IdHelper {

	fun toID(value: String) =
		value.trim().unaccent().onlyAlphaNumeric().lowercase().replace(" ", "_")

	private val unaccentRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

	private fun CharSequence.unaccent(): String {
		val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
		return unaccentRegex.replace(temp, "")
	}

	private val alphaNumericRegex = "[^A-Za-z0-9 ]".toRegex()

	private fun CharSequence.onlyAlphaNumeric(): String =
		alphaNumericRegex.replace(this, " ").onlyOneSpace().trim()

	private val multipleSpaceRegex = "  +".toRegex()

	private fun CharSequence.onlyOneSpace(): String =
		multipleSpaceRegex.replace(this, " ")

}
