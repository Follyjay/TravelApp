package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream

class Signup : AppCompatActivity() {

    private lateinit var url: String

    private lateinit var userDP: ImageView
    private lateinit var uploadImage: ImageView
    private lateinit var username: EditText
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPass: EditText
    private lateinit var newUserReg: Button
    private lateinit var male: RadioButton
    private lateinit var female: RadioButton
    private lateinit var errorMsg: TextView

    private lateinit var pictureBitmap:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        userDP = findViewById(R.id.imgUserPicture)
        uploadImage = findViewById(R.id.fileUpload)

        username = findViewById(R.id.edtUsername)
        firstname = findViewById(R.id.edtFirstName)
        lastname = findViewById(R.id.edtLastName)
        email = findViewById(R.id.edtEmail)
        password = findViewById(R.id.edtPassword)
        confirmPass = findViewById(R.id.edtPassword2)
        male = findViewById(R.id.rbtMale)
        female = findViewById(R.id.rbtFemale)
        newUserReg = findViewById(R.id.btnRegister)
        errorMsg = findViewById(R.id.tvError)

        val selectPicture =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val inputStream = contentResolver.openInputStream(it)
                    pictureBitmap = BitmapFactory.decodeStream(inputStream)
                    userDP.setImageBitmap(pictureBitmap)
                }
            }

        uploadImage.setOnClickListener{
            selectPicture.launch("image/*")
        }

        newUserReg.setOnClickListener{
            addUser()
        }

        val login = findViewById<TextView>(R.id.tvLogin)
        login.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun addUser() {

        val username = username.text.toString()
        val firstname = firstname.text.toString()
        val lastname = lastname.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()
        val confirmPass = confirmPass.text.toString()
        val gender = if (female.isChecked) {
            female.text.toString()
        } else {
            male.text.toString()
        }

        val url = getString(R.string.url)  + "adduser.php"

        if (firstname.isNotEmpty() || lastname.isNotEmpty() || email.isNotEmpty()
            || password.isNotEmpty()
        ) {

            if (password.length >= 8){

                if (password == confirmPass) {

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

                    val queue = Volley.newRequestQueue(applicationContext)
                    val stringRequest = object : StringRequest(Method.POST, url,
                        Response.Listener<String> { response ->
                            if (response.equals("success")) {
                                Toast.makeText(
                                    this, "Registration Successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, Login::class.java))
                                finish()

                            } else if (response.equals("failure")) {
                                showError("Registration Failed")

                            } else if (response.equals("Already Exist")) {
                                showError("User Already Exist")
                            }
                        }, Response.ErrorListener { error ->
                            error?.message?.let { showError(it) }

                        }) {
                        override fun getParams(): MutableMap<String, String> {
                            val params = HashMap<String, String>()
                            params["username"] = username
                            params["lastname"] = lastname
                            params["firstname"] = firstname
                            params["gender"] = gender
                            params["email"] = email
                            params["password"] = password
                            params["imageDP"] = encodedImage
                            return params
                        }
                    }
                    queue.add(stringRequest)
                } else {
                    showError(getString(R.string.PasswordMismatch))
                }

            } else{
                Toast.makeText(this,
                    "Password Must be at least Eight(8) Character",
                    Toast.LENGTH_LONG)
                    .show()
            }

        } else {
            showError(getString(R.string.FieldsRequired))
        }
    }

    private fun showError(message: String) {
        errorMsg.text = message
        errorMsg.visibility = View.VISIBLE
    }

}