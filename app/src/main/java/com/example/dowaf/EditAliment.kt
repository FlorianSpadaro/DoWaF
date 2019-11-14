package com.example.dowaf

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dowaf.model.Aliment
import com.example.dowaf.model.Category
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_aliment.*
import java.time.LocalDateTime


class EditAliment : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val PERMISSION_CODE = 1000
    val PERMISSION_ID = 42
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var aliment: Aliment? = null
    private val auth = FirebaseAuth.getInstance()
    var fusedLocationClient: FusedLocationProviderClient? = null
    var listOfCategories = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_aliment)

        //TODO ajouter la position d'un aliment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                this
            ) { location: Location? ->
                // Got last known location. In some rare
                // situations this can be null.
                if (location == null) {
                    // TODO, handle it
                } else location.apply {
                    // Handle location object
                    aliment!!.position =
                        location.latitude.toString() + "," + location.longitude.toString()
                }
            }
        }


        //val title = intent.getStringExtra("title")
        aliment = intent.getParcelableExtra("aliment")
        if (aliment == null) {
            titleView.text = "Ajout d'un nouvel aliment"
        } else {
            titleView.text = "Modification de l'aliment: " + aliment!!.name.toString()
            nameAlimentView.setText(aliment!!.name.toString())
            if (aliment!!.description != null) {
                descriptionAlimentView.setText(aliment!!.description.toString())
            }
            if (aliment!!.image != null && aliment!!.image != "" && aliment!!.image != "null") {
                storage.reference.child(aliment!!.image.toString())
                    .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageAlimentView)
                }
            }

        }

        var positionCategorieSelected = -1

        db.collection("categories").get().addOnSuccessListener {
            var i = 0
            it.documents.forEach { categoryDb ->
                var category = Category()
                category.id = categoryDb.id
                category.fromMap(categoryDb.data!!)
                listOfCategories.add(category)
                if (aliment != null && category.id == aliment!!.categoryId) {
                    positionCategorieSelected = i
                }
                i++
            }
            var listCategoriesName = ArrayList<String>()
            listOfCategories.forEach {
                listCategoriesName.add(it.name.toString())
            }
            spinner!!.onItemSelectedListener = this
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCategoriesName)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.adapter = aa
            if (positionCategorieSelected >= 0) {
                spinner.setSelection(positionCategorieSelected)
            }
        }
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
                aliment!!.description = descriptionAlimentView.text.toString()
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
            aliment!!.description = descriptionAlimentView.text.toString()
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
                var aliment = Aliment()
                aliment.image = imagePath
                aliment.name = nameAlimentView.text.toString()
                aliment.description = descriptionAlimentView.text.toString()
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
        } else {
            Toast.makeText(this, "Veuillez mettre une photo de l'aliment", Toast.LENGTH_SHORT)
                .show()
        }

    }


    private fun checkPermission(vararg perm: String): Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if (perm.toList().any {
                    ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                }
            ) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK") { id, v ->
                        ActivityCompat.requestPermissions(
                            this, perm, PERMISSION_ID
                        )
                    }
                    .setNegativeButton("No", { id, v -> })
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
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

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        var category = listOfCategories[position]
        aliment!!.categoryId = category.id
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

}
