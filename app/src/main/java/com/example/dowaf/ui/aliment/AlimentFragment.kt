package com.example.dowaf.ui.aliment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dowaf.EditAliment
import com.example.dowaf.HomeActivity
import com.example.dowaf.R
import com.example.dowaf.model.Aliment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AlimentFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var alimentViewModel: AlimentViewModel
    private val alimentsList = ArrayList<Aliment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result = db.collection("aliments").get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        var aliment = Aliment()
                        aliment.id = document.reference.id
                        aliment.fromMap(document.data)
                        alimentsList.add(aliment)
                    }
                    createAlimentsListView(alimentsList)
                } else {
                    println("Erreur")
                }
            })

        val root = inflater.inflate(R.layout.fragment_aliment, container, false)

        val btn: Button = root.findViewById(R.id.addAlimentBtn)
        btn.setOnClickListener { view ->
            //addAlimentDialog()
            addAliment()
        }

        return root
    }

    fun createAlimentsListView(aliments: ArrayList<Aliment>) {
        var alimentsNameList = ArrayList<String>()
        var i = 0
        while (i < aliments.size) {
            alimentsNameList.add(aliments[i].name.toString())

            i++
        }

        val adapter = ArrayAdapter(
            this.context,
            R.layout.alimentlist_item, alimentsNameList
        )


        val listView: ListView = this.view!!.findViewById(R.id.alimentsListView)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->

            modifyAlimentDialog(aliments[position])
        }
    }

    fun modifyAlimentDialog(aliment: Aliment) {
        val builder = AlertDialog.Builder(this.context)
        val inflater = layoutInflater
        builder.setTitle("Modification de l'aliment: " + aliment.name.toString())
        val dialogLayout = inflater.inflate(R.layout.edit_aliment_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            val editText = dialogLayout.findViewById<EditText>(R.id.nameAliment)
            aliment.name = editText.text.toString()
            db.collection("aliments").document(aliment.id.toString()).set(aliment.toMap())
        }
        builder.show()
    }

    fun addAlimentDialog() {
        val builder = AlertDialog.Builder(this.context)
        val inflater = layoutInflater
        builder.setTitle("Nouvel aliment")
        val dialogLayout = inflater.inflate(R.layout.edit_aliment_dialog, null)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            var aliment = Aliment()

            val editText = dialogLayout.findViewById<EditText>(R.id.nameAliment)
            aliment.name = editText.text.toString()
            db.collection("aliments").document().set(aliment.toMap())
        }
        builder.show()
    }

    fun addAliment()
    {
        val title = "Ajout d'un aliment"
        var intent = Intent(this.context, EditAliment::class.java)
        intent.putExtra("title", title)
        startActivity(intent)
    }
}