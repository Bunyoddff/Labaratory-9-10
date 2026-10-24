package uz.ttpu.booknest.core.util

object PriceFormatter {
    fun format(priceCents: Int): String {
        val dollars = priceCents / 100
        val cents = priceCents % 100
        return "\$$dollars.${cents.toString().padStart(2, '0')}"
    }

    fun formatWithCurrency(priceCents: Int, currency: String): String =
        "${format(priceCents)} $currency"
}
