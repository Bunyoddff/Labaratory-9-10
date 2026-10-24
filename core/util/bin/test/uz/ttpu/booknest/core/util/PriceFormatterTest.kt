package uz.ttpu.booknest.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceFormatterTest {

    @Test
    fun formatsWholeDollars() {
        assertEquals("$35.00", PriceFormatter.format(3500))
    }

    @Test
    fun padsSingleDigitCents() {
        assertEquals("$12.05", PriceFormatter.format(1205))
    }

    @Test
    fun formatsZero() {
        assertEquals("$0.00", PriceFormatter.format(0))
    }

    @Test
    fun formatsBoundaryNinetyNineCents() {
        assertEquals("$0.99", PriceFormatter.format(99))
    }
}
