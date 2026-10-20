package edu.umg.programacion2.appfinanzas.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FinanzasApp.db"
        private const val DATABASE_VERSION = 2

        // Tabla Usuarios
        const val TABLE_USERS = "usuarios"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_NAME = "nombre"
        const val COLUMN_USER_EMAIL = "correo"
        const val COLUMN_USER_PIN = "pin"

        // Tabla Transacciones (Ingresos y Gastos)
        const val TABLE_TRANSACTIONS = "transacciones"
        const val COLUMN_TRANS_ID = "id"
        const val COLUMN_TRANS_USER_ID = "usuario_id"
        const val COLUMN_TRANS_TYPE = "tipo" // "INGRESO" o "GASTO"
        const val COLUMN_TRANS_CATEGORY = "categoria"
        const val COLUMN_TRANS_AMOUNT = "monto"
        const val COLUMN_TRANS_DESC = "descripcion"
        const val COLUMN_TRANS_DATE = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, "
                + COLUMN_USER_PIN + " TEXT)")

        val createTransactionsTable = ("CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TRANS_USER_ID + " INTEGER, "
                + COLUMN_TRANS_TYPE + " TEXT, "
                + COLUMN_TRANS_CATEGORY + " TEXT, "
                + COLUMN_TRANS_AMOUNT + " REAL, "
                + COLUMN_TRANS_DESC + " TEXT, "
                + COLUMN_TRANS_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_TRANS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))")

        db?.execSQL(createUsersTable)
        db?.execSQL(createTransactionsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // 1. REGISTRAR USUARIO
    fun registrarUsuario(nombre: String, correo: String, pin: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, nombre)
            put(COLUMN_USER_EMAIL, correo)
            put(COLUMN_USER_PIN, pin)
        }
        val resultado = db.insert(TABLE_USERS, null, values)
        db.close()
        return resultado != -1L
    }

    // 2. VERIFICAR SI EXISTE ALGUN USUARIO
    fun hayUsuarioRegistrado(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count > 0
    }

    // 3. VALIDAR PIN DE ACCESO DE 4 DIGITOS
    fun validarPin(pinIngresado: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_ID FROM $TABLE_USERS WHERE $COLUMN_USER_PIN = ?",
            arrayOf(pinIngresado)
        )
        val esValido = cursor.moveToFirst()
        cursor.close()
        db.close()
        return esValido
    }

    // 4. OBTENER ID DEL USUARIO ACTUAL
    fun obtenerUsuarioId(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_USER_ID FROM $TABLE_USERS LIMIT 1", null)
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return userId
    }

    // 5. REGISTRAR NUEVA TRANSACCION (INGRESO O GASTO)
    fun agregarTransaccion(userId: Int, tipo: String, categoria: String, monto: Double, descripcion: String, fecha: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRANS_USER_ID, userId)
            put(COLUMN_TRANS_TYPE, tipo)
            put(COLUMN_TRANS_CATEGORY, categoria)
            put(COLUMN_TRANS_AMOUNT, monto)
            put(COLUMN_TRANS_DESC, descripcion)
            put(COLUMN_TRANS_DATE, fecha)
        }
        val resultado = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return resultado != -1L
    }

    // 6. CALCULAR TOTALES DE INGRESOS O GASTOS
    fun obtenerTotalPorTipo(userId: Int, tipo: String): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_TRANS_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TRANS_USER_ID = ? AND $COLUMN_TRANS_TYPE = ?",
            arrayOf(userId.toString(), tipo)
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }
}