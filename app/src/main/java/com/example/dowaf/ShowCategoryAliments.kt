package com.example.dowaf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dowaf.model.Aliment
import com.example.dowaf.model.Category
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_show_category_aliments.*

class ShowCategoryAliments : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerAdapter? = null
    private var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_category_aliments)

        category = intent.getParcelableExtra("category")
        titleCategoryView.text = category!!.name.toString()

        createAlimentsListView()
    }

    private fun createAlimentsListView() {
        layoutManager = LinearLayoutManager(this)

        val query = db.collection("aliments").whereEqualTo(
            "categoryId",
            category!!.id
        )//.orderBy("productName", Query.Direction.ASCENDING)
        val options =
            FirestoreRecyclerOptions.Builder<Aliment>().setQuery(query, Aliment::class.java).build()

        this.findViewById<RecyclerView>(R.id.recycler_view).layoutManager = layoutManager

        adapter = RecyclerAdapter(options)
        this.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
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
