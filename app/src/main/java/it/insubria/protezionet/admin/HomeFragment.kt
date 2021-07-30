package it.insubria.protezionet.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import it.insubria.protezionet.common.Person
import kotlinx.android.synthetic.main.fragment_home.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null

    //istanza utilizzata per rappresentare un utente di firebase
    private lateinit var user: FirebaseUser
    //istanza utilizzata per ottenere un riferimento al nodo del database da cui leggere
    private lateinit var reference: DatabaseReference

    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //recupero l'id del bottone per registrarsi e gli associo un onClickListener per quando viene premuto
        val signOut: Button = view!!.findViewById(R.id.signOutButton) //view!!.findViewById(R.id.mRegisterButton)
        signOut.setOnClickListener(this)

        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("person") //rifermento al nodo person da cui leggere
        userID = user.uid


        //preleviamo i dati da firebase
        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile: Person? = snapshot.getValue(Person::class.java)

                if(userProfile != null){
                    val name: String = userProfile.nome
                    val surname: String = userProfile.cognome
                    val email: String = userProfile.email
                    val ruole: String = userProfile.ruolo


                    greeting.text = resources.getString(R.string.welcome) + " " + name
                    emailAddress.text = email
                    Name.text = name
                    Surname.text = surname
                    Ruole.text = ruole
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
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        //viene eseguito quando il bottone signOutButton viene premuto
        //logout dell'utente
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }
}


