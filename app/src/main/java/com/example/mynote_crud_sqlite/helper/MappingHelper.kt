package com.example.mynote_crud_sqlite.helper

import android.database.Cursor
import com.example.mynote_crud_sqlite.db.DatabaseContract
import com.example.mynote_crud_sqlite.entity.Note

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColoumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColoumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColoumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColoumns.DATE))
                notesList.add(Note(id, title, description, date))
            }
        }
        return notesList
    }

}