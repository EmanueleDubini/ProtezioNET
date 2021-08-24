package it.insubria.protezionet.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.insubria.protezionet.common.Person
import kotlinx.android.synthetic.main.activity_delete_person.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.*

class DeletePersonActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference

    //quando viene fatta la lettura da db di tutti i nomi dei volontari presenti nel nodo "person" vengono inseriti tutti dentro qui per tenerli in memoria
    var allpersonReadFromDB: ArrayList<Person> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_person)

        //rifermento al nodo person da cui leggere i dati dei volontari presenti nel database
        reference = FirebaseDatabase.getInstance().getReference("person")

        //preleviamo i dati da firebase per mostrarli nel dropDownMenu
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //prima di inserire i nuovi valori, nel caso non sia la prima volta che questo video sia eseguito, pulisco l'arraylist che contiene tutte le persone lette
                allpersonReadFromDB.clear()

                for (dSnapshot in snapshot.children) {
                    val person = dSnapshot.getValue(Person::class.java)
                    /*val nome = dSnapshot.child("nome").getValue(String::class.java)//.child("username").getValue<String>(String::class.java)
                    val cognome = dSnapshot.child("cognome").getValue(String::class.java)
                    val ruolo = dSnapshot.child("ruolo").getValue(String::class.java)*/

                    //aggiunta dei possibili vigili del fuoco con cui si puo formare un team, verranno mostrati poi nel dropdown menu
                    if (person != null) {
                        //teamList.add("${person.nome} ${person.cognome} - ${person.ruolo}")
                        allpersonReadFromDB.add(person)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DeletePersonActivity,
                    "Something wrong happened!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    //quando viene premuto il botone di resetPassword
    @ExperimentalStdlibApi
    fun deletePerson(v: View) {
        if (v.id == R.id.deletePersonButton) {
            //salvo quale utente è correntemente loggato
            val emailCurrentUser = FirebaseAuth.getInstance().currentUser?.email
            val passwordCurrentUser = getPasswordCurrentUser(emailCurrentUser)

            //procedo con l'eliminazione della persona specificata dall'utente
            val personName: String = personNameDelete.text.toString().trim()

            if (personName.isEmpty()) {
                personNameDelete.error = getString(R.string.person_name_required)
                personNameDelete.requestFocus()
            }
            //verifico se il nome della persona da eliminare e presente nel database, se è presente mi salvo i suoi dati
            // per poterla eliminare
            val personaDaEliminare: Person? = ricercaPersona(personName)
            // se personaDaEliminare è null vuol dire che non e stato trovato chi va eliminato
            if (personaDaEliminare == null) {
                personNameDelete.error =
                    getString(R.string.specified_person_does_not_exist)      //binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
                personNameDelete.requestFocus()
            } else {

                reference.child(personaDaEliminare.id).removeValue()
                //firebase authenticator non permette al current user di eliminare altri utenti per ragioni di sicurezza
                //si potrebbe fare solamente utilizzando il databse in modo da avere account amministratori o account normali.
                // quindi per eliminare una persona dalla sezione firebase authenticator bisogna accedere con il profilo di quella persona e eliminare il proprio account
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    personaDaEliminare.email,
                    personaDaEliminare.password
                )

                FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener {

                    if (it.isSuccessful) {
                        //se ha avuo successo vuol dire che l'email e stata mandata
                        personNameDelete.setText("")

                        Toast.makeText(
                            this@DeletePersonActivity,
                            "User deleted sussesfully!",
                            Toast.LENGTH_LONG
                        ).show()

                        if (emailCurrentUser != null && passwordCurrentUser != null) {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailCurrentUser, passwordCurrentUser)
                        }


                        /*val intent = Intent(this@DeletePersonActivity, MainActivity::class.java)
                        startActivity(intent)*/

                        //progressBar.visibility = View.GONE

                    } else {
                        //progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DeletePersonActivity,
                            "Try again! Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun getPasswordCurrentUser(emailCurrentUser: String?): String? {
        for (persona in allpersonReadFromDB) {
            //persona contiene tutti gli oggetti Person presenti sul db
            //scorro tutti gli elementi di allpersonReadFromDatabase, quindi al primo ciclo la variabile persona contiene il primo elemento Person contenuto nell'arraylist allpersonreadFromDB e cosi via

            if (emailCurrentUser != null) {
                if (persona.email.lowercase(Locale.getDefault()) == emailCurrentUser.lowercase(
                        Locale.getDefault()
                    )
                ) {
                    return persona.password
                }
            }
        }
        return null
    }


    @ExperimentalStdlibApi
    private fun ricercaPersona(personNameDelete: String): Person? {
        for (persona in allpersonReadFromDB) {
            //persona contiene tutti gli oggetti Person presenti sul db
            //scorro tutti gli elementi di allpersonReadFromDatabase, quindi al primo ciclo la variabile persona contiene il primo elemento Person contenuto nell'arraylist allpersonreadFromDB e cosi via

            if (persona.nome.lowercase(Locale.getDefault()) == personNameDelete.lowercase(Locale.getDefault())) {
                return persona
            }
        }
        return null
    }
}
