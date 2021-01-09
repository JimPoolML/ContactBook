package appjpm4everyone.contactbook.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import appjpm4everyone.contactbook.classes.ScheduleTable
import appjpm4everyone.contactbook.classes.StrongContact

class ScheduleDataBase(context: Context?) : SQLiteOpenHelper(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS) {

    companion object {
        private const val VERSION_BASEDATOS = 1
        private const val NOMBRE_BASEDATOS = "Schedule.db"
        private const val TABLE_NAME = "scheduleContact"
        private var TABLA_AGENDA  = "CREATE TABLE $TABLE_NAME (${ScheduleTable.ScheduleTable.COL_ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "${ScheduleTable.ScheduleTable.COL_EVENT} TEXT," +
                "${ScheduleTable.ScheduleTable.COL_DATE} TEXT," +
                "${ScheduleTable.ScheduleTable.COL_CLOCK} TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLA_AGENDA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_AGENDA")
        onCreate(db)
    }

    fun addContact(event: String?, date: String?, clock: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (db != null) {
            values.put("event", event)
            values.put("date", date)
            values.put("clock", clock)
            //insert data into DB
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun modifyContact(id: Int, event: String?, date: String?, clock: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (db != null) {
            values.put("event", event)
            values.put("date", date)
            values.put("clock", clock)
            //update data into DB
            db.update(TABLE_NAME, values, "${ScheduleTable.ScheduleTable.COL_ID}=$id", null)
        }
        db.close()
    }

    fun eraseContact(id: Int) {
        val db = writableDatabase
        db?.delete(TABLE_NAME, "${ScheduleTable.ScheduleTable.COL_ID}=$id", null)
        db.close()
        /* como en insert se puede utilizar db.execSQL de la sigueinte manera
        Eliminar un registro con execSQL(), utilizando argumentos
        String[] args = new String[]{ String.valueOf(id);};
         db.execSQL("DELETE FROM notas WHERE _id=?", args);
         */
    }


    @SuppressLint("Recycle")
    fun recoverContact(id: Int): ScheduleTable? {
        val db = readableDatabase
        val recoverValues = arrayOf("${ScheduleTable.ScheduleTable.COL_ID}", "event", "date", "clock")
        val c = db.query(TABLE_NAME, recoverValues, "${ScheduleTable.ScheduleTable.COL_ID}=$id", null, null, null, null, null)
        c?.moveToFirst()
        val scheduleTable = ScheduleTable(c.getInt(0), c.getString(1), c.getString(2), c.getString(3))
        db.close()
        c.close()
        return scheduleTable
    }


    fun recoverContactCursor(): Cursor? {
        val db = readableDatabase
        val recoverValues = arrayOf("${ScheduleTable.ScheduleTable.COL_ID}", "event", "date", "clock")
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
        val cursor = db.rawQuery("SELECT ${ScheduleTable.ScheduleTable.COL_ID} FROM $TABLE_NAME", null)
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