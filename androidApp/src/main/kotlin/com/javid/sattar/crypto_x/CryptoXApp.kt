/*
 * Copyright (c) 2026 Javid Sattar
 * Email: javiddeveloper@gmail.com
 */
package com.javid.sattar.crypto_x

import android.app.Application

class CryptoXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
