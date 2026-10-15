package edu.umg.programacion2.appfinanzas.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FinanzasApp.db"
        private const val DATABASE_VERSION = 1

        // Tabla Usuarios
        const val TABLE_USERS = "usuarios"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_NAME = "nombre"
        const val COLUMN_USER_EMAIL = "correo"
        const val COLUMN_USER_PASSWORD = "password"

        // Tabla (Ingresos y Gastos)
        const val TABLE_TRANSACTIONS = "transacciones"
        const val COLUMN_TRANS_ID = "id"
        const val COLUMN_TRANS_USER_ID = "usuario_id"
        const val COLUMN_TRANS_TYPE = "tipo" // "INGRESO" o "GASTO"
        const val COLUMN_TRANS_CATEGORY = "categoria" // Salario, Gasolina, Despensa, etc.
        const val COLUMN_TRANS_AMOUNT = "monto"
        const val COLUMN_TRANS_DESC = "descripcion"
        const val COLUMN_TRANS_DATE = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla de Usuarios
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT)")

        // Tabla de (Ingresos y Gastos)
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
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    // REGISTRAR USUARIO
    fun registrarUsuario(nombre: String?, correo: String?, pass: String?): Boolean {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, nombre)
        values.put(COLUMN_USER_EMAIL, correo)
        values.put(COLUMN_USER_PASSWORD, pass)

        val resultado = db.insert(TABLE_USERS, null, values)
        db.close()
        return resultado != -1L
    }

    // VALIDAR INICIO DE SESIÓN
    fun validarLogin(correo: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_ID FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(correo, pass)
        )
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
        }
        cursor.close()
        db.close()
        return userId
    }

    // INSERTAR TRANSACCIÓN
    fun agregarTransaccion(userId: Int, tipo: String, categoria: String, monto: Double, descripcion: String, fecha: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRANS_USER_ID, userId)
            put(COLUMN_TRANS_TYPE, tipo) // "INGRESO" o "GASTO"
            put(COLUMN_TRANS_CATEGORY, categoria)
            put(COLUMN_TRANS_AMOUNT, monto)
            put(COLUMN_TRANS_DESC, descripcion)
            put(COLUMN_TRANS_DATE, fecha)
        }
        val resultado = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return resultado != -1L
    }
}