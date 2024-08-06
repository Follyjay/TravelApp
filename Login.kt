package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {

    lateinit var email:EditText
    lateinit var password:EditText
    lateinit var signup:Button
    lateinit var login:Button
    lateinit var errorMsg:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.btnLogin)
        signup = findViewById(R.id.btnSignUp)
        errorMsg = findViewById(R.id.tvError)

        login.setOnClickListener{
            loginAction()
        }

        signup.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun loginAction(){
        email = findViewById(R.id.edtUsername)
        password = findViewById(R.id.edtPassword)
        errorMsg = findViewById(R.id.tvError)

        val email = email.text.toString()
        val password = password.text.toString()

        val url = getString(R.string.url) + "login.php"

        if( (email.isNotEmpty() && password.isNotEmpty()) ) {

            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("LOGIN", "Response: $response")
                    try {
                        if (response.equals("Failure")) {
                            errorMsg.text = "Username/Password is not correct"
                            errorMsg.visibility = View.VISIBLE

                        } else {
                            val result = JSONObject(response)
                            val uname = result.getString("username")
                            val type = result.getString("type")

                            Toast.makeText(
                                this, "Login Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            if (type == "user"){

                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("EMAIL",email)
                                intent.putExtra("UNAME",uname)
                                intent.putExtra("STATUS",type)
                                startActivity(intent)
                                finish()

                            } else if (type == "admin") {

                                val intent = Intent(this, Dashboard::class.java)
                                intent.putExtra("EMAIL",email)
                                intent.putExtra("UNAME",uname)
                                intent.putExtra("STATUS",type)
                                startActivity(intent)
                                finish()

                            }

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this, "Invalid Credentials",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { error ->
                    errorMsg.text = error.message
                    //println(error.message)
                    errorMsg.visibility = View.VISIBLE
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            queue.add(stringRequest)

        }else{
            errorMsg.text = "Username/Password Cannot be Empty"
            errorMsg.visibility = View.VISIBLE
        }
    }
}