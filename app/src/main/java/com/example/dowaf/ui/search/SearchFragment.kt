package com.example.dowaf.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dowaf.R
import com.example.dowaf.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private val db = FirebaseFirestore.getInstance()
    private var categoriesAlimentList = ArrayList<Category>()
    private var adapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO ajouter Barre de recherche

        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        searchViewModel.text.observe(this, Observer {
            textView.text = it
        })

        createCategoriesGridView(root)

        return root
    }

    private fun createCategoriesGridView(root: View) {
        var categories = ArrayList<Category>()

        val categoriesDb = db.collection("categories").get()
        categoriesDb.addOnSuccessListener {
            it.documents.forEach { categoryDb ->
                var category = Category()
                category.id = categoryDb.id
                category.fromMap(categoryDb.data!!)
                categories.add(category)
            }

            adapter = CategoryAdapter(this.context!!, categories)
            root.findViewById<GridView>(R.id.categoriesGridView).adapter = adapter
        }
    }

    class CategoryAdapter : BaseAdapter {
        val storage = FirebaseStorage.getInstance()
        var categoriesList = ArrayList<Category>()
        var context: Context? = null

        constructor(context: Context, categoriesList: ArrayList<Category>) : super() {
            this.context = context
            this.categoriesList = categoriesList
        }

        override fun getCount(): Int {
            return categoriesList.size
        }

        override fun getItem(position: Int): Any {
            return categoriesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val category = this.categoriesList[position]

            var inflator =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var categoryView = inflator.inflate(R.layout.category_entry, null)
            val imageView = categoryView.findViewById<ImageView>(R.id.imgCategory)

            if (category.imageUrl != null && category.imageUrl != "" && category.imageUrl != "null") {
                storage.reference.child(category.imageUrl.toString())
                    .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView)
                }
            }
            //categoryView.findViewById<ImageView>(R.id.imgCategory)
            //    .setImageResource(category.image!!)
            categoryView.findViewById<TextView>(R.id.categoryName).text = category.name!!

            return categoryView
        }
    }
}