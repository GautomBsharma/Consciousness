package com.example.theconsciousness.DataHelperClasess

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.theconsciousness.Models.Note

class DatabaseHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null,
DATABASE_VERSION) {



    companion object{
        private const val DATABASE_NAME="dbnotes"
        private const val DATABASE_VERSION=1
        private const val TABLE_NAME="allnotes"
        private const val  COL_ID="id"
        private const val COL_TIME="time"

        private const val COL_NOTE="note"
        private const val EVENT_TYPE="event_type"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME($COL_ID INTEGER PRIMARY KEY, $COL_TIME TEXT, $EVENT_TYPE TEXT, $COL_NOTE TEXT)"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, ov: Int, nv: Int) {

        val dropTable = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTable)
        onCreate(db)
    }
    fun insertN(note: Note){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TIME,note.time)
            put(COL_NOTE,note.notedes)
            put(EVENT_TYPE,note.eventType)
        }
        db.insert(TABLE_NAME,null,values)
        db.close()

    }
    fun getallNote() :List<Note>{
        val notelist = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query,null)
        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME))
            val eventType = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TYPE))
            val notedes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE))

            val note = Note(id, time,eventType, notedes)
            notelist.add(note)
        }
        cursor.close()
        db.close()
        return notelist
    }
    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TIME, note.time)
            put(COL_NOTE, note.notedes)
            put(EVENT_TYPE, note.eventType)
        }
        val whereCl = "$COL_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereCl, whereArgs)
        db.close()
    }
    fun getNotebyId(notid :Int) :Note{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = $notid"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        val time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME))
        val notedes = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE))
        val eventType = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TYPE))

        cursor.close()
        db.close()
        return Note(id,time, eventType,notedes)

    }
    fun deleteNote(notid :Int){

        val db= writableDatabase
        val whereCl = "$COL_ID = ?"
        val whereArgs = arrayOf(notid.toString())
        db.delete(TABLE_NAME,whereCl,whereArgs)
        db.close()
    }

}