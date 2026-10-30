package edu.umg.programacion2.appfinanzas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.umg.programacion2.appfinanzas.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etPin: EditText
    private lateinit var etConfirmarPin: EditText
    private lateinit var btnCompletarRegistro: Button
    private lateinit var btnCerrarRegistro: ImageButton // Declaración del botón de salida

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Enlazamos con los IDs de activity_register.xml
        etNombre = findViewById(R.id.etNombreRegistro)
        etApellido = findViewById(R.id.etApellidoRegistro)
        etPin = findViewById(R.id.etPinRegistro)
        etConfirmarPin = findViewById(R.id.etConfirmarPinRegistro)
        btnCompletarRegistro = findViewById(R.id.btnCompletarRegistro)
        btnCerrarRegistro = findViewById(R.id.btnCerrarRegistro) // Enlace del botón X

        // Listener para el botón de cerrar (regresa a la pantalla anterior sin registrar)
        btnCerrarRegistro.setOnClickListener {
            finish()
        }

        // Filtro estricto para prohibir espacios en el nombre y apellido mientras se escribe
        val sinEspaciosFilter = InputFilter { source, _, _, _, _, _ ->
            source.toString().filter { !it.isWhitespace() }
        }

        etNombre.filters = arrayOf(sinEspaciosFilter)
        etApellido.filters = arrayOf(sinEspaciosFilter)

        btnCompletarRegistro.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val pin = etPin.text.toString().trim()
            val confirmarPin = etConfirmarPin.text.toString().trim()

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pin.length != 4) {
                Toast.makeText(this, "El PIN debe tener 4 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pin != confirmarPin) {
                Toast.makeText(this, "Los PINs no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar credenciales de forma local
            val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("user_name", "$nombre $apellido".trim())
                putString("user_pin", pin)
                putBoolean("is_registered", true)
                apply()
            }

            Toast.makeText(this, "¡Registro completado!", Toast.LENGTH_SHORT).show()

            // Redirigir a MainActivity limpiando el historial de navegación
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}