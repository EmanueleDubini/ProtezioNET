package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import it.insubria.protezionet.Common.ForgotPassword
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

/**
 *
 * Entry point dell'app (finestra di login),  associato
 * ad activity_main.xml
 *
 * - checklogin()
 * - isValidPassword()
 * - ]isValidUsername()
 */
class LoginActivity : AppCompatActivity() {

    // val TITOLO = "ProtezioNET"  -G:  teniamo come esempio commentato perchè potrebbe essere utile
    //                                  nelle altre Activity

    //private lateinit var binding: ActivityMainBinding

    var returnValue = 0.0
    val TAG ="MainActivity"

    private lateinit var fAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        setContentView(R.layout.activity_login)


        /*val restorePassword: TextView = findViewById(R.id.forgotPassword) //view!!.findViewById(R.id.mRegisterButton)
        restorePassword.setOnClickListener(this)*/

        fAuth = FirebaseAuth.getInstance()
        progressBar = progressBarLogin

        // title = TITOLO
    }



    fun checkLogin(v: View) {

        val email: String = mEmailLogin.text.toString() .trim()  //binding.UsernameField.text.toString()    //editTextUsername.getText().toString()
        val password: String = mPasswordLogin.text.toString().trim()  //binding.PasswordField.text.toString()                             //editTextPassword.getText().toString()


        if (email.isEmpty()) { //todo generare le stringhe
            mEmailLogin.error = "Email is Required"
            mEmailLogin.requestFocus()
        }

        else if (password.isEmpty()) {
            mPasswordLogin.error = "Password is Required"
            mPasswordLogin.requestFocus()
        }


        else if (password.length < 6) {
            mPasswordLogin.error = "Password Must be greater than 6 Characters"
            mPasswordLogin.requestFocus()
        }



        //else if (!isValidEmail(email)) {
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailLogin.error = getString(R.string.invalid_email)       //binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
            mEmailLogin.requestFocus()
        }

        /*if (!isValidPassword(password)) {
            println("password inserita:$password") //stampa di debug

            PasswordField.error = getString(R.string.invalid_password)   //binding.PasswordField.error = "Invalid Password"                 //editTextUsername.setError("Invalid Password")
        }*/

        //se quello che e stato inserito è tutto corretto
        else {
        //avvio la progress bar
            progressBar.visibility = View.VISIBLE


        //autentificazione dell'utente
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            //quando viene comppletata l'autentificazione
            //validation complete, aprire una nuova activity e fare il finish() del login

                if(it.isSuccessful){
                    //prima di fare accedere l'utente verifichiamo se la mail con la quale accede è gia stata verificata, tramite la ricezione di un email e clickando sul link di conferma
                     val user: FirebaseUser? = fAuth.currentUser

                    //controlliamo se l'email è gia stata verificata
                    if(user!!.isEmailVerified){

                        Toast.makeText(this@LoginActivity, "Logged In Successfully", Toast.LENGTH_SHORT).show()

                        //redirect to user profile
                        //Creiamo un Intent passandogli il Context ( this@LoginActivity ) e in più passiamo l'informazione per rendere l'intent esplicito, l'activity che deve essere eseguita, dicendo che deve aprire l'activity la cui classe è MainAcivity
                        val intent = Intent(this@LoginActivity, MainActivity :: class.java)
                        startActivity(intent)
                        //creato l'intent con le informazioni da passare alla SecondActivity, inviamo un richiesta ad ART tramite lo startActivity() passandogli l'intent di tipo esplicito.
                        //l'androidRuntime, sa che oggetto deve lanciare "MainActivity :: class.java" e quindi istanzia un opggetto di tipo MainActivity e chiama su di esso il metodo onCreate() che crea l'interfaccia e inserisce al suo interno il contenuto che abbiamo
                        // inserito nel file activity_main.xml

                        progressBar.visibility = View.GONE
                    }
                    //se la mail non e stata ancora verificata
                    else{
                        user.sendEmailVerification()
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Check your email to verify your account", Toast.LENGTH_LONG).show()
                    }

                }else { //todo se si inserisce un email che rispetta il controllo della regex ma non è presente su firebase authentication non succede nulla
                    // Toast.makeText(this@LoginActivity, "Email or Password is incorrect", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }


    fun restorePassword(v: View){
        val intent = Intent(this@LoginActivity, ForgotPassword :: class.java)
        startActivity(intent)

    }
    /*fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }*/

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")     /*"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"*/
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()  //se l'indirizzo email soddisfa la reg-ex ritornerà un valore true altrimenti false
    }

    /*fun goToRegisterActivity(view: View){
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }*/
}
