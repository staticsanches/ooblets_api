package dev.staticsanches.ooblets.api.validator

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

class ApiPath private constructor(val parent: ApiPath?, val path: Path) {

    val name: String
        get() = path.name

    val isDirectory: Boolean
        get() = Files.isDirectory(path)

    val isFile: Boolean
        get() = Files.isRegularFile(path)

    val children: Sequence<ApiPath>
        get() {
            if (isFile) {
                return emptySequence()
            }
            return path.listDirectoryEntries().asSequence()
                .map { this[it.name] }
                .sortedWith { p1, p2 ->
                    if (p1.isDirectory) {
                        if (p2.isFile) {
                            return@sortedWith -1
                        }
                        return@sortedWith p1.name.compareTo(p2.name)
                    }
                    if (p2.isDirectory) {
                        return@sortedWith 1
                    }
                    return@sortedWith p1.name.compareTo(p2.name)
                }
        }

    operator fun get(other: String): ApiPath = ApiPath(this, path.resolve(other))

    override fun toString(): String {
        if (parent == null) {
            return name
        }
        return "$parent/$name"
    }

    companion object {

        val root = ApiPath(
            null, if (Paths.get("").absolute().name == "api_validator") {
                Paths.get("..").resolve("api")
            } else {
                Paths.get("api")
            }.absolute()
        )["v1"]

    }

}
