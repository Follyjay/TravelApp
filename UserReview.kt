package com.example.travelapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class UserReview : AppCompatActivity() {

    private lateinit var btnDelete: ImageView
    //private var review: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.users_review)

        btnDelete = findViewById(R.id.deletePost)

        btnDelete.setOnClickListener{
            //deleteReview()
        }

    }

    /*private fun deleteReview(){
        review = findViewById(R.id.reviews)
        val reviewText = review.text.toString()

        val url = getString(R.string.url)  + "deleteReview.php"

        if (reviewText.isNotEmpty()) {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener <String> { response ->
                    try {
                        if (response == "success"){
                            Toast.makeText(this, "Review Deleted Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this, UserReview::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Record Failed to Delete!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception){
                        Toast.makeText(this, "Error parsing JSON response",
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
                    params["review"] = reviewText
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }*/

}