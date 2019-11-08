package com.example.dowaf.ui.aliment

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dowaf.model.Aliment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AlimentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _text = MutableLiveData<String>().apply {
        value = "This is aliment Fragment"
    }
    val text: LiveData<String> = _text

    var toast = ""

    fun getAliments() : Task<QuerySnapshot>
    {
        //val alimentsList = ArrayList<Aliment>()
        val result = db.collection("aliments").get()
            /*.addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        var aliment = Aliment()
                        aliment.fromMap(document.data)
                        alimentsList.add(aliment)
                    }
                    println("DANS VIEWMODEL: " + alimentsList.size.toString())
                } else {
                    toast = "Une erreur est survenue"
                }
            })*/

        return result
    }
}