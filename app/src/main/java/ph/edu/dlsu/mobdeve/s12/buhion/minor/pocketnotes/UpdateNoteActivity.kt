package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityUpdateNoteBinding

class UpdateNoteActivity : AppCompatActivity() {

    var binding: ActivityUpdateNoteBinding? = null
    private lateinit var btn_save : Button
    private lateinit var btn_delete : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        btn_save = binding!!.btnSaveNote
        btn_delete = binding!!.btnDelete

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

    }
}