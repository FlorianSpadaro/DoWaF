package com.example.dowaf

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.dowaf.model.Aliment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class RecyclerAdapter(options: FirestoreRecyclerOptions<Aliment>) :
    FirestoreRecyclerAdapter<Aliment, RecyclerAdapter.ViewHolder>(options) {
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_layout, viewGroup, false)
        v.setOnClickListener { }
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, item: Aliment) {
        //val id = snapshots.getSnapshot(position).id

        holder.apply {
            if (item.image != null && item.image != "" && item.image != "null") {
                storage.reference.child(item.image.toString()).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(itemImage)
                }
            }

            itemTitle.text = item.name
            if (item.description != null) {
                itemDetail.text = item.description
            }
            //itemDetail.text = id
            itemImage.setImageResource(R.drawable.ic_home_black_24dp)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            //TODO Pouvoir Supprimer les aliments (LongClick / Swipe / Button)

            itemView.setOnLongClickListener {
                val aliment = getItem(adapterPosition)
                aliment.id = snapshots.getSnapshot(adapterPosition).id

                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Suppression de l'aliment: " + aliment.name.toString())
                builder.setMessage("Voulez-vous vraiment supprimer cet aliment?")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Confirmer") { dialog, which ->
                    db.collection("aliments").document(aliment.id.toString()).delete()
                }

                builder.setNegativeButton("Annuler") { dialog, which ->

                }

                builder.show()

                true
            }

            itemView.setOnClickListener {
                val aliment = getItem(adapterPosition)
                aliment.id = snapshots.getSnapshot(adapterPosition).id

                var text = ""
                if (aliment.ownerUid == auth.currentUser!!.uid) {
                    var intent = Intent(it.context, EditAliment::class.java)
                    intent.putExtra("aliment", aliment)
                    it.context.startActivity(intent)
                } else {
                    var intent = Intent(it.context, ShowAlimentActivity::class.java)
                    intent.putExtra("aliment", aliment)
                    it.context.startActivity(intent)
                }
                Toast.makeText(
                    itemView.context,
                    text,
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}