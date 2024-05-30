package com.example.chatproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatproject.model.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel: ViewModel(), ChildEventListener {

    private val usersRef = Firebase.database.getReference("/users")
    private val messagesRef = Firebase.database.getReference("/messages")

    private val _user: MutableLiveData<String?> = MutableLiveData(null)
    val user get(): LiveData<String?> = _user

    private val _messages = MutableLiveData(emptyList<Message>())
    val messages: LiveData<List<Message>> get() = _messages

    init {
        messagesRef.addChildEventListener(this)
    }

    fun sendMessage(content: String) {
        val children = messagesRef.push()
        children.setValue(Message(children.key!!, _user.value!!, content))
    }

    fun login(author: String, isAdmin: Boolean) {
        usersRef.child(author).setValue(isAdmin)
        _user.postValue(author)
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