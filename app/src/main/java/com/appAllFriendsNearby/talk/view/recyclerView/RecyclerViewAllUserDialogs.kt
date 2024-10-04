package com.appAllFriendsNearby.talk.view.recyclerView

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserDialogsDataClass
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.getDateFormat
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.setUserPhoto
import com.appAllFriendsNearby.talk.view.OnClickRecyclerViewItemUsersOrDialogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecyclerViewAllUserDialogs(
    private val userDialogsDataClasses: MutableList<UserDialogsDataClass>,
    private val onClickRecyclerViewItemUsers: OnClickRecyclerViewItemUsersOrDialogs
) : RecyclerView.Adapter<RecyclerViewAllUserDialogs.MyViewHolder>() {


    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhoto)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
        val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint)
        val timeSend: TextView = itemView.findViewById(R.id.timeSend)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_user_chats, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = userDialogsDataClasses.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dialog = userDialogsDataClasses[position]
        CoroutineScope(Dispatchers.Main).launch {
            setUserPhoto(dialog.userPhoto, holder.progressBar, holder.userPhoto)
        }
        holder.userName.text = dialog.userName
        if (dialog.userSender == USER_ID_O) {
            val spannableString = SpannableString("You: ${dialog.userLastMessage}")
            spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, "You:".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.lastMessage.text = spannableString
        }else {
            holder.lastMessage.text = dialog.userLastMessage
        }
        holder.constraint.setOnClickListener {
            onClickRecyclerViewItemUsers.itemClick(dialog.userCompanion)
        }
        holder.timeSend.text = getDateFormat(dialog.timestamp)
    }
}