package it.insubria.protezionet.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import it.insubria.protezionet.common.Event
import it.insubria.protezionet.common.Truck
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_truck.*
import kotlinx.android.synthetic.main.fragment_truck.truckType

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val eventDBReference = FirebaseDatabase.getInstance().getReference("event")

/**
 * Una sottoclasse di [Fragment].
 * Utilizza il meotodo [TruckFragment.newInstance] per
 * generare un istanza di questo fragment.
 *
 * Questo fragment rappresenta la schermata dell'applicazione Amministratori che permette di registrare
 * un evento
 */
class EventFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null

    //istanza utilizzata per gestire la barra di caricamento
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        val registerButton: Button = view!!.findViewById(R.id.mRegisterButtonFragmentEvent)//view!!.findViewById(R.id.mRegisterButton)
        registerButton.setOnClickListener(this)

        //inizializzazione
        progressBar = view.findViewById(R.id.progressBarFragmentEvent)

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        //viene eseguito quando il bottone progressBarFragmentEvent viene premuto

        val nomeEvento: String = mNameEvent.text.toString().trim().lowercase()
        val citta: String = mCity.text.toString().trim().lowercase()
        val severita: String = mSeverita.text.toString().trim().lowercase()

        if (nomeEvento.isEmpty()) { //todo generare le stringhe
            truckType.error = "Event name is Required"
            truckType.requestFocus()
        }

        else if (citta.isEmpty()) {
            truckPlate.error = "City is Required"
            truckPlate.requestFocus()
        }

        else if (severita.isEmpty()) {
            truckColor.error = "Severity value is Required"
            truckColor.requestFocus()
        }
        else {

            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i dati inseriti dall'utente sono validi e possiamo effettuare la registrazione di un nuovo evento

            //genero il mezzo da salvare nel database
            val uniqueId = eventDBReference.push().key!!
            val evento = Event(uniqueId, nomeEvento, citta, severita)

            //salvo il mezzo nel database
            FirebaseDatabase.getInstance().getReference("event")
                .child(uniqueId).setValue(evento).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Event has been registered sucessfully ", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }else{
                        Toast.makeText(activity, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }

            //svuoto i campi scrivibili
            mNameEvent.setText("")
            mCity.setText("")
            mSeverita.setText("")
        }

    }
}