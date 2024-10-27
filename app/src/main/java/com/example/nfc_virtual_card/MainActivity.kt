package com.example.nfc_virtual_card

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nfc_virtual_card.ui.theme.Nfc_virtual_cardTheme
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var cardId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // SharedPreferencesからカードIDを取得
        val sharedPreferences = getSharedPreferences("CardData", Context.MODE_PRIVATE)
        cardId = sharedPreferences.getString("savedCardId", null)

        // カードIDの更新を受信するレシーバを登録
//        val filter = IntentFilter("com.example.nfc_virtual_card.ACTION_CARD_ID_UPDATE")
//        registerReceiver(cardIdReceiver, filter)

        setContent {
            Nfc_virtual_cardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Greeting("Android")
                        RegisterButton(onRegister = { startCardReading() })
                        EmulateButton(onEmulate = { startEmulation() })
                    }
                }
            }
        }
    }

//    private val cardIdReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val receivedCardId = intent.getStringExtra("CARD_ID")
//            Toast.makeText(context, "Emulated Card ID: $receivedCardId", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(cardIdReceiver)
//    }

    private fun startCardReading() {
        nfcAdapter?.enableReaderMode(this, { tag ->
            cardId = tag.id?.joinToString("") { String.format("%02X", it) }
            cardId?.let {
                val sharedPreferences = getSharedPreferences("CardData", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("savedCardId", it).apply()
                runOnUiThread {
                    Toast.makeText(this, "Card ID Registered: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    private fun startEmulation() {
        if (cardId != null) {
            val intent = Intent(this, MyHostApduService::class.java)
            intent.putExtra("CARD_ID", cardId)
            startService(intent)
            Toast.makeText(this, "Emulation Started", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Register a card first!", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun RegisterButton(onRegister: () -> Unit) {
    Button(onClick = onRegister) {
        Text("Register NFC Card")
    }
}

@Composable
fun EmulateButton(onEmulate: () -> Unit) {
    Button(onClick = onEmulate) {
        Text("Emulate NFC Card")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "NFC card emulator for $name")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Nfc_virtual_cardTheme {
        Greeting("Android")
    }
}
