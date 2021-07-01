package it.insubria.protezionet.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import it.insubria.protezionet.common.Person
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TeamFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var reference: DatabaseReference

    lateinit var layoutList: LinearLayout
    lateinit var buttonAdd: Button
    lateinit var buttonSubmitList: Button

    var teamList: ArrayList<String> = ArrayList() //lista dei dei volontari letta da database (Person) che vengono mostrati nelle possibili scelte del dropdownMenu

    var allpersonReadFromDB : ArrayList<Person> = ArrayList() //quando viene fatta la lettura da db di tutti i volontari presenti in person vengono inseriti tutti dentro qui per tenerli in memoria

    var teamMemberList: ArrayList<Person> = ArrayList<Person>() //lista delle persone che poi andranno a formare un team

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team, container, false)

        layoutList = view.findViewById(R.id.layout_list) //linearLayout che contiene le righe che si aggiungono per inserire un nuovo componente del team

        buttonAdd = view.findViewById(R.id.button_add) //bottone che aggiunge una riga per inserire un membro del team

        buttonSubmitList = view.findViewById(R.id.mRegisterButtonTeam) //bottone che invia il team al database

        buttonAdd.setOnClickListener(this)
        buttonSubmitList.setOnClickListener(this)

        reference = FirebaseDatabase.getInstance().getReference("person") //rifermento al nodo person da cui leggere

        teamList.add(" ") //aggiungo una stringa vuota nell'arrayList usato per popolare le scelte del dropdownMenu, per lasciare uno spazio vuoto all'inizio

        //preleviamo i dati da firebase per mostrarli nel dropDownMenu
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                allpersonReadFromDB.clear() //prima di inserire i nuovi valori, nel caso non sia la prima volta che questo video sia eseguito, pulisco l'arraylist che contiene tutte le persone lette
                for (dSnapshot in snapshot.children) {
                    val person = dSnapshot.getValue(Person::class.java)
                    /*val nome = dSnapshot.child("nome").getValue(String::class.java)//.child("username").getValue<String>(String::class.java)
                    val cognome = dSnapshot.child("cognome").getValue(String::class.java)
                    val ruolo = dSnapshot.child("ruolo").getValue(String::class.java)*/

                    //aggiunta dei possibili vigili del fuoco con cui si puo formare un team, verranno mostrati poi nel dropdown menu
                    if (person != null) {
                        teamList.add("${person.nome} ${person.cognome} - ${person.ruolo}")
                        allpersonReadFromDB.add(person)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Something wrong happened!", Toast.LENGTH_LONG).show()

            }
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TeamFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TeamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button_add -> addView()
            R.id.mRegisterButtonTeam -> if (checkIfValidAndRead()) {

                //todo dovrei leggere tutto e inviare al db
                //apro l'activity che mostra le persone inserite
                /*val intent = Intent(activity, ActivityCricketers::class.java)
                val bundle = Bundle() //passo le prsone tramite l'intent
                bundle.putSerializable("list", team_member_list)
                intent.putExtras(bundle)
                startActivity(intent)*///////////////////////////////
            }
        }


    }

    private fun checkIfValidAndRead(): Boolean {
        teamMemberList.clear()
        var result = true
        var valoreLetto: String
        for (i in 0 until layoutList.childCount) {
            val teamMemberView = layoutList.getChildAt(i)

            //val editTextName = teamMemberView.findViewById<View>(R.id.edit_team_member_name) as EditText //editText ceh contiene il nome della persona
            //val editTextSurname = teamMemberView.findViewById<View>(R.id.edit_team_member_surname) as EditText //editText ceh contiene il cognome della persona

            //nello spinner sono mostrati tutti i volontari presenti nel db, 'nome cognome - ruolo'
            val spinnerTeamMember = teamMemberView.findViewById<View>(R.id.spinner_team) as AppCompatSpinner //dropDown menu che fa scegliere il componenten del team


            /////////questa parte non serve perchè noi selezioniamo tramite il dropdown menu
            /*if (editTextName.text.toString() != "") {
                cricketer.setCricketerName(editTextName.text.toString()) //scrive il nome della persona nella sua istanza
            } else {
                result = false
                break
            }*/
            //lettura della persona selezionata dallo spinner
            if (spinnerTeamMember.selectedItemPosition != 0) {
                //person.setTeamName(teamList[spinnerTeam.selectedItemPosition]) //scrive il nome del team nell'istanza della persona
                valoreLetto = teamList[spinnerTeamMember.selectedItemPosition]

                val valoriLettura = valoreLetto.split(" ")
                val nome = valoriLettura[0]
                val cognome = valoriLettura[1] //la posizione due viene saltata perche contiene '-' ---> nome cognome - ruolo
                val ruolo = valoriLettura[3]

                for (persona in allpersonReadFromDB) {//persona contiene tutti gli oggetti Person presenti sul db

                    if (persona.nome == nome && persona.cognome == cognome && persona.ruolo == ruolo){
                        teamMemberList.add(persona) //aggiunge la persona alla lista delle persone inserite nella finestra
                    }
                }
            } else {
                result = false
                break
            }
            //lista che contiene tutte le persone che compongono la squadra
        }
        if (teamMemberList.size == 0) { //in caso vengano compilati in modo errato i campi
            result = false
            Toast.makeText(activity, "Add Team Member First!", Toast.LENGTH_SHORT).show()
        } else if (!result) {
            Toast.makeText(activity, "Enter All Details Correctly!", Toast.LENGTH_SHORT).show()
        }
        return result
    }

    private fun addView() { //metodo che aggiunge una riga per inserire una nuova persona al team

        val teamMemberView: View = layoutInflater.inflate(R.layout.row_add_team_member, null, false) //il layout dell'intera riga per aggiungere persone

        //todo teoricamente usiamo solo lo spinner//val editText = cricketerView.findViewById<View>(R.id.edit_team_member_name) as EditText
        val spinnerTeam = teamMemberView.findViewById<View>(R.id.spinner_team) as AppCompatSpinner

        val imageClose = teamMemberView.findViewById<View>(R.id.image_remove) as ImageView //tasto per rimuovere la riga


        spinnerTeam.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, teamList) //adapter per inserire le possibili scelte nel dropDownMenu

        imageClose.setOnClickListener { removeView(teamMemberView) } //rimuove la riga se premuto

        layoutList.addView(teamMemberView)
    }

    private fun removeView(view: View) {
        layoutList.removeView(view)
    }

}