package com.example.dowaf

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dowaf.model.Aliment
import com.example.dowaf.model.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_aliment.*
import kotlinx.android.synthetic.main.contact_owner_layout.view.*


//class ShowAlimentActivity : AppCompatActivity(), OnMapReadyCallback {
class ShowAlimentActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var aliment: Aliment? = null
    private var owner = User()

    /*private var mapView: MapView? = null
    private var gmap: GoogleMap? = null

    private val MAP_VIEW_BUNDLE_KEY = "AIzaSyAJdsDjZdebiZf71fyFb5AZImQHWvjkc3Q"*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_aliment)

        //TODO Montrer l'emplacement de l'aliment
        /*var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView = findViewById(R.id.positionMapView)
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)*/

        aliment = intent.getParcelableExtra("aliment")

        db.collection("users").document(aliment!!.ownerUid.toString()).get().addOnSuccessListener {
            owner.fromMap(it.data!!)
            owner.id = it.reference.id
            if (aliment!!.bookerUid == auth.currentUser!!.uid) {
                this.findViewById<Button>(R.id.bookingAlimentBtn).text = "Annuler la réservation"
            }
        }

        this.findViewById<TextView>(R.id.nameAlimentView).text = aliment!!.name.toString()
        if (aliment!!.description != null) {
            this.findViewById<TextView>(R.id.descriptionAlimentView).text =
                aliment!!.description.toString()
        }
        if (aliment!!.image != null && aliment!!.image != "" && aliment!!.image != "null") {
            storage.reference.child(aliment!!.image.toString())
                .downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imageAlimentView)
            }
        }

        cancelBtn.setOnClickListener { finish() }
    }


    fun onClickContactOwnerBtn(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Contact du propriétaire")
        val dialogLayout = inflater.inflate(R.layout.contact_owner_layout, null)
        dialogLayout.nameOwnerView.text = owner.name.toString()
        dialogLayout.mailOwnerView.text = owner.mail.toString()
        dialogLayout.phoneOwnerView.text = owner.phone.toString()
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            //TODO Gérer l'appel au propriétaire / Envoi de mail
        }
        builder.show()
    }

    fun onClickBookingBtn(view: View) {
        var successMessage = "Votre réservation a été effectuée"
        if (aliment!!.bookerUid == auth.currentUser!!.uid) {
            aliment!!.bookerUid = null
            successMessage = "Votre réservation a été annulée"
        } else {
            aliment!!.bookerUid = auth.currentUser!!.uid
        }
        val resultat =
            db.collection("aliments").document(aliment!!.id.toString()).set(aliment!!.toMap())
        resultat.addOnFailureListener {
            Toast.makeText(
                this,
                "Une erreur s'est produite, veuillez réessayer plus tard",
                Toast.LENGTH_SHORT
            ).show()
        }
        resultat.addOnSuccessListener {
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        gmap!!.setMinZoomPreference(12f)
        val ny = LatLng(40.7143528, -74.0059731)
        gmap!!.moveCamera(CameraUpdateFactory.newLatLng(ny))
    }*/
}
