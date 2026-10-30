package edu.umg.programacion2.appfinanzas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.umg.programacion2.appfinanzas.R

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnConfirmarPin = findViewById<Button>(R.id.btnConfirmarPin)

        btnConfirmarPin.setOnClickListener {
            val pinIngresado = etPin.text.toString().trim()

            if (pinIngresado.length != 4) {
                Toast.makeText(this, "El PIN debe ser de 4 digitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
            val pinGuardado = prefs.getString("user_pin", "1234")

            if (pinIngresado == pinGuardado) {
                // Indicamos a MainActivity que ya pasamos la seguridad del PIN
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("DEBE_PEDIR_PIN", false)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }
}