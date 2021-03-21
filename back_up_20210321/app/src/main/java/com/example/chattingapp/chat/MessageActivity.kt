package com.example.chattingapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
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
import kotlinx.android.synthetic.main.activity_message2.*
import org.jetbrains.anko.find

class MessageActivity : AppCompatActivity() {

    private lateinit var destinationUid: String
    private lateinit var button: Button
    private lateinit var editText: EditText

    private lateinit var uid: String
    private var chatRoomUid: String? = null

    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message2)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        destinationUid = intent.getStringExtra("destinationUid")
        editText = messageActivity_edittext
        button = messageActivity_button

        recyclerView = messageActivity_recyclerView
        button.setOnClickListener{
            var chatModel: ChatModel = ChatModel()
            chatModel.users.put(uid, true)
            chatModel.users.put(destinationUid, true)

            if(chatRoomUid == null) {
                button.isEnabled = false
                FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(chatModel).addOnSuccessListener { it ->
                    checkChatRoom()
                }
            }else{
                var comment: ChatModel.Comment = ChatModel.Comment(uid, editText.text.toString())
                FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comment).addOnCompleteListener{
                    editText.setText("")
                }

            }
        }
        checkChatRoom()
    }

    fun checkChatRoom(){
        FirebaseDatabase.getInstance().reference.child("chatrooms")
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{ it ->
                            var chatModel = ChatModel()
                            it.child("comments").children.forEach{
                                chatModel?.comments!!.put(uid, ChatModel.Comment(uid, it.child("message").value.toString()))
                            }
                            it.child("users").children.forEach{
                                chatModel?.users!!.put(it.key.toString(), true)
                            }

                            if(chatModel?.users!!.containsKey(destinationUid)){
                                chatRoomUid = it.key.toString()
                                button.isEnabled = true
                                recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                                recyclerView?.adapter = RecyclerViewAdapter()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var comments: ArrayList<ChatModel.Comment> = arrayListOf()
        lateinit var userModel: UserModel
        init {
            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    userModel = UserModel(snapshot.child("userName").value.toString(), snapshot.child("profileImageUrl").value.toString(), snapshot.child("uid").value.toString())
                    getMessageList()

                }

            })
        }

        fun getMessageList(){
            FirebaseDatabase.getInstance().reference.child("chatrooms")
                    .child(chatRoomUid!!).child("comments").addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            comments.clear()
                            snapshot.children.forEach{
                                //comments.add(it.getValue(ChatModel.Comment::class.java)!!)
                                comments.add(ChatModel.Comment(it.child("uid").value.toString(), it.child("message").value.toString()))
                            }
                            notifyDataSetChanged()

                            recyclerView?.scrollToPosition(comments.size-1)
                        }

                    })
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val messageViewHolder: MessageViewHolder = holder as MessageViewHolder

            if(comments[position].uid.equals(uid)){
                messageViewHolder.textView_message.setText(comments[position].message)
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                messageViewHolder.ll_destination.visibility = View.INVISIBLE
                messageViewHolder.textView_message.textSize = 25f
                messageViewHolder.ll_main.gravity = Gravity.RIGHT
            }else{
                Glide.with(holder.itemView.context)
                        .load(userModel.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile)
                messageViewHolder.textView_name.text = userModel.userName
                messageViewHolder.ll_destination.visibility = View.VISIBLE
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                messageViewHolder.textView_message.text = comments[position].message
                messageViewHolder.textView_message.textSize = 25f
                messageViewHolder.ll_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            var textView_message: TextView = itemView.findViewById(R.id.messageItem_textView_message)
            var textView_name: TextView = itemView.findViewById(R.id.messageItem_textView_name)
            var imageView_profile: ImageView = itemView.findViewById(R.id.messageItem_imageview_profile)
            var ll_destination: LinearLayout = itemView.findViewById(R.id.messageItem_linearlayout_destination)
            var ll_main: LinearLayout = itemView.findViewById(R.id.messageItem_linearlayout_main)
        }
    }
}