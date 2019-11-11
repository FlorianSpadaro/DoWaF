package com.example.dowaf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.passwordTxt
import kotlinx.android.synthetic.main.activity_registration.*
import com.example.dowaf.model.User as UserApp

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    fun onClickConnectionBtn(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickRegistrationBtn(view: View) {
        val name = nameRegistrationTxt.text.toString()
        val firstName = firstNameRegistrationTxt.text.toString()
        val phone = phoneTxt.text.toString()
        val mail = mailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val confirmationPassword = confirmPasswordTxt.text.toString()

        if (checkFirstName(firstName)) {
            if (checkName(name)) {
                if (checkPhone(phone)) {
                    if (checkMail(mail)) {
                        if (checkPasswordLength(password)) {
                            if (checkPassword(
                                    password,
                                    confirmationPassword
                                )
                            ) {
                                registration(name, firstName, phone, mail, password)
                            } else {
                                toastMessage("Les mots de passe ne sont pas identiques")
                                confirmPasswordTxt.error = "Le mot de passe ne correspond pas"
                            }
                        } else {
                            toastMessage("Le mot de passe doit avoir au moins 7 caractères")
                            passwordTxt.error = "Le mot de passe doit avoir au moins 7 caractères"
                        }
                    } else {
                        toastMessage("Adresse mail invalide")
                        mailTxt.error = "Adresse mail invalide"
                    }
                } else {
                    toastMessage("Numéro de téléphone incorrect")
                    phoneTxt.error = "Numéro de téléphone incorrect"
                }
            } else {
                toastMessage("Le nom n'est pas correct")
                nameRegistrationTxt.error = "Le nom n'est pas correct"
            }
        } else {
            toastMessage("Le prénom n'est pas correct")
            firstNameRegistrationTxt.error = "Le prénom n'est pas correct"
        }
    }

    fun checkName(name: String): Boolean {
        if (name.trim().length > 0) {
            return true
        }
        return false
    }

    fun checkFirstName(firstName: String): Boolean {
        if (firstName.trim().length > 0) {
            return true
        }
        return false
    }

    fun checkPasswordLength(password: String): Boolean {
        if (password.length > 6) {
            return true
        }
        return false
    }

    fun checkPassword(password: String, confirm: String): Boolean {
        if (password == confirm) {
            return true
        }
        return false
    }

    fun checkMail(mail: String): Boolean {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            return true
        }
        return false
    }

    fun checkPhone(phone: String): Boolean {
        if (android.util.Patterns.PHONE.matcher(phone).matches()) {
            return true
        }
        return false
    }

    fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun registration(
        name: String,
        firstName: String,
        phone: String,
        mail: String,
        password: String
    ) {
        var auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(mail, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser!!
                    createUser(UserApp(firebaseUser.uid, name, firstName, phone, mail, null))
                    toastMessage("Votre compte a été créé avec succès")
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    toastMessage("Une erreur s'est produite, veuillez réessayer plus tard")
                }
            }

    }

    fun createUser(user: UserApp) {
        val db = FirebaseFirestore.getInstance()
        val noteDocRef = db.collection("users").document(user.id.toString())
        noteDocRef.set(user.toMap())
    }
}
