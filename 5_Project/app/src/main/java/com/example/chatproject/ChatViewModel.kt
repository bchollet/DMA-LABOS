package com.example.chatproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatproject.model.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel(database: FirebaseDatabase = Firebase.database) : ViewModel(), ChildEventListener {
    private val messagesRef = database.getReference("/messages")
    init {
        messagesRef.addChildEventListener(this)
    }
    private val _messages = MutableLiveData(emptyList<Message>())
    val messages: LiveData<List<Message>> get() = _messages

    fun sendMessage(message: Message) {
        val children = messagesRef.push()
        children.setValue(message)
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val message = snapshot.getValue(Message::class.java)!!
        _messages.postValue(_messages.value?.plus(message))
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }



}