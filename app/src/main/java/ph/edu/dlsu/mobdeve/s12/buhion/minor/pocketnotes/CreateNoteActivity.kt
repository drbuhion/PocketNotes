package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityCreateNoteBinding
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    var binding: ActivityCreateNoteBinding? = null
    private lateinit var database: DatabaseReference
    private var mAuth: FirebaseAuth? = null
    private var onlineUserId = ""

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth!!.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("note").child(onlineUserId)

        binding!!.llSpeech.setOnClickListener{
            val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            sttIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

            try {
                startActivityForResult(sttIntent, REQUEST_CODE_STT)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Speech to Text not supported", Toast.LENGTH_LONG).show()
            }
        }

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
                        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Failed to save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = TextUtils.concat(binding!!.etNoteText.text, " ", it[0])
                        binding!!.etNoteText.setText(recognizedText)
                    }
                }
            }
        }
    }
}