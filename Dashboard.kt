package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Dashboard : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var reviewTab: Button
    private lateinit var userTab: Button

    private lateinit var dp: ImageView
    private lateinit var box: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val type = this.intent.getStringExtra("STATUS")
        val uname = this.intent.getStringExtra("UNAME")
        val email = this.intent.getStringExtra("EMAIL")

        reviewTab = findViewById(R.id.reviewTab)
        userTab = findViewById(R.id.userTab)

        val backgroundImage: Drawable? = ContextCompat.getDrawable(this, R.drawable.button)

        box = findViewById(R.id.reviewBox)
        //edit = findViewById(R.id.editProfile)

        reviewTab.setOnClickListener{
            val fragment = ViewReview()
            // Declaring/Initializing variables for holding arguments
            val args = Bundle()
            val args2 = Bundle()
            val args3 = Bundle()

            args.putString("STATUS", type)
            args2.putString("UNAME", uname)
            args3.putString("EMAIL", email)

            // Setting the arguments for the fragment
            fragment.arguments = args
            goToFragment(fragment)

            reviewTab.background = backgroundImage
            userTab.background = null
        }
        userTab.setOnClickListener{
            val fragment = ViewUsers()
            // Declaring/Initializing variables for holding arguments
            val args = Bundle()
            val args2 = Bundle()
            val args3 = Bundle()

            args.putString("STATUS", type)
            args2.putString("UNAME", uname)
            args3.putString("EMAIL", email)

            // Setting the arguments for the fragment
            fragment.arguments = args
            goToFragment(fragment)

            userTab.background = backgroundImage
            reviewTab.background = null
        }

        box.setOnClickListener{

            val intent = Intent(this, NewReview::class.java)
            intent.putExtra("EMAIL",email)
            intent.putExtra("UNAME",uname)
            intent.putExtra("STATUS",type)
            startActivity(intent)
        }

        goToFragment(ViewReview())
        reviewTab.background = backgroundImage
        fetchCurrentUser(email.toString())
    }

    private fun goToFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun fetchCurrentUser(email: String){

        dp = findViewById(R.id.userDP)

        //val email = this.intent.getStringExtra("EMAIL")
        val url = getString(R.string.url) + "selectCurrentUser.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val result = JSONObject(response)
                    val uname = result.getString("username")
                    val encodedImage = result.getString("imageDP")
                    val dec: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                    val decodedImage: Bitmap = BitmapFactory.decodeByteArray(dec, 0, dec.size)
                    Log.d("PRINT", "$decodedImage")
                    val pp = BitmapDrawable(resources, decodedImage)

                    dp.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    dp.setImageDrawable(pp)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                fun onErrorResponse(error: VolleyError?) {
                    if (error != null) {
                        Toast.makeText(this, error.message,
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

}