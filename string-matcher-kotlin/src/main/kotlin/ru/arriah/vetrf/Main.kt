package ru.arriah.vetrf

/**
 * Created by 123 on 21.06.2017.
 */
fun main(args: Array<String>) {
    println("The very first stub")
}

val MISS_SIGN = 'O'
val GAP_SIGN = '_'

fun minInt(vararg values:Int) = values.min() ?: 0

sealed class MatchResponse
/**
 * Начало и конец выводятся включительно, и начиная с единицы, а не с нуля, т.к.
 */
data class ExactMatch(val start:  Int, val end: Int): MatchResponse()
data class ApproximateMatch(val start: Int, val end: Int, val distance:Int,
                            val text: String, val pattern: String): MatchResponse()

enum class DIRECTION {UP, LEFT,DIAG, SKIP, UNEXPECTED}
data class Cell(val value: Short = 0, val direction: DIRECTION = DIRECTION.UNEXPECTED, val calculated: Boolean = false)

/**
 * Данный класс используется как промежуточные, и не используется для вывода напрямую пользователю
 * А посему и не должен быть одним из вариантов ответа
 */
data class QuickApproximateMatch(val end:Int, val distance:Int)

fun analyze(pattern: String, text: String) = analyzeStripped(clearFromNoise(pattern), clearFromNoise(text))
fun analyzeStripped(pattern: String, text: String) : MatchResponse {
    println("First pass - just the distance")
    checkNonEmpty(pattern, "pattern")
    checkNonEmpty(text, "text")
    checkPatternIsNotLongerThanText(pattern, text)

    val pos = text.indexOf(pattern)
    if (pos >= 0) return ExactMatch(pos+1, pos+pattern.length)

    val quickMatch = analyzeQuick(pattern, text)
    return analyzeDetailed(pattern, text, quickMatch)
}

fun analyzeDetailed(pattern: String, text: String, quickMatch: QuickApproximateMatch): ApproximateMatch {
    println("Second pass")
    val (n,m) = Pair(pattern.length, text.length)
    println("After clearing the noise from data pattern is now $n symbol(-s) long. Text is now $m symbol-s long")

    val left = Math.max(1, quickMatch.end-n-quickMatch.distance-5)
    val right = Math.min(m, quickMatch.end+quickMatch.distance+5)

    println("Analyzing the part from $left to $right inclusive")

    return addOffset(analyzeDetailed(pattern, text.substring(left..right)), left)

}

fun addOffset(analyzeDetailed: ApproximateMatch, left: Int): ApproximateMatch =
        ApproximateMatch(start = analyzeDetailed.start+left-1, end = analyzeDetailed.end+left-1,
                distance = analyzeDetailed.distance, text = analyzeDetailed.text, pattern =  analyzeDetailed.pattern)

fun analyzeDetailed(pattern: String, text: String): ApproximateMatch {
    val (n, m) = Pair(pattern.length, text.length)
    val matr = Array(n+1, {Array(m+1, { Cell()}) })

    for (row in 0..n)
        for (col in 0..m)
            calcCell(matr, row, col, pattern, text)

    TODO("not implemented")
}

fun getEditDistancePosition(matr: Array<Array<Cell>>, pattern: String, text: String): Int {
    val (n, m) = Pair(pattern.length, text.length)
    return getEditDistancePosition(matr[n], m)
}

fun getEditDistancePosition(lastRow: Array<Cell>, textLength: Int): Int =
    lastRow.zip(0..textLength).filter{it.first.direction != DIRECTION.SKIP}.minBy { it.first.value }!!.second


fun calculateOffset(str: String): Int =
        str.toCharArray().zip(0..str.length).filter{it.first != MISS_SIGN}.first().second

fun calcCell(matr: Array<Array<Cell>>, row: Int, col: Int, pattern: String, text:String): Cell {
    if (matr[row][col].calculated) return matr[row][col]
    val cell = calcCellDirect(matr, row, col, pattern, text)
    matr[row][col] = cell.copy(calculated = true)
    return matr[row][col]
}

fun calcCellDirect(matr: Array<Array<Cell>>, row: Int, col: Int, pattern: String, text: String): Cell {
    val n = pattern.length
    if (col == 0) return Cell(row.toShort(), DIRECTION.UP)
    if (row == 0) return calcCell(matr, row, col-1, pattern, text)
    val (textSymbol, patternSymbol) = Pair(text[col-1], pattern[row-1])
    val cell = min(
            makeCell(calcCell(matr, row, col-1, pattern, text).value, DIRECTION.LEFT, cost(patternSymbol, GAP_SIGN)),
            makeCell(calcCell(matr, row-1, col, pattern, text).value, DIRECTION.UP, cost(GAP_SIGN, textSymbol)),
            makeCell(calcCell(matr, row-1, col-1, pattern, text).value, DIRECTION.DIAG, cost(patternSymbol, textSymbol)),
            if (row == n) makeCell(calcCell(matr, row, col-1, pattern, text).value, DIRECTION.SKIP, 0)
            else Cell((n+100).toShort(), DIRECTION.UNEXPECTED)
    )

    return cell
}

fun min(vararg cells: Cell) = cells.minBy{it.value} ?: Cell(0, DIRECTION.UNEXPECTED)
fun makeCell(value: Short, direction: DIRECTION, cost: Short = 0) = Cell((value + cost).toShort(), direction)

fun analyzeQuick(pattern: String, text: String): QuickApproximateMatch {
    val (n, m) = Pair(pattern.length, text.length)
    // Да, это матрица, но нам всегда нужны только ее последние две строки, а не вся она целиком
    var matrix = Pair(IntArray(m+1), IntArray(m+1))
    val step = maxOf(1, n/100)

    for (x in 1..n) {
        val patternSymbol = pattern[x-1] // Не забываем про смещение
        matrix.second[0] = x // Получить Получить непустой образец из пустой строки можно только за x - извиняйте
        for (y in 1..m) {
            val textSymbol = text[y-1] // Не забываем про смещение
            matrix.second[y] = minInt(1+matrix.second[y-1], 1 + matrix.first[y],
                    matrix.first[y-1] + cost(patternSymbol, textSymbol))
        }
        matrix = Pair(matrix.second, IntArray(m+1))
        if (x%step == 0) println("Step $x/$n")
    }

    val (distance, end) = matrix.first.zip(0..m).minBy{it.first} ?: Pair(n, 0) // Крайне маловероятно, но все же
    println("Distance is $distance. End is at $end")
    return QuickApproximateMatch(end = end, distance = distance)
}
