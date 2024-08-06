package com.example.travelapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class ReviewAdapter(private val context: Context, val email: String, val status: String, private var items: MutableList<Reviews>) :
    RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.users_review, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.username
        holder.reviews.text = item.review
        holder.likesCount.text = item.likes_count.toString()
        holder.dislikesCount.text = item.dislikes_count.toString()

        val encodedImage = item.imageDP
        val dec: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedImage: Bitmap = BitmapFactory.decodeByteArray(dec, 0, dec.size)

        if (decodedImage == null) {

            holder.image.setImageDrawable(R.drawable.baseline_person_add_24.toDrawable())
            Log.d("ImageDebug", "Using default image")
        } else {

            val resources = context.resources
            val pp = BitmapDrawable(resources, decodedImage)
            holder.image.setImageDrawable(pp)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(items: List<Reviews>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviews: TextView = itemView.findViewById(R.id.reviews)
        val name: TextView = itemView.findViewById(R.id.username)
        val image: ImageView = itemView.findViewById(R.id.userImage)
        val likesCount: TextView = itemView.findViewById(R.id.likesCount)
        val dislikesCount: TextView = itemView.findViewById(R.id.dislikesCount)
        private val deletePost: ImageView = itemView.findViewById(R.id.deletePost)
        private val like: TextView = itemView.findViewById(R.id.like)
        private val dislike: TextView = itemView.findViewById(R.id.disLike)

        init {
            deletePost.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val rev = items[position]
                    deleteReview(rev.review, status)
                }
            }

            like.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val use = items[position]
                    if (like.currentTextColor == R.color.lightBlue){
                        unlikeReview(email, use.review, use.review_id)
                        like.setTextColor(Color.parseColor("#000000"))
                    } else {
                        likeReview(email, use.review, use.review_id)
                        like.setTextColor(Color.parseColor("#067FF8"))
                        val count: Int = likesCount.text.toString().toInt()
                        likesCount.text = (count + 1).toString()
                    }

                }
            }

            dislike.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val use = items[position]
                    if (dislike.currentTextColor == R.color.lightBlue){
                        unDislikeReview(email, use.review, use.review_id)
                        dislike.setTextColor(Color.parseColor("#000000"))
                    } else {
                        dislikeReview(email, use.review, use.review_id)
                        dislike.setTextColor(Color.parseColor("#067FF8"))
                        val count: Int = likesCount.text.toString().toInt()
                        dislikesCount.text = (count + 1).toString()
                    }

                }
            }
        }
    }

    private fun unDislikeReview(email: String, review: String, review_id: Int) {
        val url = context.getString(R.string.url) + "undislikeReview.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    if (response.equals("failed")) {
                        Toast.makeText(context, "Record Failed to Delete!!!", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        Toast.makeText(context, "You Liked This Review", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(
                        context, "Connection Error !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            },
            Response.ErrorListener {
                Toast.makeText(context, "Please Try Again !!!", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["review"] = review
                params["review_id"] = review_id.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun dislikeReview(email: String, review: String, review_id: Int) {
        val url = context.getString(R.string.url) + "dislikeReview.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    if (response.equals("failed")) {
                        Toast.makeText(context, "Record Failed to Delete!!!", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        Toast.makeText(context, "You Disliked This Review", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(
                        context, "Connection Error !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            },
            Response.ErrorListener {
                Toast.makeText(context, "Please Try Again !!!", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["review"] = review
                params["review_id"] = review_id.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun unlikeReview(email: String, review: String, review_id: Int) {
        val url = context.getString(R.string.url) + "unlikeReview.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    if (response.equals("failed")) {
                        Toast.makeText(context, "Record Failed to Delete!!!", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        Toast.makeText(context, "You Liked This Review", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(
                        context, "Connection Error !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            },
            Response.ErrorListener {
                Toast.makeText(context, "Please Try Again !!!", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["review"] = review
                params["review_id"] = review_id.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun likeReview(email: String, review: String, review_id: Int) {

        val url = context.getString(R.string.url) + "likeReview.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    if (response.equals("failed")) {
                        Toast.makeText(context, "Record Failed to Delete!!!", Toast.LENGTH_LONG)
                            .show()

                    } else {
                        Toast.makeText(context, "You Liked This Review", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(
                        context, "Connection Error !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            },
            Response.ErrorListener {
                Toast.makeText(context, "Please Try Again !!!", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["review"] = review
                params["review_id"] = review_id.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun deleteReview(reviewText: String, status: String) {
        if (status == "user"){
            Toast.makeText(context, "Review Deleted Successfully", Toast.LENGTH_SHORT)
                .show()

            // Removing the review from recyclerview and updating it
            items.removeIf{ it.review == reviewText }
            notifyDataSetChanged()
        } else {
            val url = context.getString(R.string.url) + "deleteReview.php"

            val queue = Volley.newRequestQueue(context)
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener<String> { response ->
                    try {
                        if (response.equals("failed")) {
                            Toast.makeText(context, "Record Failed to Delete!!!", Toast.LENGTH_LONG)
                                .show()

                        } else {
                            Toast.makeText(context, "Review Deleted Successfully", Toast.LENGTH_SHORT)
                                .show()

                            // Removing the review from recyclerview and updating it
                            items.removeIf{ it.review == reviewText }
                            notifyDataSetChanged()
                        }
                    } catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(
                            context, "Connection Error !!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(context, "Please Try Again !!!", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["review"] = reviewText
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}