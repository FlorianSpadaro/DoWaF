package com.example.dowaf.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dowaf.R
import com.example.dowaf.RecyclerAdapter
import com.example.dowaf.model.Aliment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderFragment : Fragment() {

    companion object {
        fun newInstance() = OrderFragment()
    }

    private lateinit var viewModel: OrderViewModel
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.order_fragment, container, false)
        createAlimentsListView(root)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }

    private fun createAlimentsListView(view: View) {
        layoutManager = LinearLayoutManager(this.context)

        val query = db.collection("aliments")
            .whereEqualTo(
                "bookerUid",
                auth.currentUser!!.uid
            )
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
