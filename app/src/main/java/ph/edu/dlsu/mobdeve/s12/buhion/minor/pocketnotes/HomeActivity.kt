package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.R
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityHomeBinding
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(),NotesAdapter.MyOnClickListener {

    var binding: ActivityHomeBinding? = null

    private lateinit var database : DatabaseReference
    private lateinit var notesRecyclerview : RecyclerView
    private lateinit var notesArrayList : ArrayList<Note>
    private lateinit var tempArrayList : ArrayList<Note>

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    private var onlineUserId = ""

    private lateinit var sv_notes : SearchView
    private lateinit var pb_connection : ProgressBar
    private lateinit var tv_connection : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val fab_create = binding!!.fabCreateNote

        auth = FirebaseAuth.getInstance()

        fab_create.setOnClickListener{
            startActivity(Intent(this,CreateNoteActivity::class.java))
        }

        pb_connection = binding!!.pbConnection
        tv_connection = binding!!.tvConnection

        sv_notes = binding!!.svNotes
        sv_notes.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    notesArrayList.forEach {
                        if (it.title!!.lowercase(Locale.getDefault()).contains(searchText) ||
                            it.text!!.lowercase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                            notesRecyclerview.adapter = NotesAdapter(tempArrayList,this@HomeActivity)
                            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            notesRecyclerview.layoutManager = staggeredGridLayoutManager
                        }
                    }
                    notesRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    tempArrayList.clear()
                    tempArrayList.addAll(notesArrayList)
                    notesRecyclerview.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1064735599648-d53vgdknhvlvn8nmrse4rocr94ivuog6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        notesRecyclerview = binding!!.rvNotes
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesRecyclerview.layoutManager = staggeredGridLayoutManager
        notesRecyclerview.setHasFixedSize(true)
        notesArrayList = arrayListOf()
        tempArrayList = arrayListOf()
        getUserData()

    }

    private fun getUserData() {

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth!!.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("note").child(onlineUserId)
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val data = userSnapshot.getValue(Note::class.java)
                        notesArrayList.add(0,data!!)
                    }
                    pb_connection.visibility = View.GONE
                    tv_connection.visibility = View.GONE
                    notesRecyclerview.adapter = NotesAdapter(notesArrayList,this@HomeActivity)
                }else{
                    pb_connection.visibility = View.GONE
                    tv_connection.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onClick(position: Int) {
        val intent = Intent(this@HomeActivity,UpdateNoteActivity::class.java)
        intent.putExtra("title",notesArrayList[position].title)
        intent.putExtra("note",notesArrayList[position].text)
        intent.putExtra("id",notesArrayList[position].id)
        startActivity(intent)
    }
}