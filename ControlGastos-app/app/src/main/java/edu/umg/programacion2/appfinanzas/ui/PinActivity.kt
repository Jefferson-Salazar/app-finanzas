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

        val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
        val estaRegistrado = prefs.getBoolean("is_registered", false)

        // Si no se ha registrado entra directo a MainActivity la primera vez
        if (!estaRegistrado) {
            irAMain()
            return
        }

        setContentView(R.layout.activity_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnDesbloquear = findViewById<Button>(R.id.btnConfirmarPin)
        val pinGuardado = prefs.getString("user_pin", "")

        btnDesbloquear.setOnClickListener {
            val pinIngresado = etPin.text.toString().trim()
            if (pinIngresado == pinGuardado) {
                irAMain()
            } else {
                Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }

    private fun irAMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}