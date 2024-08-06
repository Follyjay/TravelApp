package com.example.travelapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

class MainActivity : AppCompatActivity() {
    private lateinit var rcvReview: RecyclerView
    private lateinit var adapter: ReviewAdapter

    private lateinit var dp:ImageView
    private lateinit var box:TextView
    private lateinit var edit:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = this.intent.getStringExtra("EMAIL")
        val uname = this.intent.getStringExtra("UNAME")
        val status = this.intent.getStringExtra("STATUS")

        box = findViewById(R.id.reviewBox)
        edit = findViewById(R.id.editProfile)

        rcvReview = findViewById(R.id.rcvReview)
        rcvReview.layoutManager = LinearLayoutManager(this)

        adapter = ReviewAdapter(this, email.toString(), status.toString(), mutableListOf())
        rcvReview.adapter = adapter

        box.setOnClickListener{
            val intent = Intent(this, NewReview::class.java)
            intent.putExtra("EMAIL", email)
            intent.putExtra("UNAME", uname)
            intent.putExtra("STATUS", status)
            startActivity(intent)
        }

        edit.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("EMAIL",email)
            intent.putExtra("UNAME",uname)
            startActivity(intent)
        }

        fetchCurrentUser(email.toString())

        fetchReviews()
    }

    private fun fetchCurrentUser(email: String){

        dp = findViewById(R.id.userDP)
        box = findViewById(R.id.reviewBox)

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

                    dp.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    dp.setImageDrawable(pp)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                Toast.makeText(this, "Please Try Again !!!",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun fetchReviews() {

        val url = getString(R.string.url) + "fetchReviews.php"

        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.POST, url, null,
            { response ->
                Log.d("RESPONSE", "$response")
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

                        reviews.add(Reviews(e, u, r, id, i, lc, dc))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(reviews)

            }, { error ->
                Log.d("ERROR", "${error.message}")
                Toast.makeText(
                    this, "Error fetching Reviews: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue.add(jsonArrayRequest)

    }
}