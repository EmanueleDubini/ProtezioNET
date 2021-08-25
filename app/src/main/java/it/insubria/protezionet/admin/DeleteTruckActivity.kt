package it.insubria.protezionet.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import it.insubria.protezionet.common.Truck
import kotlinx.android.synthetic.main.activity_delete_equipment.*
import kotlinx.android.synthetic.main.activity_delete_person.*
import kotlinx.android.synthetic.main.activity_delete_team.*
import kotlinx.android.synthetic.main.activity_delete_team.teamDelete
import kotlinx.android.synthetic.main.activity_delete_truck.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.*

class DeleteTruckActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference

    //quando viene fatta la lettura da db di tutti i nomi dei volontari presenti nel nodo "person" vengono inseriti tutti dentro qui per tenerli in memoria
    var allTruckReadFromDB: ArrayList<Truck> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_truck)

        //rifermento al nodo person da cui leggere i dati dei volontari presenti nel database
        reference = FirebaseDatabase.getInstance().getReference("truck")

        //preleviamo i dati da firebase per mostrarli nel dropDownMenu
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //prima di inserire i nuovi valori, nel caso non sia la prima volta che questo video sia eseguito, pulisco l'arraylist che contiene tutte le persone lette
                allTruckReadFromDB.clear()

                for (dSnapshot in snapshot.children) {
                    val truck = dSnapshot.getValue(Truck::class.java)
                    /*val nome = dSnapshot.child("nome").getValue(String::class.java)//.child("username").getValue<String>(String::class.java)
                    val cognome = dSnapshot.child("cognome").getValue(String::class.java)
                    val ruolo = dSnapshot.child("ruolo").getValue(String::class.java)*/

                    //aggiunta dei possibili vigili del fuoco con cui si puo formare un truck, verranno mostrati poi nel dropdown menu
                    if (truck != null) {
                        allTruckReadFromDB.add(truck)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DeleteTruckActivity,
                    "Something wrong happened!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    //quando viene premuto il botone di resetPassword
    @ExperimentalStdlibApi
    fun deleteTruck(v: View) {
        if (v.id == R.id.deleteTruckButton) {
            //salvo quale utente è correntemente loggato
            /*var emailCurrentUser = FirebaseAuth.getInstance().currentUser?.email
            var passwordCurrentUser = getPasswordCurrentUser(emailCurrentUser)*/

            //procedo con l'eliminazione della persona specificata dall'utente
            val truckName: String = truckDelete.text.toString()

            if (truckName.isEmpty()) {
                truckDelete.error = "Name of truck to be deleted required"
                truckDelete.requestFocus()
            }
            //verifico se il nome della persona da eliminare e presente nel database, se è presente mi salvo i suoi dati
            // per poterla eliminare
            val truckDaEliminare: Truck? = ricercaTruck(truckName)
            // se personaDaEliminare è null vuol dire che non e stato trovato chi va eliminato
            if (truckDaEliminare == null) {
                truckDelete.error = "specified truck does not exist"
                truckDelete.requestFocus()

            } else {
                reference.child(truckDaEliminare.id).removeValue().addOnCompleteListener(){

                    if (it.isSuccessful) {
                        //se ha avuto successo l'equipaggiamento che si desiderava eliminare è stato tolto
                        truckDelete.setText("")

                        Toast.makeText(
                            this@DeleteTruckActivity,
                            "truck deleted sussesfully!",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        //progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DeleteTruckActivity,
                            "Try again! Something wrong happened",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }
        }
    }



    @ExperimentalStdlibApi
    private fun ricercaTruck(TruckDelete: String): Truck? {
        for (truck in allTruckReadFromDB) {
            //persona contiene tutti gli oggetti Person presenti sul db
            //scorro tutti gli elementi di allpersonReadFromDatabase, quindi al primo ciclo la variabile persona contiene il primo elemento Person contenuto nell'arraylist allpersonreadFromDB e cosi via

            if (truck.tipo.lowercase(Locale.getDefault()) == TruckDelete.lowercase(Locale.getDefault())) {
                return truck
            }
        }
        return null
    }
}
