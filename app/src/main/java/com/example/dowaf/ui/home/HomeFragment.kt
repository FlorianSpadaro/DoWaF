package com.example.dowaf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dowaf.R
import com.example.dowaf.RecyclerAdapter
import com.example.dowaf.model.Aliment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        createAlimentsListView(root)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
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