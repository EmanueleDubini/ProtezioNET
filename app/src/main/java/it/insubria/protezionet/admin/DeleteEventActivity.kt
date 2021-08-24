package it.insubria.protezionet.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import it.insubria.protezionet.common.Event
import kotlinx.android.synthetic.main.activity_delete_equipment.*
import kotlinx.android.synthetic.main.activity_delete_person.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.*

class DeleteEventActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference

    //quando viene fatta la lettura da db di tutti i nomi dei volontari presenti nel nodo "person" vengono inseriti tutti dentro qui per tenerli in memoria
    var allEventReadFromDB: ArrayList<Event> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_event)

        //rifermento al nodo person da cui leggere i dati dei volontari presenti nel database
        reference = FirebaseDatabase.getInstance().getReference("event")

        //preleviamo i dati da firebase per mostrarli nel dropDownMenu
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //prima di inserire i nuovi valori, nel caso non sia la prima volta che questo video sia eseguito, pulisco l'arraylist che contiene tutte le persone lette
                allEventReadFromDB.clear()

                for (dSnapshot in snapshot.children) {
                    val event = dSnapshot.getValue(Event::class.java)
                    /*val nome = dSnapshot.child("nome").getValue(String::class.java)//.child("username").getValue<String>(String::class.java)
                    val cognome = dSnapshot.child("cognome").getValue(String::class.java)
                    val ruolo = dSnapshot.child("ruolo").getValue(String::class.java)*/

                    //aggiunta dei possibili vigili del fuoco con cui si puo formare un team, verranno mostrati poi nel dropdown menu
                    if (event != null) {
                        //teamList.add("${person.nome} ${person.cognome} - ${person.ruolo}")
                        allEventReadFromDB.add(event)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DeleteEventActivity,
                    "Something wrong happened!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    //quando viene premuto il botone di resetPassword
    @ExperimentalStdlibApi
    fun deleteEvent(v: View) {
        if (v.id == R.id.deleteEquipmentButton) {
            //salvo quale utente è correntemente loggato
            /*var emailCurrentUser = FirebaseAuth.getInstance().currentUser?.email
            var passwordCurrentUser = getPasswordCurrentUser(emailCurrentUser)*/

            //procedo con l'eliminazione della persona specificata dall'utente
            val eventName: String = eventDelete.text.toString().trim()

            if (eventName.isEmpty()) {
                eventDelete.error = "Name of event to be deleted required"
                eventDelete.requestFocus()
            }
            //verifico se il nome della persona da eliminare e presente nel database, se è presente mi salvo i suoi dati
            // per poterla eliminare
            val eventoDaEliminare: Event? = ricercaEvento(eventName)
            // se personaDaEliminare è null vuol dire che non e stato trovato chi va eliminato
            if (eventoDaEliminare == null) {
                eventDelete.error = "specified event does not exist"
                eventDelete.requestFocus()

            } else {
                Toast.makeText(
                    this@DeleteEventActivity,
                    "evento esiste, ora va eliminato",
                    Toast.LENGTH_LONG
                ).show()

                reference.child(eventoDaEliminare.id).removeValue().addOnCompleteListener(){

                    if (it.isSuccessful) {
                        //se ha avuto successo l'equipaggiamento che si desiderava eliminare è stato tolto
                        eventDelete.setText("")

                        Toast.makeText(
                            this@DeleteEventActivity,
                            "Event deleted sussesfully!",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        //progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DeleteEventActivity,
                            "Try again! Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }
        }
    }



    @ExperimentalStdlibApi
    private fun ricercaEvento(EventDelete: String): Event? {
        for (event in allEventReadFromDB) {
            //persona contiene tutti gli oggetti Person presenti sul db
            //scorro tutti gli elementi di allpersonReadFromDatabase, quindi al primo ciclo la variabile persona contiene il primo elemento Person contenuto nell'arraylist allpersonreadFromDB e cosi via

            if (event.tipo.lowercase(Locale.getDefault()) == EventDelete.lowercase(Locale.getDefault())) {
                return event
            }
        }
        return null
    }
}
