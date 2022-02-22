package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityUpdateNoteBinding
import java.io.File
import java.io.FileOutputStream
import java.util.*

class UpdateNoteActivity : AppCompatActivity() {

    var binding: ActivityUpdateNoteBinding? = null
    private lateinit var btn_save : Button
    private lateinit var btn_delete : ImageButton
    private lateinit var btn_pdf : ImageButton
    private lateinit var btn_email : ImageButton
    private lateinit var btn_hear : ImageButton
    private lateinit var btn_mic : ImageButton


    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.UK
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        btn_save = binding!!.btnSaveNote
        btn_delete = binding!!.btnDelete
        btn_pdf = binding!!.btnDownload
        btn_email = binding!!.btnEmail
        btn_mic = binding!!.btnMic
        btn_hear = binding!!.btnHear

        val et_title : TextView = binding!!.etNoteTitle
        val et_text : TextView = binding!!.etNoteText

        val bundle: Bundle? = intent.extras
        val title = bundle!!.getString("title")
        val text = bundle.getString("note")
        val id = bundle.get("id")

        et_title.text = title
        et_text.text = text

        btn_mic.setOnClickListener{
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

        btn_hear.setOnClickListener{
            val text = binding!!.etNoteText.text.toString().trim()
            if (text.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {
                    textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                Toast.makeText(this, "Text cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

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

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

}