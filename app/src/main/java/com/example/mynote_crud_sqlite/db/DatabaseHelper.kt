package com.example.mynote_crud_sqlite.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mynote_crud_sqlite.db.DatabaseContract.NoteColoumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private val DATABASE_NAME = "dbNoteApp"
        private val DATABASE_VERSION = 1

        private val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                "(${DatabaseContract.NoteColoumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DatabaseContract.NoteColoumns.TITLE} TEXT NOT NULL," +
                "${DatabaseContract.NoteColoumns.DESCRIPTION} TEXT NOT NULL," +
                "${DatabaseContract.NoteColoumns.DATE} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}