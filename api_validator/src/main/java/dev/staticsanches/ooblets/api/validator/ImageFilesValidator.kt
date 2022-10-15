package dev.staticsanches.ooblets.api.validator

object ImageFilesValidator {

    fun validate(directory: ApiPath) {
        directory.children
            .filter { it.isDirectory }
            .map { it["image.png"] }
            .forEach { require(it.isFile) }
    }

}
