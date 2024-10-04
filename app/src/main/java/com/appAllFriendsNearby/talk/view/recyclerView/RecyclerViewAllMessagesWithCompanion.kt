package com.appAllFriendsNearby.talk.view.recyclerView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.dataBase.removeSelectMessages
import com.appAllFriendsNearby.talk.databinding.FragmentDialogBinding
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.getDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class RecyclerViewAllMessagesWithCompanion(
    private val list: List<UserMessagesWithCompanionDataClass>,
    binding: FragmentDialogBinding
) :
    RecyclerView.Adapter<RecyclerViewAllMessagesWithCompanion.MyViewHolder>() {

    private val listWithRemovedMessages = mutableListOf<UserMessagesWithCompanionDataClass>()
    private val constrainSendMessage = binding.constraintSendMessage
    private val constrainDeleteMessage = binding.constraintDeleteMessage
    private var flagRemove = false
    private val delete = binding.delete


    class MyViewHolder(itemView: View): ViewHolder(itemView) {
        val dateMessageCompanion: TextView = itemView.findViewById(R.id.dateMessageCompanion)
        val messageCompanion: TextView = itemView.findViewById(R.id.messageCompanion)
        val dateMessageCurrentUser: TextView = itemView.findViewById(R.id.dateMessageCurrentUser)
        val messageCurrentUser: TextView = itemView.findViewById(R.id.messageCurrentUser)
        val selectCurrentUser: ImageView = itemView.findViewById(R.id.selectCurrentUser)
        val selectCompanion: ImageView = itemView.findViewById(R.id.selectCompanion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_dialog, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userMessagesWithCompanion = list[position]
        if (userMessagesWithCompanion.sender == USER_ID_O) {////////////Если сообщение от нас
            setMessage(holder.dateMessageCompanion, holder.messageCompanion, holder.dateMessageCurrentUser, holder.messageCurrentUser, userMessagesWithCompanion.timestamp, userMessagesWithCompanion.message, holder.selectCurrentUser, userMessagesWithCompanion)
        }
        else {//////////////////Если от собеседника
            setMessage(holder.dateMessageCurrentUser, holder.messageCurrentUser, holder.dateMessageCompanion, holder.messageCompanion, userMessagesWithCompanion.timestamp, userMessagesWithCompanion.message, holder.selectCompanion, userMessagesWithCompanion)
        }
        //////////////Удаляем сообщения
        delete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                removeSelectMessages(listWithRemovedMessages)
            }
        }
    }
    private fun setMessage (
        goneDataMessage: TextView,
        goneMessage: TextView,
        dateMessage: TextView,
        messageView: TextView,
        timeStamp: Long,
        message: String,
        selectView: ImageView,
        userMessagesWithCompanion: UserMessagesWithCompanionDataClass
    ) {
        goneDataMessage.visibility = View.GONE
        goneMessage.visibility = View.GONE
        dateMessage.text = getDateFormat(timeStamp)
        messageView.text = message
        messageView.setOnLongClickListener {
            flagRemove = true
            selectView.visibility = View.VISIBLE
            listWithRemovedMessages.add(userMessagesWithCompanion)
            if(constrainSendMessage.visibility == View.VISIBLE) {
                constrainSendMessage.visibility = View.GONE
                constrainDeleteMessage.visibility = View.VISIBLE
            }
            true
        }
        messageView.setOnClickListener {
            if (flagRemove && selectView.visibility == View.VISIBLE) {
                selectView.visibility = View.GONE
                listWithRemovedMessages.remove(userMessagesWithCompanion)
            }else if (flagRemove && selectView.visibility == View.GONE) {
                selectView.visibility = View.VISIBLE
                listWithRemovedMessages.add(userMessagesWithCompanion)
            }
        }
    }

}