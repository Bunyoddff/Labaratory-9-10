package uz.ttpu.booknest.core.util

import uz.ttpu.booknest.core.model.Book

fun Book.displayPrice(): String = PriceFormatter.format(priceCents)
