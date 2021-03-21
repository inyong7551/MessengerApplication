package com.example.chattingapp.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chattingapp.R
import com.example.chattingapp.chat.MessageActivity
import com.example.chattingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_message2.*

class PeopleFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    // 왜 따로 recyclerView를 정의하였는지 복습
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_people, container, false)
        recyclerView = view.findViewById(R.id.peoplefragment_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(inflater.context)
        val adapter = PeopleFragmentRecyclerViewAdapter()
        recyclerView?.adapter = adapter

        return view
    }

    class PeopleFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var userModels: ArrayList<UserModel> = ArrayList()

        init {
            val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userModels.clear()
                    snapshot.children.forEach{
                        val userModel: UserModel = UserModel(it.child("userName").value.toString()
                                , it.child("profileImageUrl").value.toString(), it.child("uid").value.toString())

                        if(userModel?.uid.equals(uid)){
                            return@forEach
                        }
                        Log.d("PeopleFragment", userModel.profileImageUrl.toString())
                        userModels.add(userModel)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return userModels.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load(userModels[position].profileImageUrl)
                .apply(RequestOptions().circleCrop())
                .into((holder as CustomViewHolder).imageView)

            holder.textView.text = userModels[position].userName

            holder.itemView.setOnClickListener{
                val intent = Intent(it.context, MessageActivity::class.java)
                intent.putExtra("destinationUid", userModels[position].uid)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //val option: ActivityOptions = ActivityOptions.makeCustomAnimation(it.context, R.anim.fromright, R.anim.toleft)
                startActivity(it.context, intent, null)
            }
        }

        private class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imageView: ImageView = itemView.findViewById(R.id.frienditem_imageview)
            var textView: TextView = itemView.findViewById(R.id.frienditem_textview)
        }

    }
}