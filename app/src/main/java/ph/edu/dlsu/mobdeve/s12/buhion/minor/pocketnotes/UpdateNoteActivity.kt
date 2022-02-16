package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityUpdateNoteBinding
import java.io.File
import java.io.FileOutputStream

class UpdateNoteActivity : AppCompatActivity() {

    var binding: ActivityUpdateNoteBinding? = null
    private lateinit var btn_save : Button
    private lateinit var btn_delete : ImageButton
    private lateinit var btn_export : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        btn_save = binding!!.btnSaveNote
        btn_delete = binding!!.btnDelete
        btn_export = binding!!.btnDownload

        val et_title : TextView = binding!!.etNoteTitle
        val et_text : TextView = binding!!.etNoteText

        val bundle: Bundle? = intent.extras
        val title = bundle!!.getString("title")
        val text = bundle.getString("note")
        val id = bundle.get("id")

        et_title.text = title
        et_text.text = text

        btn_save.setOnClickListener{
            val title = et_title.text.toString()
            val note = et_text.text.toString()

            val data = Note(title,note, id as String)

            val reference = FirebaseDatabase.getInstance().reference.child("note").child(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            reference.child(id).setValue(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    Toast.makeText(this, "failed to update " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_delete.setOnClickListener{
            val reference = FirebaseDatabase.getInstance().reference.child("note").child(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            reference.child(id as String).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    Toast.makeText(this,"failed to delete " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_export.setOnClickListener{
            TODO()
        }
    }
}