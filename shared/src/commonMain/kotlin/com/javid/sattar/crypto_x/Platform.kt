/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform