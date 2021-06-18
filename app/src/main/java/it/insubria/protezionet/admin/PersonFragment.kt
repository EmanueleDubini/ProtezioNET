package it.insubria.protezionet.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.health.UidHealthStats
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import it.insubria.protezionet.Common.Person
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.android.synthetic.main.fragment_person.view.*
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * Una sottoclasse di [Fragment].
 * Utilizza il meotodo [PersonFragment.newInstance] per
 * generare un istanza di questo fragment.
 *
 * Questo fragment rappresenta la schermata dell'applicazione Amministratori che permette di registrare
 * un nuovo volonario oppure registrare un nuovo admin
 */
class PersonFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    //istanza utilizzata per gestire le autentificazioni su firebase
    private lateinit var fAuth: FirebaseAuth
    //istanza utilizzata per gestire la barra di caricamento
    private lateinit var progressBar: ProgressBar  //todo forse questi due campi potrebbero essere messi a null e salvati come variabili



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume(){
        super.onResume()

        //adapter per il dropdown menu
        val ruoli = resources.getStringArray(R.array.ruole)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, ruoli)
        view?.autoCompleteTextView?.setAdapter(arrayAdapter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_person, container, false)

        //recupero l'id del bottone per registrarsi e gli associo un onClickListener per quando viene premuto
        val registerButton: Button = view!!.findViewById(R.id.mRegisterButtonFragmentPerson)//view!!.findViewById(R.id.mRegisterButton)
        registerButton.setOnClickListener(this)

        //inizializzazione
        fAuth = FirebaseAuth.getInstance()
        progressBar = view.findViewById(R.id.progressBarFragmentPerson)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onClick(v: View?) {
        //viene eseguito quando il bottone mRegisterButton viene premuto

        val username: String = mName.text.toString().trim()
        val surname: String = mSurname.text.toString().trim()
        val email: String = mEmailRegister.text.toString().trim()
        val password: String = mPasswordRegister.text.toString().trim()

        if (username.isEmpty()) { //todo generare le stringhe
            mName.error = "Name is Required"
            mName.requestFocus()
        }

        else if (surname.isEmpty()) {
            mSurname.error = "Surname is Required"
            mSurname.requestFocus()
        }

        else if (email.isEmpty()) {
            mEmailRegister.error = "Email is Required"
            mEmailRegister.requestFocus()
        }

        //else if (!isValidEmail(email)) {
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailRegister.error = getString(R.string.invalid_email)
            mEmailRegister.requestFocus()//binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
        }

        else if (password.isEmpty()) {
            mPasswordRegister.error = "Password is Required"
            mPasswordRegister.requestFocus()
        }

        else if (password.length <= 6) {
            mPasswordRegister.error = "Password Must be greater than 6 Characters"
            mPasswordRegister.requestFocus()
        }

        //se quello che e stato inserito è tutto corretto
        else {


            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i deati inseriti dall'utente sono validi e possiamo effettuare la registrazione

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    //l'utente e stato inserito correttamente
                    //Toast.makeText(activity, "User Created", Toast.LENGTH_SHORT).show()

                    //leggo quello che e stato selezionato come ruolo dall'interfaccia utente
                    val ruolo = autoCompleteTextView.text.toString()

                    //genero l'utente da salvare nel database
                    val user = Person(username, surname, email, password, ruolo)
                    //salvo l' id delll'utente corrente che ha effettuato il login nell'app
                    val currentUser : String? = FirebaseAuth.getInstance().currentUser?.uid

                    //salvo l'utente sul database nel nodo "person"
                    if (currentUser != null) {
                        FirebaseDatabase.getInstance().getReference("person")
                            .child(currentUser).setValue(user).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(activity, "User has been registered sucessfully ", Toast.LENGTH_LONG).show()
                                    progressBar.visibility = View.GONE
                                }
                            }
                    }else{
                        Toast.makeText(activity, "Failed to registter! Try again!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }

                    //si apre la main activity
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(activity, "Error! Problem during creating user", Toast.LENGTH_SHORT)
                        .show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

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
}