package com.example.travelapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class ViewReview : Fragment() {

    private lateinit var rcvReview: RecyclerView
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status = arguments?.getString("STATUS")
        val username = arguments?.getString("UNAME")
        val email = arguments?.getString("EMAIL")

        rcvReview = view.findViewById(R.id.rcvReview)
        rcvReview.layoutManager = LinearLayoutManager(context)

        adapter = ReviewAdapter(requireContext(), email.toString(), status.toString(), mutableListOf())
        rcvReview.adapter = adapter

        fetchReviews()

    }

    private fun fetchReviews() {

        val url = getString(R.string.url) + "fetchReviews.php"

        val queue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.POST, url, null,
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

                        reviews.add(Reviews(e, u, r, id, i, lc, dc))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(reviews)

            }, { error ->
                Toast.makeText(
                    context, "Error fetching Reviews: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            })
        queue.add(jsonArrayRequest)

    }

}