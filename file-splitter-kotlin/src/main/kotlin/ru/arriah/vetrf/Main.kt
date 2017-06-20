package ru.arriah.vetrf

import java.io.*

/**
 * Created by shevchenko-dv-100705 on 16.06.17.
 */
fun main(args: Array<String>) {
    if (args.size < 2) {
        error("Pattern name or output directory name is not provided")
        return
    }

    val (patternName, outputDir) = Pair(args[0], args[1])

    val lines = BufferedReader(FileReader(patternName)).lineSequence()
    val patterns = transform(lines)
    patterns.forEach{
        println("Writing down pattern ${it.filename}...")
        val content = it.content
        BufferedWriter(FileWriter("$outputDir/${it.filename}")).use{it.write(content)}
    }

}

data class OutputPattern(val filename: String, var content: String = "")

fun transform(lines: Sequence<String>) : List<OutputPattern> = lines.fold(ArrayList(),::handleLine).map { complement(it) }

fun handleLine(list:ArrayList<OutputPattern>, line: String) : ArrayList<OutputPattern> {
    if (isGeneDescription(line)) {
        list.add(OutputPattern(filename =  convertToFilename(line)))
    } else {
        val last = list.last()
        last.content += line
    }
    return list
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

fun complement(pattern: OutputPattern): OutputPattern {
    if (!pattern.filename.contains("complement")) return pattern
    return OutputPattern(pattern.filename, String(pattern.content.map{complement(it)}.toCharArray()).reversed())
}

fun complement(symbol: Char) = when(symbol) {
    'A' -> 'T'
    'T' -> 'A'
    'G' -> 'C'
    'C' -> 'G'
    else -> symbol
}