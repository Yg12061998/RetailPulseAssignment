package com.example.retailpulseassignment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    lateinit var btnLogIn: Button
    lateinit var edtUser: TextInputLayout
    lateinit var edtPass: TextInputLayout
    lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogIn = findViewById(R.id.btnLogIn)
        edtUser = findViewById(R.id.edtUsername)
        edtPass = findViewById(R.id.edtPassword)

        var userName = edtUser.editText?.text.toString()
        var passWord = edtPass.editText?.text.toString()

        sharedPref = getSharedPreferences(getString(R.string.sharedPrefUserData), MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        userName = sharedPref.getString("user", "").toString()

        if(isLoggedIn){
            getLoggedIn(userName)
        }

        btnLogIn.setOnClickListener{

            userName = edtUser.editText?.text.toString()
            passWord = edtPass.editText?.text.toString()

            if(userName.contentEquals("")){
                edtUser.error = "Username is empty"
            }else if(passWord.contentEquals("")){
                edtPass.error = "Password is empty"
            }else if(!userName.contentEquals("user 1") && !userName.contentEquals("user 2")){
                edtPass.error = "Username or Password is Invalid"
            }else if(!passWord.contentEquals("retailpulse")){
                edtPass.error = "Username or Password is Invalid"
            }else{

                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                if(userName.contentEquals("user 1"))
                    getLoggedIn("Shyam")
                else
                    getLoggedIn("Ram")

            }
        }

        edtUser.editText?.doOnTextChanged {it,_,_,count ->
            edtUser.error = ""
            edtPass.error = ""
        }
        edtPass.editText?.doOnTextChanged {it,_,_,count ->
            edtUser.error = ""
            edtPass.error = ""
        }

    }

    private fun getLoggedIn(userName: String) {

        sharedPref.edit().putString("user", userName).apply()
        sharedPref.edit().putBoolean("isLoggedIn", true).apply()

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("user", userName)
        startActivity(intent)
        finishAffinity()



    }
}