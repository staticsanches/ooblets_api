package dev.staticsanches.ooblets.api.validator

import kotlin.io.path.writeText

object AllYamlGenerator {

	fun generateAll() {
		ApiPath.root.children
			.filter { it.isDirectory }
			.forEach { it.generate() }
	}

	private fun ApiPath.generate() {
		this["all.yaml"].path.writeText(
			children
				.filter { it.isDirectory }
				.map { it.name }
				.map { "- $it" }
				.joinToString("\n") + "\n"
		)
	}

}
