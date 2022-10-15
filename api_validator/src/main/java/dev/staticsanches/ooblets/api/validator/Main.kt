package dev.staticsanches.ooblets.api.validator

fun main() {
    ApiPath.root.children
        .forEach(DirectoryWithAllFilesValidator::validate)
    ApiPath.root.children
        .forEach(DataFilesValidator::validate)
}
