package dev.staticsanches.ooblets.api.validator

object DataFilesValidator {

    fun validate(directory: ApiPath) {
        directory.children
            .filter { it.isDirectory }
            .forEach(this::validateInner)
    }

    private fun validateInner(directory: ApiPath) {
        val json = FileReader.read<Map<String, Any>>(directory["data.json"])
        val yaml = FileReader.read<Map<String, Any>>(directory["data.yaml"])

        require(json == yaml) { "$json != $yaml" }
    }

}
