package ru.arriah.vetrf

import java.util.regex.Pattern

/**
 * Created by shevchenko-dv-100705 on 16.06.17.
 */
fun main(args: Array<String>) {
    println("This is the stub for kotlin program")
}

val genePattern = Regex("""gene=([\w|\.|\-|\_]*)""")
val locationPattern = Regex("""location=(\w*)""")

fun isGeneDescription(name: String) = name.startsWith(">lcl")
fun convertToFilename(description: String): String {
    val filename = genePattern.find(description)?.destructured?.component1() ?: "FAIL"
    val location = locationPattern.find(description)?.destructured?.component1() ?: "FAIL"
    val filenameStrip = filename.replace(".", "").replace("_", "").replace("-", "")
    return "${filenameStrip}_$location"
}

