package com.example.dowaf

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dowaf.model.Aliment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_aliment.*
import java.time.LocalDateTime


class EditAliment : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    private var locationManager: LocationManager? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var aliment: Aliment? = null
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_aliment)

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?*/


        //val title = intent.getStringExtra("title")
        aliment = intent.getParcelableExtra("aliment")
        if (aliment == null) {
            titleView.text = "Ajout d'un nouvel aliment"
        } else {
            titleView.text = "Modification de l'aliment: " + aliment!!.name.toString()
            nameAlimentView.setText(aliment!!.name.toString())

            if (aliment!!.image != null && aliment!!.image != "" && aliment!!.image != "null") {
                storage.reference.child(aliment!!.image.toString())
                    .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageAlimentView)
                }
            }

        }
        //titleView.text = title
    }

    fun onClickCancelBtn(view: View) {
        finish()
    }

    fun onClickValidateBtn(view: View) {
        if (aliment == null) {
            createNewAliment()
        } else {
            modifyAliment()
        }
    }

    private fun modifyAliment() {
        if (image_uri != null) {
            val currentTime = LocalDateTime.now().toString()
            val currentUserUid = auth.currentUser!!.uid

            val imagePath = "images/${currentUserUid}/${currentTime}"

            val storageRef = storage.reference

            val riversRef = storageRef.child(imagePath)
            val uploadTask = riversRef.putFile(image_uri!!)

            uploadTask.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Une erreur est survenue lors de l'upload de l'image",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                aliment!!.image = imagePath
                aliment!!.name = nameAlimentView.text.toString()
                val result = db.collection("aliments").document(aliment!!.id.toString())
                    .set(aliment!!.toMap())
                result.addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "L'aliment a été modifié avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        } else {
            aliment!!.name = nameAlimentView.text.toString()
            val result = db.collection("aliments").document(aliment!!.id.toString())
                .set(aliment!!.toMap())
            result.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "L'aliment a été modifié avec succès",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun createNewAliment() {
        val currentTime = LocalDateTime.now().toString()
        val currentUserUid = auth.currentUser!!.uid

        val imagePath = "images/${currentUserUid}/${currentTime}"

        val storageRef = storage.reference

        val riversRef = storageRef.child(imagePath)
        val uploadTask = riversRef.putFile(image_uri!!)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this,
                "Une erreur est survenue lors de l'upload de l'image",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            var aliment = Aliment()
            aliment.image = imagePath
            aliment.name = nameAlimentView.text.toString()
            aliment.ownerUid = FirebaseAuth.getInstance().currentUser!!.uid
            val result = db.collection("aliments").document().set(aliment.toMap())
            result.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "L'aliment a été créé avec succès",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    fun showlocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )

    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            var message = location.longitude.toString() + ":" + location.latitude.toString()
            //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            // il faut retourner ou jour avec la variable message
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun onClickPictureBtn(view: View) {
        //if system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }
        } else {
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            imageAlimentView.setImageURI(image_uri)
        }
    }

}
