package ru.arriah.vetrf

import org.junit.Assert
import org.junit.Test

/**
 * Created by shevchenko-dv-100705 on 21.06.2017.
 */
class StringTest {
    @Test
    fun simpleCheck() = println("If you read this, JUnit executed fine")

    @Test
    fun tgacSymbolsShouldBeRecognized() {
        val arr = charArrayOf('A', 'T', 'G', 'C')
        arr.forEach{Assert.assertTrue("Sybmol $it should have been recognized", isTGAC(it))}
    }

    @Test
    fun clearStringFromNoiseText() = Assert.assertEquals("TGACAAC", clearFromNoise("111TG22AC\n\tAAC"))


    @Test
    fun costOfEqualSymbolsShouldBeZero() = Assert.assertEquals(0, cost('a', 'a'))

    @Test
    fun costOfDifferentSymbolsShouldBeOne() = Assert.assertEquals(1, cost('a', 'c'))

    @Test
    fun nonEmptyStringShouldPass() = checkNonEmpty("Something")

    @Test(expected = IllegalStateException::class)
    fun emptyStringShouldNotPass() = checkNonEmpty("")

    @Test
    fun exactMatchShouldWork() = Assert.assertEquals(ExactMatch(4,5), analyzeStripped("AA", "BBBAACC"))

    @Test
    fun approximateMatchShouldWork() = Assert.assertEquals(QuickApproximateMatch(3, 1), analyzeQuick("AB", "TGACC"))
}