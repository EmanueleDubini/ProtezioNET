package it.insubria.protezionet.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import it.insubria.protezionet.common.Person
import kotlinx.android.synthetic.main.activity_delete_person.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.*

class DeletePersonActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference

    //quando viene fatta la lettura da db di tutti i nomi dei volontari presenti nel nodo "person" vengono inseriti tutti dentro qui per tenerli in memoria
    var allpersonReadFromDB : ArrayList<Person> = ArrayList()

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
                Toast.makeText(this@DeletePersonActivity, "Something wrong happened!", Toast.LENGTH_LONG).show()

            }
        })
    }

    //quando viene premuto il botone di resetPassword
    fun deletePerson(v: View){
        if(v.id == R.id.deletePersonButton){
            //procedo con l'eliminazione della persona specificata dall'utente
            val personName: String = personNameDelete.text.toString().trim()

            if(personName.isEmpty()){
                personNameDelete.error = getString(R.string.person_name_required)
                personNameDelete.requestFocus()
            }
            //verifico se il nome della persona da eliminare e presente nel database
            else if (!ricercaPersona(personName)) {
                personNameDelete.error = getString(R.string.specified_person_does_not_eist)      //binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
                personNameDelete.requestFocus()
            }else{
                Toast.makeText(this@DeletePersonActivity, "la persona esiste, ora va eliminata", Toast.LENGTH_LONG).show()
                //todo implementare la parte in cui si elimina il volontario dal database
            }



        }
        /*val email: String = emailAddress.text.toString().trim()

        if(email.isEmpty()){
            emailAddress.error = getString(R.string.email_is_required)
            emailAddress.requestFocus()
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.error = getString(R.string.invalid_email)       //binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
            emailAddress.requestFocus()
        }
        //nel caso in cui la mail inserita sia valida
        else{
            progressBar.visibility = View.VISIBLE

            fAuth.sendPasswordResetEmail(email).addOnCompleteListener {

                if(it.isSuccessful){
                    //se ha avuo successo vuol dire che l'email e stata mandata
                    emailAddress.setText("")

                    val intent = Intent(this@ForgotPassword, LoginActivity :: class.java)
                    startActivity(intent)

                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ForgotPassword, "Check your email o reset your password!", Toast.LENGTH_LONG).show()
                }else{
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ForgotPassword, "Try again! Something wrong happened", Toast.LENGTH_LONG).show()
                }
            }
        }*/
    }

    private fun ricercaPersona(personNameDelete: String): Boolean {
        for(persona in allpersonReadFromDB){
            //persona contiene tutti gli oggetti Person presenti sul db
            //scorro tutti gli elementi di allpersonReadFromDatabase, quindi al primo ciclo la variabile persona contiene il primo elemento Person contenuto nell'arraylist allpersonreadFromDB e cosi via

            if(persona.nome.lowercase(Locale.getDefault()) == personNameDelete.lowercase(Locale.getDefault())){
                return true
            }
        }
        return false
    }
}