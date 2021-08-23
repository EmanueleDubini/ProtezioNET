package it.insubria.protezionet.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import it.insubria.protezionet.common.Equipment
import kotlinx.android.synthetic.main.fragment_equipment.*
import java.util.*
import java.util.regex.Pattern

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val equipmentDBReference = FirebaseDatabase.getInstance().getReference("equipment")

/**
 * A simple [Fragment] subclass.
 * Use the [EquipmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentFragment : Fragment(), View.OnClickListener {
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
        val view = inflater.inflate(R.layout.fragment_equipment, container, false)

        val registerButton: Button = view!!.findViewById(R.id.mRegisterButtonFragmentEquipment)//view!!.findViewById(R.id.mRegisterButton)
        registerButton.setOnClickListener(this)

        //inizializzazione
        progressBar = view.findViewById(R.id.progressBarFragmentEquipment)

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
         * @return A new instance of fragment EquipmentFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EquipmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        //viene eseguito quando il bottone mRegisterButtonFragmentEquipment viene premuto

        val tipo: String = equipmentType.text.toString().trim()
        val stato: String = equipmentState.text.toString().trim()
        val data: String = equipmentAcquireData.text.toString().trim()

        if (tipo.isEmpty()) { //todo generare le stringhe
            equipmentType.error = "Truck type is Required"
            equipmentType.requestFocus()
        }

        else if (stato.isEmpty()) {
            equipmentState.error = "Truck plate is Required"
            equipmentState.requestFocus()
        }

        else if (stato.toIntOrNull() !is Int || stato.toInt() > 100 || stato.toInt() < 0) {
            equipmentState.error = "type Equipment state as format from 00 to 100"
            equipmentState.requestFocus()
        }

        else if (data.isEmpty()) {
            equipmentAcquireData.error = "Truck color date is Required"
            equipmentAcquireData.requestFocus()
        }

        else if (!isValidDate(data)) {
            equipmentAcquireData.error = "Date format must be yyyy-mm-dd"
            equipmentAcquireData.requestFocus()//binding.UsernameField.error = "Invalid Email"                    //editTextUsername.setError("Invalid email")
        }
        else {

            //avvio la progress bar
            progressBar.visibility = View.VISIBLE

            //se tutte le condizioni non sono valide, vuole dire che i dati inseriti dall'utente sono validi e possiamo effettuare la registrazione di un nuovo equipaggiamento

            val chiaveValore: List<String> = data.split("-") // spezzo la stringa letta in due

            val date = Date(chiaveValore[0].toInt(), chiaveValore[1].toInt(), chiaveValore[2].toInt())

            //genero il mezzo da salvare nel database
            val uniqueId = equipmentDBReference.push().key!!
            val equipaggiamento = Equipment(uniqueId, tipo, date, stato.toInt())

            //salvo il mezzo nel database
            FirebaseDatabase.getInstance().getReference("equipment")
                .child(uniqueId).setValue(equipaggiamento).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(activity, "Equipment has been registered sucessfully ", Toast.LENGTH_LONG).show()
                        progressBar.visibility = View.GONE
                    }else{
                        Toast.makeText(activity, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }

            //svuoto i campi scrivibili
            equipmentType.setText("")
            equipmentState.setText("")
            equipmentAcquireData.setText("")
        }

    }

    private fun isValidDate(date: String): Boolean {
        val datePattern = ("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])\$")

        val pattern = Pattern.compile(datePattern)
        val matcher = pattern.matcher(date)
        return matcher.matches()  //se la data soddisfa la reg-ex ritornerà un valore true altrimenti false
    }
}