package ru.arriah.vetrf

import java.util.regex.Pattern

/**
 * Created by shevchenko-dv-100705 on 16.06.17.
 */
fun main(args: Array<String>) {
    println("This is the stub for kotlin program")
}

private val genePattern = Pattern.compile("gene=([\\w|\\.|\\-|\\_]*)")
private val locationPattern = Pattern.compile("location=(\\w*)")


private fun isGeneDescription(name: String) = name.startsWith(">lcl")
private fun convertToFilename(description: String): String {
    val geneMatcher = genePattern.matcher(description)
    if (!geneMatcher.find() || geneMatcher.groupCount() < 1) return ""
    val filename = geneMatcher.group(1)

    val locationMatcher = locationPattern.matcher(description)

    if (!locationMatcher.find() || locationMatcher.groupCount() < 1) {
        return filename
    }

    val filenameStrip = filename.replace(".", "").replace("_", "").replace("-", "")
    return "${filenameStrip}_${locationMatcher.group(1)}"
}