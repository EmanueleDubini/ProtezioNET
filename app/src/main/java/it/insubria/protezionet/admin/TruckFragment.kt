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
import it.insubria.protezionet.common.Truck
import kotlinx.android.synthetic.main.fragment_truck.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Una sottoclasse di [Fragment].
 * Utilizza il meotodo [TruckFragment.newInstance] per
 * generare un istanza di questo fragment.
 *
 * Questo fragment rappresenta la schermata dell'applicazione Amministratori che permette di registrare
 * un mezzo
 */
class TruckFragment : Fragment(), View.OnClickListener {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_truck, container, false)

        val registerButton: Button = view!!.findViewById(R.id.mRegisterButtonFragmentTruck)//view!!.findViewById(R.id.mRegisterButton)
        registerButton.setOnClickListener(this)

        //inizializzazione
        progressBar = view.findViewById(R.id.progressBarFragmentPerson)

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
         * @return A new instance of fragment TruckFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TruckFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        //viene eseguito quando il bottone mRegisterButtonFragmentTruck viene premuto

        val tipo: String = truckType.text.toString().trim()
        val targa: String = truckPlate.text.toString().trim()
        val colore: String = truckColor.text.toString().trim()

        if (tipo.isEmpty()) { //todo generare le stringhe
            truckType.error = "Truck type is Required"
            truckType.requestFocus()
        }

        else if (targa.isEmpty()) {
            truckPlate.error = "Truck plate is Required"
            truckPlate.requestFocus()
        }

        else if (colore.isEmpty()) {
            truckColor.error = "Truck color date is Required"
            truckColor.requestFocus()
        }
        else {

            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i dati inseriti dall'utente sono validi e possiamo effettuare la registrazione di un nuovo mezzo

            //genero il mezzo da salvare nel database
            val mezzo = Truck(tipo, targa, colore)

            //salvo il mezzo nel database
            FirebaseDatabase.getInstance().getReference("trucks")
                .child(targa).setValue(mezzo).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Truck has been registered sucessfully ", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }else{
                        Toast.makeText(activity, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }

            //svuoto i campi scrivibili
            truckType.setText("")
            truckPlate.setText("")
            truckColor.setText("")
        }

    }
}