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
import kotlin.random.Random

class NewReview : AppCompatActivity() {
    private lateinit var post: Button
    private lateinit var review: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_review)

        var uname = intent.getStringExtra("UNAME").toString()
        review = findViewById(R.id.edtReview)
        review.requestFocus()

        post = findViewById(R.id.btnPost)
        post.setOnClickListener(){
            postReview()
        }
    }

    private fun postReview() {
        var email = this.intent.getStringExtra("EMAIL")
        var username = this.intent.getStringExtra("UNAME")
        var type = this.intent.getStringExtra("STATUS")

        review = findViewById(R.id.edtReview)
        val reviewText = review.text.toString()

        val reviewID = Random.nextInt(100000, 1000000)

        val url = getString(R.string.url)  + "addReview.php"

        if (reviewText.isNotEmpty()) {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> {  response ->
                    if (response == "success"){
                        Toast.makeText(this, "You have successfully posted a new review",
                        Toast.LENGTH_LONG
                        ).show()

                        if (type == "user"){
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("EMAIL",email)
                            intent.putExtra("UNAME",username)
                            intent.putExtra("STATUS",type)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, Dashboard::class.java)
                            intent.putExtra("EMAIL",email)
                            intent.putExtra("UNAME",username)
                            intent.putExtra("STATUS",type)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        Toast.makeText(this, "Please Try Again !!!",
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
                    params["email"] = email.toString()
                    params["username"] = username.toString()
                    params["review"] = reviewText
                    params["review_id"] = reviewID.toString()
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}