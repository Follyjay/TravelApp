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

class ViewUsers : Fragment() {

    private lateinit var rcvUsers: RecyclerView
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status = arguments?.getString("STATUS")
        val username = arguments?.getString("UNAME")
        val email = arguments?.getString("EMAIL")

        rcvUsers = view.findViewById(R.id.rcvUsers)
        rcvUsers.layoutManager = LinearLayoutManager(context)

        adapter = MyAdapter(requireContext(), emptyList())
        rcvUsers.adapter = adapter

        fetchUsers()
    }

    private fun fetchUsers() {
        val url = getString(R.string.url) + "selectUsers.php"

        val queue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                val user = mutableListOf<Users>()
                for (i in 0 until response.length()) {
                    try {
                        val usersObject = response.getJSONObject(i)

                        val u = usersObject.getString("username")
                        val l = usersObject.getString("lastname")
                        val f = usersObject.getString("firstname")
                        val g = usersObject.getString("gender")
                        val e = usersObject.getString("email")
                        val i = usersObject.getString("imageDP")

                        user.add(Users(u, l, f, g, e, i))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter.setData(user)

            }, { error ->
                Toast.makeText(
                    context, "Error fetching users: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue.add(jsonArrayRequest)

    }

}