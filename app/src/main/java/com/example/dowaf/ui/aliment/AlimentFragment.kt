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
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_aliment, container, false)

        createAlimentsListView(root)

        val btn: Button = root.findViewById(R.id.addAlimentBtn)
        btn.setOnClickListener { view ->
            var intent = Intent(this.context, EditAliment::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun createAlimentsListView(view: View) {
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

}