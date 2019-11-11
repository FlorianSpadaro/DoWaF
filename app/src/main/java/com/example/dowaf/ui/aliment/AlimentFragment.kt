package com.example.dowaf.ui.aliment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dowaf.EditAliment
import com.example.dowaf.R
import com.example.dowaf.RecyclerAdapter
import com.example.dowaf.model.Aliment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class AlimentFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var alimentViewModel: AlimentViewModel
    private val alimentsList = ArrayList<Aliment>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*db.collection("aliments")
        val result = db.collection("aliments").get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        var aliment = Aliment()
                        aliment.id = document.reference.id
                        aliment.fromMap(document.data)
                        alimentsList.add(aliment)
                    }
                    //createAlimentsListView(alimentsList)
                    createAlimentsListView()
                } else {
                    println("Erreur")
                }
            })*/
        val root = inflater.inflate(R.layout.fragment_aliment, container, false)

        createAlimentsListView(root)

        val btn: Button = root.findViewById(R.id.addAlimentBtn)
        btn.setOnClickListener { view ->
            //addAlimentDialog()
            addAliment()
        }

        return root
    }

    fun createAlimentsListView(view: View) {
        layoutManager = LinearLayoutManager(this.context)

        val query = db.collection("aliments")//.orderBy("productName", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Aliment>().setQuery(query, Aliment::class.java).build()

        view.findViewById<RecyclerView>(R.id.recycler_view).layoutManager = layoutManager

        adapter = RecyclerAdapter(options)
        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    /*fun createAlimentsListView(aliments: ArrayList<Aliment>) {
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
    }*/

    /*fun modifyAlimentDialog(aliment: Aliment) {
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
    }*/


    fun addAliment() {
        var intent = Intent(this.context, EditAliment::class.java)
        startActivity(intent)
    }
}