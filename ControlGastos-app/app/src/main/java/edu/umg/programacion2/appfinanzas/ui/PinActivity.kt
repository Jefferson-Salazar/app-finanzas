package edu.umg.programacion2.appfinanzas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.umg.programacion2.appfinanzas.R
import edu.umg.programacion2.appfinanzas.dao.Database

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnIngresarPin = findViewById<Button>(R.id.btnIngresarPin)
        val db = Database(this)

        btnIngresarPin.setOnClickListener {
            val pinIngresado = etPin.text.toString().trim()

            if (pinIngresado.length != 4) {
                Toast.makeText(this, "El PIN debe ser de 4 digitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.validarPin(pinIngresado)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }
}