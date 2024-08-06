package com.example.travelapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MyAdapter(private val context: Context, private var items: List<Users>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.firstname
        holder.email.text = item.username
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(items: List<Users>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val email: TextView = itemView.findViewById(R.id.tvEmail)
        private val chat: ImageView = itemView.findViewById(R.id.imEdit)
        private val edit: LinearLayout = itemView.findViewById(R.id.llContainer)

        init {
            chat.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    showChatDialog(user.email, user.username)
                }
            }

            edit.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = items[position]
                    showEditDialog(user.email, user.username)
                }
            }
        }
    }

    private fun showChatDialog(email: String, username: String) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout = LayoutInflater.from(context).inflate(R.layout.chat_user, null)
        val edtChat: EditText = dialogLayout.findViewById(R.id.edtChat)

        with(builder){
            setTitle("NOTIFY USER")
            setPositiveButton("send") { dialog, _ ->
                sendChatMessage(email, username, edtChat.text.toString())
                dialog.dismiss()
            }
            setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
        }

        builder.show()
    }

    private fun sendChatMessage(email: String, username: String, message: String) {
        val url = context.getString(R.string.url) + "sendChat.php"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                if (response == "failed") {
                    Toast.makeText(context, "Fail to send!!!",
                        Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(context, "Sent Successfully",
                        Toast.LENGTH_SHORT).show()

                }
            },
            Response.ErrorListener {
                Toast.makeText(context, "Please Try Again !!!",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["username"] = username
                params["message"] = message
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun showEditDialog(email: String, username: String) {
        val intent = Intent(context, UpdateDelete::class.java)
        intent.putExtra("EMAIL", email)
        intent.putExtra("USERNAME", username)
        context.startActivity(intent)
    }
}