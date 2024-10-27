package com.example.nfc_virtual_card

import android.content.Context
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MyHostApduService : HostApduService() {
    private var cardId: String? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("CardData", Context.MODE_PRIVATE)
        cardId = sharedPreferences.getString("savedCardId", null)
    }

    override fun processCommandApdu(apdu: ByteArray, extras: Bundle?): ByteArray {
        val selectApdu = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x07, 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        return if (apdu.contentEquals(selectApdu)) {
            cardId?.toByteArray() ?: "No ID".toByteArray()
        } else {
            "Unknown Command".toByteArray()
        }


    }

//    override fun processCommandApdu(apdu: ByteArray, extras: Bundle?): ByteArray {
//        val selectApdu = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x07, 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
//
//        if (apdu.contentEquals(selectApdu)) {
//            // メインスレッドでアプリに通知
//            handler.post {
//                val intent = Intent("com.example.nfc_virtual_card.ACTION_CARD_ID_UPDATE")
//                intent.putExtra("CARD_ID", cardId)
//                sendBroadcast(intent)
//            }
//            return cardId?.toByteArray() ?: "No ID".toByteArray()
//        }
//        return "Unknown Command".toByteArray()
//    }

    override fun onDeactivated(reason: Int) {
        // NFCリーダーとの接続が終了した際の処理
    }
}
