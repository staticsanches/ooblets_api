package dev.staticsanches.ooblets.api.validator

import kotlin.io.path.nameWithoutExtension

object DirectoryWithAllFilesValidator {

    fun validate(directory: ApiPath) {
        val files = directory.children
            .filter { it.isFile }
            .toList()

        // Expect 2 files named all.yaml and all.json
        require(files.size == 2)
        require(files.map { it.path.nameWithoutExtension }.toSet() == setOf("all"))

        // The two files must have the same content
        val set0 = FileReader.read<List<String>>(files[0]).toSet()
        val set1 = FileReader.read<List<String>>(files[1]).toSet()
        require(set0 == set1)

        // Te directories described in the files must exist
        val directories = directory.children
            .filter { it.isDirectory }
            .map { it.name }
            .toSet()
        require(set0 == directories)
    }

}
