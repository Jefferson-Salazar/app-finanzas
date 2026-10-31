package edu.umg.programacion2.appfinanzas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.umg.programacion2.appfinanzas.R

// Actividad de seguridad: Solicita el PIN de 4 dígitos para autorizar el acceso a la aplicación
class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnConfirmarPin = findViewById<Button>(R.id.btnConfirmarPin)

        // Evento al presionar el botón de confirmar PIN
        btnConfirmarPin.setOnClickListener {
            val pinIngresado = etPin.text.toString().trim()

            // Valida que el PIN tenga exactamente 4 dígitos
            if (pinIngresado.length != 4) {
                Toast.makeText(this, "El PIN debe ser de 4 digitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Consulta el PIN guardado previamente en las Preferencias Compartidas (SharedPreferences)
            val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
            val pinGuardado = prefs.getString("user_pin", "1234")

            // Compara el PIN ingresado con el registrado
            if (pinIngresado == pinGuardado) {
                // Si es correcto, abre MainActivity indicando que ya no debe pedir el PIN otra vez
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("DEBE_PEDIR_PIN", false)
                    // Limpia las actividades anteriores para que al presionar "atrás" no regrese al PIN
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish() // Cierra la pantalla de PIN
            } else {
                // Si es incorrecto, muestra un error y limpia el campo de texto
                Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                etPin.text.clear()
            }
        }
    }
}