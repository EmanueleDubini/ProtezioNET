package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.mRegisterButton
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    /*lateinit var mName: EditText
    lateinit var mSurname: EditText
    lateinit var mEmail: EditText
    lateinit var mPassword: EditText
    lateinit var mConfirmPassword: EditText*/
    private lateinit var fAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /*mName = findViewById(R.id.mName)
        mSurname = findViewById(R.id.mSurname)
        mEmail = findViewById(R.id.mEmail)
        mPassword = findViewById(R.id.mPassword)
        mConfirmPassword = findViewById(R.id.mConfirmPassword)*/

        fAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBarRegister)

        //controllo se l'utente e gia loggato, ed accedera direttamente e non mostrera ne la pagina di login ne quella di registrazione
        if(fAuth.currentUser != null){
            val intent = Intent(this@RegisterActivity, MainActivity :: class.java)
            startActivity(intent)
            finish()
        }

        mRegisterButton.setOnClickListener {

            val username: String = mName.text.toString()
            val surname: String = mSurname.text.toString()
            val email: String = mEmailRegister.text.toString()
            val password: String = mPasswordRegister.text.toString()
            val confirmPassword: String = mConfirmPassword.text.toString()

            if (username.isEmpty()) {
                mName.error = "Name is Required"
            }

            if (surname.isEmpty()) {
                mSurname.error = "Surname is Required"
            }

            if (email.isEmpty()) {
                mEmailRegister.error = "Email is Required" //todo aggiungere il controlo come nella fase di login
            }

            if (password.isEmpty()) {
                mPasswordRegister.error = "Password is Required"
            }

            if (confirmPassword.isEmpty()) {
                mConfirmPassword.error = "Confirm Password is Required"
            }

            if (password.length < 6) {
                mPasswordRegister.error = "Password Must be >= 6 Characters"
            }

            if (password != confirmPassword) {
                mConfirmPassword.error = "Confirm Passwoard not equal to Password"
            }

            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i deati inseriti dall'utente sono validi e possiamo effettuare la registrazione

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "User Created", Toast.LENGTH_SHORT).show()

                    //so apre la main activity
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    progressBar.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this@RegisterActivity, "Error ! the password is invalid", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }

        }
    }
    fun goToLoginActivity(view: View){
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}
