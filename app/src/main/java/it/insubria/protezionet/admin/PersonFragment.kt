package it.insubria.protezionet.admin

import android.os.Bundle
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
import it.insubria.protezionet.common.Person
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.android.synthetic.main.fragment_person.view.*


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
    private var param1: String? = null
    private var param2: String? = null



    //istanza utilizzata per gestire le autentificazioni su firebase
    private lateinit var fAuth: FirebaseAuth
    //istanza utilizzata per gestire la barra di caricamento
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume(){
        super.onResume()

        //adapter per il dropdown menu ruoli
        val ruoli = resources.getStringArray(R.array.roleList)
        val arrayAdapterRuoli = ArrayAdapter(requireContext(), R.layout.dropdown_item_role, ruoli)
        view?.autoCompleteTextViewRole?.setAdapter(arrayAdapterRuoli)

        //adapter per il dropdown menu team
        /*val teams = resources.getStringArray(R.array.teamList)
        val arrayAdapterTeams = ArrayAdapter(requireContext(), R.layout.dropdown_item_team, teams)
        view?.autoCompleteTextViewTeam?.setAdapter(arrayAdapterTeams)*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_person, container, false)

        //recupero l'id del bottone per registrarsi e gli associo un onClickListener per quando viene premuto
        val registerButton: Button = view!!.findViewById(R.id.mRegisterButtonFragmentPerson)//view!!.findViewById(R.id.mRegisterButton)
        registerButton.setOnClickListener(this)

        //inizializzazione dei vari riferimenti
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

        val username: String = personName.text.toString().trim()
        val surname: String = personSurname.text.toString().trim()
        val email: String = personEmail.text.toString().trim()
        val password: String = personPassword.text.toString().trim()

        if (username.isEmpty()) {
            personName.error = getString(R.string.name_is_required)
            personName.requestFocus()
        }

        else if (surname.isEmpty()) {
            personSurname.error = getString(R.string.surname_is_required)
            personSurname.requestFocus()
        }

        else if (email.isEmpty()) {
            personEmail.error = getString(R.string.email_is_required)
            personEmail.requestFocus()
        }

        //else if (!isValidEmail(email)) {
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            personEmail.error = getString(R.string.invalid_email)
            personEmail.requestFocus()//binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
        }

        else if (password.isEmpty()) {
            personPassword.error = getString(R.string.pasword_is_required)
            personPassword.requestFocus()
        }

        else if (password.length <= 6) {
            personPassword.error = getString(R.string.password_not_aceptable)
            personPassword.requestFocus()
        }

        /*else if (false) {
        // aggiungere il controllo che venga selezionato un team tra quelli disponibili, i team sono stati tolti da questa finestra
        }*/

        //se quello che e stato inserito è tutto corretto
        else {


            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i deati inseriti dall'utente sono validi e possiamo effettuare la registrazione

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                //todo NOTA: quando viene creato un nuovo utente il currentUser diventa il nuovo utente inserito nel db
                // ogni volta che aggiungo un nuovo account utente, espelle l'utente corrente che ha già effettuato l'accesso.
                // Ho letto l'API di Firebase e dice che " Se il nuovo account è stato creato, l'utente accede automaticamente "
                // quindi nel homeFragment.kt non si puo lasciare il nome e la mail dell'itente che attualmente ha effettuato
                // l'accesso altrimenti ogni volya che si inserisce un nuovo utente vengono mostrati nomi diversi
                // https://stackoverflow.com/questions/37517208/firebase-kicks-out-current-user/37614090#37614090

                if (task.isSuccessful) {
                    //l'utente e stato inserito correttamente

                    //leggo quello che e stato selezionato come ruolo dall'interfaccia utente
                    val ruolo = autoCompleteTextViewRole.text.toString()

                    //leggo il team, quello che e stato selezionato come ruolo dall'interfaccia utente
                    //val team = autoCompleteTextViewTeam.text.toString()

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
                        Toast.makeText(activity, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }

                    //una volta iscritta una nuova persona si rimane sul PersonFragment e vengono resettati i campi della finestra
                    //pronti per un possibile altro inserimento
                    /*val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)*/
                    resetCampiInserimento()

                } else {
                    Toast.makeText(activity, getString(R.string.Error_just_registered), Toast.LENGTH_SHORT)
                        .show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun resetCampiInserimento() {
        personName.text.clear()
        personSurname.text.clear()
        personEmail.text.clear()
        personPassword.text.clear()
        //todo se si desidera resettare il contenuto del dropdownmenu che permette di selzionare il ruolo di una persona
        // bisogna anche inserire il controllo che quando si leggono i vari valori, non sia selezionata la casella vuota
    }

    // metodo non utilizzato perchè sostituito con i pattern di android
    /*private fun isValidEmail(email: String): Boolean {
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
    }*/
}