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

// Actividad de registro: Permite al usuario ingresar sus datos personales y configurar su PIN de seguridad
class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etPin: EditText
    private lateinit var etConfirmarPin: EditText
    private lateinit var btnCompletarRegistro: Button
    private lateinit var btnCerrarRegistro: ImageButton // Botón para salir sin registrarse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Enlaza las variables del código con los elementos visuales del archivo XML
        etNombre = findViewById(R.id.etNombreRegistro)
        etApellido = findViewById(R.id.etApellidoRegistro)
        etPin = findViewById(R.id.etPinRegistro)
        etConfirmarPin = findViewById(R.id.etConfirmarPinRegistro)
        btnCompletarRegistro = findViewById(R.id.btnCompletarRegistro)
        btnCerrarRegistro = findViewById(R.id.btnCerrarRegistro)

        // Cierra la pantalla actual y regresa sin completar el registro al presionar la "X"
        btnCerrarRegistro.setOnClickListener {
            finish()
        }

        // Filtro estricto que elimina automáticamente cualquier espacio en blanco mientras el usuario escribe
        val sinEspaciosFilter = InputFilter { source, _, _, _, _, _ ->
            source.toString().filter { !it.isWhitespace() }
        }

        etNombre.filters = arrayOf(sinEspaciosFilter)
        etApellido.filters = arrayOf(sinEspaciosFilter)

        // Valida los datos y guarda las credenciales al hacer clic en completar registro
        btnCompletarRegistro.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val pin = etPin.text.toString().trim()
            val confirmarPin = etConfirmarPin.text.toString().trim()

            // Validación de que el campo nombre no esté vacío
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de que el PIN tenga exactamente 4 dígitos
            if (pin.length != 4) {
                Toast.makeText(this, "El PIN debe tener 4 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Comprueba que ambos PINs coincidan
            if (pin != confirmarPin) {
                Toast.makeText(this, "Los PINs no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guarda las credenciales de forma local usando SharedPreferences
            val prefs = getSharedPreferences("AppFinanzasPrefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("user_name", "$nombre $apellido".trim())
                putString("user_pin", pin)
                putBoolean("is_registered", true)
                apply() // Guarda los cambios de forma asíncrona
            }

            Toast.makeText(this, "¡Registro completado!", Toast.LENGTH_SHORT).show()

            // Redirige a MainActivity y limpia el historial para que no se pueda regresar al registro
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}