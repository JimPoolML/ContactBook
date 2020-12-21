package appjpm4everyone.contactbook.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import appjpm4everyone.contactbook.classes.StrongContact
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_ADDRESS
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_CELLPHONE
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_EMAIL
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_ID
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_LOCAL_PHONE
import appjpm4everyone.contactbook.classes.StrongContact.StrongContact.COL_NAME

class MyDataBase(context: Context?) : SQLiteOpenHelper(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS) {

    companion object {
        private const val VERSION_BASEDATOS = 1
        private const val NOMBRE_BASEDATOS = "ContactBook.db"
        private const val TABLE_NAME = "nameContact"
        private var TABLA_AGENDA  = "CREATE TABLE $TABLE_NAME (${COL_ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "$COL_NAME TEXT," +
                "$COL_ADDRESS TEXT," +
                "$COL_CELLPHONE TEXT," +
                "$COL_LOCAL_PHONE TEXT," +
                "$COL_EMAIL TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLA_AGENDA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_AGENDA")
        onCreate(db)
    }

    fun addContact(name: String?, address: String?, cellPhone: String?, localPhone: String?, email: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (db != null) {
            values.put("name", name)
            values.put("address", address)
            values.put("cellPhone", cellPhone)
            values.put("localPhone", localPhone)
            values.put("email", email)
            //insert data into DB
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun modifyContact(id: Int, name: String?, address: String?, cellPhone: String?, localPhone: String?, email: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (db != null) {
            values.put("name", name)
            values.put("address", address)
            values.put("cellPhone", cellPhone)
            values.put("localPhone", localPhone)
            values.put("email", email)
            //update data into DB
            db.update(TABLE_NAME, values, "$COL_ID=$id", null)
        }
        db.close()
    }

    fun eraseContact(id: Int) {
        val db = writableDatabase
        db?.delete(TABLE_NAME, "$COL_ID=$id", null)
        db.close()
        /* como en insert se puede utilizar db.execSQL de la sigueinte manera
        Eliminar un registro con execSQL(), utilizando argumentos
        String[] args = new String[]{ String.valueOf(id);};
         db.execSQL("DELETE FROM notas WHERE _id=?", args);
         */
    }


    @SuppressLint("Recycle")
    fun recoverContact(id: Int): StrongContact? {
        val db = readableDatabase
        val recoverValues = arrayOf("$COL_ID", "name", "address", "cellPhone", "localPhone", "email")
        val c = db.query(TABLE_NAME, recoverValues, "$COL_ID=$id", null, null, null, null, null)
        c?.moveToFirst()
        val strongContact = StrongContact(c!!.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5))
        db.close()
        c.close()
        return strongContact
    }


    fun recoverContactCursor(): Cursor? {
        val db = readableDatabase
        val recoverValues = arrayOf("$COL_ID", "name", "cellPhone")
        /*query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
         Con este método conseguimos leer un registro de la tabla.
         Como primer parámetro "table" nos pide el nombre de la tabla
         , "columns" las columnas que queremos recuperar,
         con "selection" le indicamos el registro a recuperar (si recuperamos con el id  -> "_id=" + id),
         o los registros a recuperar "selectionArgs",
         "groupBy" para agrupar los registros consultados ,
          "having" es un filtro para incluir los registros en el cursor (este parámetro se usaría con groupBy),
          "orderBy" para ordenar las filas y "limit" para limitar el numero de filas consultadas.*/
        return db.query(TABLE_NAME, recoverValues, null, null, null, null, null, null)
    }

    fun rowNumber(): Int {
        return DatabaseUtils.queryNumEntries(writableDatabase, TABLE_NAME).toInt()
    }

    fun recoverIds(): IntArray? {
        val dataId: IntArray
        var i: Int
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_ID FROM $TABLE_NAME", null)
        if (cursor.count > 0) {
            dataId = IntArray(cursor.count)
            i = 0
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                dataId[i] = cursor.getInt(0)
                i++
                cursor.moveToNext()
            }
        } else dataId = IntArray(0)
        cursor.close()
        return dataId
    }

}