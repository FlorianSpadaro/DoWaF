package com.example.dowaf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickRegistrationBtn(view: View)
    {
        val intent = Intent(applicationContext, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun connection(view: View)
    {
        val mail = usernameTxt.text.toString()
        val password = passwordTxt.text.toString()
        var auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                var intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("id", auth.currentUser?.email)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_LONG).show()
            }
        })
    }
}
