package dev.staticsanches.ooblets.api.validator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import kotlin.io.path.extension

object FileReader {

    val jsonMapper = ObjectMapper()
    val yamlMapper = ObjectMapper(YAMLFactory())

    inline fun <reified T> read(path: ApiPath): T {
        require(path.isFile) { "$path is not a file" }
        return when (path.path.extension) {
            "json" -> jsonMapper
            "yaml" -> yamlMapper
            else -> throw IllegalArgumentException("$path is not json or yaml")
        }.readValue(path.path.toFile(), T::class.java)
    }

}
