package ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ph.edu.dlsu.mobdeve.s12.buhion.minor.pocketnotes.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {

    var binding: ActivityLandingBinding? = null

    private lateinit var googleSignInClient: GoogleSignInClient
    var googleSignIn: SignInButton? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        googleSignIn = binding!!.btnSignin
        googleSignIn!!.setSize(SignInButton.SIZE_STANDARD)



        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1064735599648-d53vgdknhvlvn8nmrse4rocr94ivuog6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        googleSignIn!!.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent

        startForResult.launch(signInIntent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->

        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result!!.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("LANDING_TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("LANDING_TAG", "Google sign in failed", e)
            }
        } else {
            Snackbar.make(binding!!.root,
                "UNSUCCESSFUL",
                Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                SavedPreference.setEmail(this,account.email.toString())
                SavedPreference.setUsername(this,account.displayName.toString())
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
                Toast.makeText(this,"Sign-in Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    object SavedPreference {

        const val user_email= "email"
        const val user_name="username"

        private  fun getSharedPreference(ctx: Context?): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }

        private fun  editor(context: Context, const:String, string: String){
            getSharedPreference(
                context
            )?.edit()?.putString(const,string)?.apply()
        }

        fun setEmail(context: Context, email: String){
            editor(
                context,
                user_email,
                email
            )
        }

        fun setUsername(context: Context, username:String){
            editor(
                context,
                user_name,
                username
            )
        }
    }
    /*
    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
    */
}