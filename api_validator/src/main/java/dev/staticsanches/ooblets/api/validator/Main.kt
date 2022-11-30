package dev.staticsanches.ooblets.api.validator

import kotlin.io.path.nameWithoutExtension

fun main() {
    ApiPath.root.children
        .filter { it.isDirectory }
        .forEach(AllFilesValidator::validate)
    ApiPath.root.children
        .filter { it.isFile }
        .forEach { require(it.path.nameWithoutExtension == "timestamp_version") }
    ApiPath.root.children
        .forEach(DataFilesValidator::validate)
    ImageFilesValidator.validate(ApiPath.root["items"])
    ImageFilesValidator.validate(ApiPath.root["locations"])
    ImageFilesValidator.validate(ApiPath.root["moves"])
}
