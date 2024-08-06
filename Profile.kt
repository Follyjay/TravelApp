package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class Profile : AppCompatActivity() {
    private lateinit var pictureBitmap:Bitmap

    private lateinit var profilePicture:ImageView
    private lateinit var fileUpload:ImageView
    private lateinit var changeUsername:EditText
    private lateinit var oldPass:EditText
    private lateinit var newPass:EditText
    private lateinit var changeProfile:Button
    private lateinit var submit:Button

    private lateinit var rcvReview: RecyclerView
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val email = this.intent.getStringExtra("EMAIL")
        val uname = this.intent.getStringExtra("UNAME")
        val status = this.intent.getStringExtra("STATUS")

        fileUpload = findViewById(R.id.fileUpload)
        changeProfile = findViewById(R.id.profileToggle)
        submit = findViewById(R.id.btnSubmit)

        rcvReview = findViewById(R.id.myReviews)
        rcvReview.layoutManager = LinearLayoutManager(this)

        adapter = ReviewAdapter(this, email.toString(), status.toString(), mutableListOf())
        rcvReview.adapter = adapter

        fetchReviews(uname.toString())

        changeProfile.setOnClickListener{
            fileUpload.visibility = View.VISIBLE
            changeUsername.visibility = View.VISIBLE
            oldPass.visibility = View.VISIBLE
            newPass.visibility = View.VISIBLE
            submit.visibility = View.VISIBLE
            changeProfile.visibility = View.GONE
        }

        val selectPicture =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val inputStream = contentResolver.openInputStream(it)
                    pictureBitmap = BitmapFactory.decodeStream(inputStream)
                    profilePicture.setImageBitmap(pictureBitmap)
                }
            }

        fileUpload.setOnClickListener{
            selectPicture.launch("image/*")
        }

        submit.setOnClickListener{
            updateUser()
        }

        fetchUser(email.toString())
    }

    private fun fetchUser(email: String) {

        profilePicture = findViewById(R.id.profilePicture)
        changeUsername = findViewById(R.id.changeUsername)
        oldPass = findViewById(R.id.oldPassword)
        newPass = findViewById(R.id.newPassword)

        val url = getString(R.string.url) + "selectCurrentUser.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                try {
                    val result = JSONObject(response)

                    val uname = result.getString("username")
                    val encodedImage = result.getString("imageDP")

                    val dec: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                    val decodedImage: Bitmap = BitmapFactory.decodeByteArray(dec, 0, dec.size)
                    Log.d("PRINT", "$decodedImage")
                    val pp = BitmapDrawable(resources, decodedImage)

                    profilePicture.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    profilePicture.setImageDrawable(pp)

                    changeUsername.setText(uname.toString())

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

    private fun updateUser(){

        val email = this.intent.getStringExtra("EMAIL")

        val username = changeUsername.text.toString()
        val password = newPass.text.toString()
        val oldPass = oldPass.text.toString()

        val url = getString(R.string.url)  + "updateUser.php"

        if (username.isNotEmpty() && password.isNotEmpty()) {

            if (password.length >= 8){

                if (password != oldPass) {

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

                    val queue = Volley.newRequestQueue(applicationContext)
                    val stringRequest = object : StringRequest(Method.POST, url,
                        Response.Listener<String> { response ->
                            if (response.equals("success")) {
                                Toast.makeText(
                                    this, "Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()

                            } else if (response.equals("failure")) {
                                Toast.makeText(
                                    this, "Failed to Update",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this, "Failed to Update",
                                    Toast.LENGTH_LONG
                                ).show()

                            }) {
                            override fun getParams(): MutableMap<String, String> {
                                val params = HashMap<String, String>()
                                params["username"] = username
                                params["password"] = password
                                params["email"] = email.toString()
                                params["imageDP"] = encodedImage
                                return params
                            }
                        }
                    queue.add(stringRequest)

                } else {
                    Toast.makeText(this,
                        "Password Must not be same as old password",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } else{
                Toast.makeText(this,
                    "Password Must be at least Eight(8) Character",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {
            Toast.makeText(this,
                "All Fields are Required",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun fetchReviews(uname: String) {

        val url = getString(R.string.url) + "fetchReviews.php"

        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val reviews = mutableListOf<Reviews>()
                for (i in 0 until response.length()) {
                    try {
                        val usersObject = response.getJSONObject(i)

                        val e = usersObject.getString("email")
                        val u = usersObject.getString("username")
                        val r = usersObject.getString("review_text")
                        val d = usersObject.getString("review_id")
                        val i = usersObject.getString("imageDP")
                        val lc = usersObject.getInt("likes")
                        val dc = usersObject.getInt("dislikes")

                        val id = d.toInt()

                        if (u == uname) {
                            reviews.add(Reviews(e, u, r, id, i, lc, dc))
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(reviews)

            }, { error ->
                Toast.makeText(
                    this, "Error fetching Reviews: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
        queue.add(jsonArrayRequest)

    }

}