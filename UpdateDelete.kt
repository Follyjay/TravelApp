package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class UpdateDelete : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText

    private lateinit var update: Button
    private lateinit var delete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete)

        val type = this.intent.getStringExtra("STATUS")
        val uname = this.intent.getStringExtra("UNAME")
        val email = this.intent.getStringExtra("EMAIL")

        update = findViewById(R.id.btnUpdate)
        delete = findViewById(R.id.btnDelete)

        fetchUser(email.toString())

        val intent = Intent(this, Dashboard::class.java)
        intent.putExtra("EMAIL",email)
        intent.putExtra("UNAME",uname)
        intent.putExtra("STATUS",type)

        update.setOnClickListener{
            updateRecord(email.toString())
            startActivity(intent)
            finish()
        }
        delete.setOnClickListener{
            deleteRecord(email.toString())
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUser(email: String) {

        username = findViewById(R.id.edtUsername)
        firstname = findViewById(R.id.edtFirst)
        lastname = findViewById(R.id.edtLast)

        val url = getString(R.string.url) + "selectCurrentUser.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val result = JSONObject(response)
                    val uname = result.getString("username")
                    val fName = result.getString("firstname")
                    val lName = result.getString("lastname")

                    val user = uname
                    val first = fName.toString()
                    val last = lName.toString()

                    username.setText(uname)
                    username.setText(uname.toString())
                    firstname.setText(fName.toString())
                    lastname.setText(lName.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener {error->
                Toast.makeText(this, "Error: ${error.message}",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun updateRecord(email: String){
        val username = username.text.toString()
        val firstname = firstname.text.toString()
        val lastname = lastname.text.toString()

        val url = getString(R.string.url)  + "updateUser.php"

        if (username.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty()) {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    if (response == "success"){
                        Toast.makeText(this, "Record Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Record Failed to Update!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError?) {
                        if (error != null) {
                            Toast.makeText(this, "Please Try Again !!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["username"] = username
                    params["name"] = firstname
                    params["email"] = lastname
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    private fun deleteRecord(email: String){

        val url = getString(R.string.url)  + "deleteUser.php"

        if (email.isNotEmpty()) {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    if (response == "success"){
                        Toast.makeText(this, "Record Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Record Failed to Delete!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    fun onErrorResponse(error: VolleyError?) {
                        if (error != null) {
                            Toast.makeText(this, "Please Try Again !!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}