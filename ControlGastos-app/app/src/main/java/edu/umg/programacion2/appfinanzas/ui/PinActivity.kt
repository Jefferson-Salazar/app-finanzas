package edu.umg.programacion2.appfinanzas.ui

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
        val btnIngresarPin = findViewById<Button>(R.id.btnIngresarPin)

        btnIngresarPin.setOnClickListener {
            val pinIngresado = etPin.text.toString().trim()

            if (pinIngresado.length != 4) {
                Toast.makeText(this, "El PIN debe ser de 4 digitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pinIngresado == "1234") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "PIN incorrecto (usa 1234 para probar)", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }
}