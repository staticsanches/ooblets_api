package dev.staticsanches.ooblets.api.validator

fun main() {
    ApiPath.root.children
        .forEach(AllFilesValidator::validate)
    ApiPath.root.children
        .forEach(DataFilesValidator::validate)
    ImageFilesValidator.validate(ApiPath.root["items"])
    ImageFilesValidator.validate(ApiPath.root["moves"])
}
