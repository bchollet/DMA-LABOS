package com.example.chatproject

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatproject.model.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel: ViewModel(), ChildEventListener {

    private val usersRef = Firebase.database.getReference("/users")
    private val messagesRef = Firebase.database.getReference("/messages")
    private val adminMessagesRef = Firebase.database.getReference("/messages-admin")

    private val _user: MutableLiveData<String?> = MutableLiveData(null)
    val user get(): LiveData<String?> = _user

    private val _messages = MutableLiveData(emptyList<Message>())
    val messages: LiveData<List<Message>> get() = _messages

    private val _permissionError = MutableLiveData("")
    val permissionError: LiveData<String> get() = _permissionError

    fun fetchLastMessages() {
        val ti = object : GenericTypeIndicator<Map<String, Message>>() {}
        suspend fun getMessages(ref: DatabaseReference) : List<Message> {
            return ref
                .limitToLast(10)
                .orderByKey()
                .get()
                .await()
                .getValue(ti)?.values?.toList()
                ?: emptyList()
        }

        viewModelScope.launch {
            var adminMessages: List<Message> = emptyList()
            try {
                adminMessages = getMessages(adminMessagesRef)
            } catch(e: Exception) {
                _permissionError.postValue(e.message)
            }
            val messages = getMessages(messagesRef).plus(adminMessages)
            _messages.postValue(messages.sortedBy { it.id })
        }
    }

    fun sendMessage(content: String) {
        val destination =
            if (content.startsWith("@admin "))
                adminMessagesRef.push()
            else
                messagesRef.push()
        viewModelScope.launch {
            try {
                destination.setValue(Message(destination.key!!, _user.value!!, content)).await()
            } catch (e: Exception) {
                _permissionError.postValue(e.message)
            }
        }
    }

    fun login(author: String, isAdmin: Boolean) {
        viewModelScope.launch {
            val id = Firebase.auth.signInAnonymously().await().user!!.uid
            usersRef.child(id).setValue(isAdmin)
            _user.postValue(author)
        }

        messagesRef.addChildEventListener(this)
        if (isAdmin) {
            adminMessagesRef.addChildEventListener(this)
        }

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
        if (error.code == DatabaseError.PERMISSION_DENIED) {
            _permissionError.postValue(error.message)
            return
        }
        TODO("Not yet implemented")
    }


}