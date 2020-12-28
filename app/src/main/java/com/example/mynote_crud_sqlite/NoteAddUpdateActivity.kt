package com.example.mynote_crud_sqlite

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mynote_crud_sqlite.databinding.ActivityNoteAddUpdateBinding
import com.example.mynote_crud_sqlite.db.DatabaseContract
import com.example.mynote_crud_sqlite.db.NoteHelper
import com.example.mynote_crud_sqlite.entity.Note
import java.text.SimpleDateFormat
import java.util.*


class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var noteHelper: NoteHelper

    private lateinit var binding: ActivityNoteAddUpdateBinding

    companion object{
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        note = intent.getParcelableExtra<Note>(EXTRA_NOTE)
        if (note != null){
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        }else{
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit){
            actionBarTitle = "Ubah"
            btnTitle = "Update"

            note?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }
        }else{
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        //Open database
        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnSubmit.text = btnTitle

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_submit){
            val title = binding.edtTitle.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()
            // Cek fiel terisi ata tidak
            if (title.isEmpty()){
                binding.edtTitle.error = "Field can not be blank"
                return
            }
            if (description.isEmpty()){
                binding.edtTitle.error = "Field can not be blank"
                return
            }

            // rubah value entity note menadi value yang diinput edtext
            note?.title = title
            note?.description = description
            Log.d("input text= ", title)

            //membuat variable intent dengan data
            val intentdata = Intent()
            intentdata.putExtra(EXTRA_NOTE, note)
            intentdata.putExtra(EXTRA_POSITION, position)

            //membuat variabel value untuk dimasukan ke database melalui DatabaseContract
            val values = ContentValues()
            values.put(DatabaseContract.NoteColoumns.TITLE,title)
            values.put(DatabaseContract.NoteColoumns.DESCRIPTION,description)
            Log.d(" simpan database= ", values.toString())


            if (isEdit){
                //save to Database!
                val result = noteHelper.update(note?.id.toString(), values).toLong()
                if (result > 0){
                    setResult(RESULT_UPDATE, intentdata)
                    finish()
                    Log.d("data result= ", result.toString())
                }else{
                    Toast.makeText(this@NoteAddUpdateActivity, "Gagal Update Data!", Toast.LENGTH_SHORT).show()
                }
            }else{
                note?.date = getCurrentDate()
                values.put(DatabaseContract.NoteColoumns.DATE, getCurrentDate())
                val result = noteHelper.insert(values)

                if (result > 0) {
                    note?.id = result.toInt()
                    setResult(RESULT_ADD, intentdata)
                    finish()
                }else{
                    Toast.makeText(this@NoteAddUpdateActivity, " Gagal update tipe 2", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/mm/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit){
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int){
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        }else{
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                if (isDialogClose){
                    finish()
                }else{
                    val result = noteHelper.deleteById(note?.id.toString()).toLong()
                    if (result > 0) {
                        val intentdata = Intent()
                        intentdata.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intentdata)
                        finish()
                    }else{
                        Toast.makeText(this@NoteAddUpdateActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close()
    }
}