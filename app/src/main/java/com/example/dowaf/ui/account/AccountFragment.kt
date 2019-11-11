package com.example.dowaf.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dowaf.R
import com.example.dowaf.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var user = User()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO pouvoir modifier les infos de l'utilisateur
        //TODO photo de profil de l'utilisateur

        accountViewModel =
            ViewModelProviders.of(this).get(AccountViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        accountViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val result = db.collection("users").document(auth.currentUser!!.uid).get()
        result.addOnSuccessListener {
            user.fromMap(it.data!!)
            nameUserView.text = user.name.toString()
            firstNameUserView.text = user.firstName.toString()
            mailUserView.text = user.mail.toString()
            phoneUserView.text = user.phone.toString()
        }

        root.findViewById<Button>(R.id.signOutBtn).setOnClickListener {
            auth.signOut()
            this.activity!!.finish()
        }

        return root
    }

}