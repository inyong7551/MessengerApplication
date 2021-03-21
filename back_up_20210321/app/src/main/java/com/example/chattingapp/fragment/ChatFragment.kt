package com.example.chattingapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chattingapp.R
import com.example.chattingapp.model.ChatModel
import com.example.chattingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chat.*
import org.jetbrains.anko.coroutines.experimental.asReference
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.chatFragment_recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView?.adapter = ChatRecyclerViewAdapter()

        return view
    }

    class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var chatModels: ArrayList<ChatModel> = ArrayList()
        var uid: String
        init {
            uid = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/"+uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModels.clear()
                    snapshot.children.forEach{
                        var chatModel: ChatModel = ChatModel()
                        it.child("comments").children.forEach{
                            chatModel?.comments!!.put(uid, ChatModel.Comment(uid, it.child("message").value.toString()))
                        }
                        it.child("users").children.forEach{
                            chatModel?.users!!.put(it.key.toString(), true)
                        }
                        chatModels.add(chatModel)
                    }
                    
                    notifyDataSetChanged()
                }


            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)

            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return chatModels.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var customViewHolder: CustomViewHolder = holder as CustomViewHolder
            var destinationUid: String? = null

            chatModels[position].users.keys.forEach{
                if(!it.equals(uid)){
                    destinationUid = it
                }
            }

            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var userModel: UserModel = UserModel(snapshot.child("userName").value.toString(), snapshot.child("profileImageUrl").value.toString(), snapshot.child("uid").value.toString())

                    Glide.with(customViewHolder.itemView.context)
                            .load(userModel.profileImageUrl)
                            .apply(RequestOptions().circleCrop())
                            .into(customViewHolder.imageView)

                    customViewHolder.textView_title.text = userModel.userName
                }
            })

            var commentMap: TreeMap<String, ChatModel.Comment> = TreeMap(Collections.reverseOrder())
            commentMap.putAll(chatModels[position].comments)
            val lastMessage: String = commentMap.values.first().message
            customViewHolder.textView_lastMessage.text = lastMessage
        }

        inner class CustomViewHolder: RecyclerView.ViewHolder{
            var imageView: ImageView
            var textView_title: TextView
            var textView_lastMessage: TextView

            constructor(itemView : View) : super(itemView){
                imageView = itemView.findViewById(R.id.chatItem_imageView)
                textView_title = itemView.findViewById(R.id.chatItem_textView_title)
                textView_lastMessage = itemView.findViewById(R.id.chatItem_textView_lastMessage)
            }
        }

    }
}