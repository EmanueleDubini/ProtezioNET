package it.insubria.protezionet.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mName: EditText
    lateinit var mSurname: EditText
    lateinit var mEmail: EditText
    lateinit var mPassword: EditText
    lateinit var mConfirmPassword: EditText
    lateinit var fAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /*mName = findViewById(R.id.mName)
        mSurname = findViewById(R.id.mSurname)
        mEmail = findViewById(R.id.mEmail)
        mPassword = findViewById(R.id.mPassword)
        mConfirmPassword = findViewById(R.id.mConfirmPassword)*/

        fAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        if(fAuth.currentUser != null){
            val intent = Intent(this@RegisterActivity, MainActivity :: class.java)
            startActivity(intent)
            finish()
        }

        mRegisterButton.setOnClickListener(View.OnClickListener {
            fun onClick(view: View){
                var username: String = mName.text.toString()
                var surname: String = mSurname.text.toString()
                var email: String = mEmail.text.toString()
                var password: String = mPassword.text.toString()
                var confirmPassword: String = mConfirmPassword.text.toString()

                if(username.isEmpty()){
                    mName.error = "Name is Required"
                    return
                }

                if(surname.isEmpty()){
                    mSurname.error = "Surname is Required"
                    return
                }

                if(email.isEmpty()){
                    mEmail.error = "Email is Required"
                    return
                }

                if(password.isEmpty()){
                    mPassword.error = "Password is Required"
                    return
                }

                if(confirmPassword.isEmpty()){
                    mConfirmPassword.error = "Confirm Password is Required"
                    return
                }

                if(password.length < 6){
                    mPassword.error = "Password Must be >= 6 Characters"
                    return
                }

                if(password != confirmPassword){
                    mConfirmPassword.error = "Confirm Passwoard not equal to Password"
                    return
                }

                //se tutte le condizioni non sono valide, vuole dire che i deati inseriti dall'utente sono validi e possiamo effettuare la registrazione

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

                    if (it.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "User Created", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@RegisterActivity, MainActivity :: class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this@RegisterActivity, "Error ! " + it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            })
        }
    }
