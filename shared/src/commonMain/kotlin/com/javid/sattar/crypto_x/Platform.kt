package com.javid.sattar.crypto_x

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform