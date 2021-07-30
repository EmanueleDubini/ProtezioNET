package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import it.insubria.protezionet.common.ForgotPassword
import it.insubria.protezionet.common.Person
import it.insubria.protezionet.common.StaticBoolean
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.regex.Pattern
//todo il login deve poter essere effettuato solamente se l'utente che cerca di acceder usa creddenziali da amministratore
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
    //istanza utilizzata per ottenere un riferimento al nodo del database da cui leggere
    private lateinit var reference: DatabaseReference
    private lateinit var progressBar: ProgressBar





    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        setContentView(R.layout.activity_login)


        /*val restorePassword: TextView = findViewById(R.id.forgotPassword) //view!!.findViewById(R.id.mRegisterButton)
        restorePassword.setOnClickListener(this)*/

        fAuth = FirebaseAuth.getInstance()

        reference = FirebaseDatabase.getInstance().getReference("person") //rifermento al nodo person da cui leggere

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


        //autentificazione dell'utente, verifichiamo se la mail utilizzata sia presente in firebase authenticator
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            //quando viene comppletata l'autentificazione
            //validation complete, aprire una nuova activity e fare il finish() del login se chi accede e un admin

                if(it.isSuccessful) {
                    val user: FirebaseUser? = fAuth.currentUser

                    //una persona puo accedere all'applicazione solamente se e stato registrato come admin, andiamo ad effettuare quel controllo
                    println("DEBUG: verifica(user) restituiscce: ${verificaAdmin(user)}")
                    if (verificaAdmin(user)) {
                        //se l'utente che accede e un admin

                        //per l'utente admin verifichiamo se la mail con la quale accede è gia stata verificata, tramite la ricezione di un email e clickando sul link di conferma
                        //controlliamo se l'email è gia stata verificata confermandola tramite mail, se non e verificata inviamo la mail di verifica
                        if (user!!.isEmailVerified) {

                            Toast.makeText(this@LoginActivity, "Logged In Successfully", Toast.LENGTH_SHORT).show()

                        //redirect to user profile
                        //Creiamo un Intent passandogli il Context ( this@LoginActivity ) e in più passiamo l'informazione per rendere l'intent esplicito, l'activity che deve essere eseguita, dicendo che deve aprire l'activity la cui classe è MainAcivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        //creato l'intent con le informazioni da passare alla SecondActivity, inviamo un richiesta ad ART tramite lo startActivity() passandogli l'intent di tipo esplicito.
                        //l'androidRuntime, sa che oggetto deve lanciare "MainActivity :: class.java" e quindi istanzia un opggetto di tipo MainActivity e chiama su di esso il metodo onCreate() che crea l'interfaccia e inserisce al suo interno il contenuto che abbiamo
                        // inserito nel file activity_main.xml

                        progressBar.visibility = View.GONE
                        }
                        else {
                            //se la mail non e stata ancora verificata, inviamo la mail di verifica
                            user.sendEmailVerification()
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, "Check your email to verify your account", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        //se l'utente che accede non e un admin lo avvisiamo
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "You are logging in without Admin permission", Toast.LENGTH_LONG).show()

                    }
                }else {
                    // se si entra in questo ramo dell'if vuol dire che si ha provato ad accedere ma si ha inserito una mail
                    // non presente nel firebase auth oppure mail corretta ma password errata o entrambi
                    Toast.makeText(this@LoginActivity, "Email or Password is incorrect", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    /**
     * metodo che verifica che l'utente che cerca di accedere all'applicazione amministratore sia salvato
     * nel database come un amministratore
     *
     * legge la mail con cui si ceerca di accedere e verifica il campo relativo al ruolo di quel volontario,
     * restituisce true se l'utente è un amministratore, false se è registrato con un altro ruolo
     *
     * il parametro contiene il riferimento all'utente attualmente loggato, l'utente corrente
     */
    private fun verificaAdmin(user: FirebaseUser?): Boolean {
        StaticBoolean.restoreInitialState()
        //var result = false
        //uid dell'utente che ha effettuato l'accesso
        val userID = user?.uid!!

        //preleviamo i dati da firebase dell' utente che ha effetuato l'accesso, cercandolo tramite l'UID
        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile: Person? = snapshot.getValue(Person::class.java)

                //print(userID)
                //println(userProfile)
                if(userProfile != null){
                    //lettura del ruolo dell'utente che ha effettuato l'accesso
                    val ruole: String = userProfile.ruolo
                    //println("Ruolo letto dall'applicazione: $ruole")
                    //println("ruolo usato a cui si fa riferimento: ${getString(R.string.admin)}")

                    //se il ruolo dell'utente che ha effettuato l'accesso è un amministratore resul = true, altrimenti false
                    //if(ruole == getString(R.string.admin)){
                        //StaticBoolean.setTrue()
                        //println("prima del return result vale: $result")
                    if(ruole == "Admin" || ruole == "Amministratore"){
                        StaticBoolean.setTrue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Something wrong happened!", Toast.LENGTH_LONG).show()

            }
        })

        println("StaticBoolean.currentState= $StaticBoolean.currentState()")
        return StaticBoolean.currentState()
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
