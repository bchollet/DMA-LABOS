package com.example.chatproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatproject.model.Message
import com.example.chatproject.model.Session
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

    private val _user: MutableLiveData<String> = MutableLiveData("")
    val user get(): LiveData<String> = _user

    private val _messages = MutableLiveData(emptyList<Message>())
    val messages: LiveData<List<Message>> get() = _messages

    private val _error = MutableLiveData("")
    val error: LiveData<String> get() = _error

    // returns whether a message should be sent to admins or users
    private fun messageDestination(messageContent: String) =
        if (messageContent.startsWith("@admin "))
            adminMessagesRef
        else
            messagesRef

    // betch the last messages that are in the database and post them in the live data
    fun fetchLastMessages() {
        val incomingMessagePayloadType = object : GenericTypeIndicator<Map<String, Message>>() {}
        suspend fun getMessages(ref: DatabaseReference) : List<Message>
            = ref.limitToLast(10)
                .orderByKey()
                .get()
                .await()
                .getValue(incomingMessagePayloadType)?.values?.toList()
                ?: emptyList()

        viewModelScope.launch {
            val adminMessages: List<Message> =
                try {
                    getMessages(adminMessagesRef)
                }  catch(e: Exception) {
                    _error.postValue(e.message)
                    listOf()
                }
            val messages = getMessages(messagesRef).plus(adminMessages)
            _messages.postValue(messages.sortedBy { it.id })
        }
    }

    // send a new message using the current user
    fun sendMessage(content: String) {
        val destination = messageDestination(content).push()
        viewModelScope.launch {
            try {
                destination.setValue(Message(destination.key!!, _user.value!!, content))
                    .await()
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }


    fun deleteMessage(messageId: String) {
        Log.d("Deletion", messageId)
        val messageToDelete = _messages.value!!
            .find { it.id == messageId }
            ?: throw IllegalArgumentException("$messageId does not exist")
        viewModelScope.launch {
            try {
                messageDestination(messageToDelete.content)
                    .child(messageId)
                    .removeValue()
                    .await()
            } catch(e: Exception) {
                _error.postValue(e.message)
            }

        }
    }

    fun editMessage(messageId: String, newContent: String) {
        viewModelScope.launch {
            val newMsg = Message(messageId, _user.value!!, newContent)
            messageDestination(newContent)
                .child(messageId)
                .setValue(newMsg)
                .await()
        }
    }

    fun login(author: String, isAdmin: Boolean) {
        // clear messages if there are some
        _messages.postValue(listOf())
        viewModelScope.launch {
            // anonymously sign in the user
            // this is the simplest form of auth that we found and is used only for the demo
            val id = Firebase.auth
                .signInAnonymously()
                .await()
                .user!!
                .uid

            usersRef.child(id)
                .setValue(Session(author, isAdmin))
                .await() // we ensure that the user is correctly registered before the rest

            _user.postValue(author)
            // once the user is correctly logged in, subscribe him to the correct message refs
            messagesRef.addChildEventListener(this@ChatViewModel)
            if (isAdmin) {
                adminMessagesRef.addChildEventListener(this@ChatViewModel)
            } else {
                // prevent being still subscribed to admin messages when swapping the status
                adminMessagesRef.removeEventListener(this@ChatViewModel)
            }
        }

    }

    // called when a child has been added in the database refs
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val message = snapshot.getValue(Message::class.java)!!
        _messages.postValue(_messages.value?.plus(message))
    }

    // Called when a message has been edited
    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        // find which message has been changed and swap it
        val changedMessage = snapshot.getValue(Message::class.java)!!
        val newMessagesList = _messages.value!!.map {
            if (it.id == changedMessage.id) {
                changedMessage
            } else {
                it
            }
        }
        _messages.postValue(newMessagesList)
    }

    // called when a message has been removed
    override fun onChildRemoved(snapshot: DataSnapshot) {
        // find which message has been removed  adn remove it in the new list
        val removedMessage = snapshot.getValue(Message::class.java)!!
        val newList = mutableListOf<Message>()
        _messages.value!!.forEach {
            if (it.id != removedMessage.id) {
                newList.add(it)
            }
        }
        _messages.postValue(newList)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        _error.postValue(error.message)
        Log.e("ChatCode", "Error occured with Firebase", error.toException())
    }
}