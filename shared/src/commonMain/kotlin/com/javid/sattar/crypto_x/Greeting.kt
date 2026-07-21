/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}