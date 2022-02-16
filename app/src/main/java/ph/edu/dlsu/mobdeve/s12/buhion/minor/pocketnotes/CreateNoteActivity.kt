package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityCreateNoteBinding

class CreateNoteActivity : AppCompatActivity() {

    var binding: ActivityCreateNoteBinding? = null
    private lateinit var database: DatabaseReference
    private var mAuth: FirebaseAuth? = null
    private var onlineUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth!!.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("note").child(onlineUserId)

        binding!!.btnSaveNote.setOnClickListener {

            val notes: String = binding!!.etNoteText.text.toString()

            if (notes.isEmpty()) {
                binding!!.etNoteText.error = "Note Required!"
            }else{

                val title = binding!!.etNoteTitle.text.toString()
                val note = binding!!.etNoteText.text.toString()
                val id: String? = database.push().key

                val data = Note(title,note,id)
                if (id != null) {
                    database.child(id).setValue(data).addOnSuccessListener {
                        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this,"failed to save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}