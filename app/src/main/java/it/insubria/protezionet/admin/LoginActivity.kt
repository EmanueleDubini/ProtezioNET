package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
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

        fAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBarLogin)

        // title = TITOLO
    }

    fun checkLogin(v: View) {

        val email: String = mEmailLogin.text.toString()   //binding.UsernameField.text.toString()    //editTextUsername.getText().toString()
        val password: String = mPasswordLogin.text.toString()   //binding.PasswordField.text.toString()                             //editTextPassword.getText().toString()


        if (email.isEmpty()) {
            mEmailLogin.error = "Email is Required"
        }

        else if (password.isEmpty()) {
            mPasswordLogin.error = "Password is Required"
        }


        else if (password.length < 6) {
            mPasswordLogin.error = "Password Must be greater than 6 Characters"
        }



        else if (!isValidEmail(email)) {
            mEmailLogin.error = getString(R.string.invalid_email)       //binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
        }

        /*if (!isValidPassword(password)) {
            println("password inserita:$password") //stampa di debug

            PasswordField.error = getString(R.string.invalid_password)   //binding.PasswordField.error = "Invalid Password"                 //editTextUsername.setError("Invalid Password")
        }*/
        else {
        //avvio la progress bar
            progressBar.visibility = View.VISIBLE


        //autentificazione dell'utente
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            //quando viene comppletata l'autentificazione
            //validation complete, aprire una nuova activity e fare il finish() del login

                if(it.isSuccessful){
                    Toast.makeText(this@LoginActivity, "Logged In Successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MainActivity :: class.java) //Creiamo un Intent passandogli il Context ( this@MainActivity ) e in più passiamo l'informazione per rendere l'intent esplicito, l'activity che deve essere eseguita, dicendo che deve aprire l'activity la cui classe è SecondActivity
                    startActivity(intent)
                    progressBar.visibility = View.INVISIBLE

                //creato l'intent con le informazioni da passare alla SecondActivity, inviamo un richiesta ad ART tramite lo startActivity() passandogli l'intent di tipo esplicito.
                //l'androidRuntime, sa che oggetto deve lanciare "MainActivity :: class.java" e quindi istanzia un opggetto di tipo MainActivity e chiama su di esso il metodo onCreate() che crea l'interfaccia e inserisce al suo interno il contenuto che abbiamo
                // inserito nel file activity_main.xml

                }   else { //todo se si inserisce un email che rispetta il controllo della regex ma non è presente su firebase authentication non succede nulla
                    Toast.makeText(this@LoginActivity, "Error ! the password is invalid", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    }
            }
        }
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

    fun goToRegisterActivity(view: View){
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}
