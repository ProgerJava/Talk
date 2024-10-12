package com.appAllFriendsNearby.talk.view.recyclerView


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.dataClass.UserMessagesWithCompanionDataClass
import com.appAllFriendsNearby.talk.dataBase.USER_ID_O
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.getDateFormat
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.setUserPhoto
import com.appAllFriendsNearby.talk.view.OnClickRemoveItem
import com.appAllFriendsNearby.talk.view.OnLongTouchRecyclerViewItemDelete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecyclerViewAllMessagesWithCompanion(
    private val list: List<UserMessagesWithCompanionDataClass>,
    private val onLongTouchRecyclerViewItemDelete: OnLongTouchRecyclerViewItemDelete,
    private val onClickRemoveItem: OnClickRemoveItem
) :
    RecyclerView.Adapter<RecyclerViewAllMessagesWithCompanion.MyViewHolder>() {

    private var flagRemove = false


    class MyViewHolder(itemView: View): ViewHolder(itemView) {
        val dateMessageCompanion: TextView = itemView.findViewById(R.id.dateMessageCompanion)
        val messageCompanion: TextView = itemView.findViewById(R.id.messageCompanion)
        val dateMessageCurrentUser: TextView = itemView.findViewById(R.id.dateMessageCurrentUser)
        val messageCurrentUser: TextView = itemView.findViewById(R.id.messageCurrentUser)
        val selectCurrentUser: ImageView = itemView.findViewById(R.id.selectCurrentUser)
        val selectCompanion: ImageView = itemView.findViewById(R.id.selectCompanion)
        val constraintCompanion: ConstraintLayout = itemView.findViewById(R.id.constraintCompanion)
        val constraintCurrent: ConstraintLayout = itemView.findViewById(R.id.constraintCurrent)
        val cardViewImageCompanionUser: CardView = itemView.findViewById(R.id.cardViewImageCompanionUser)
        val cardViewImageCurrentUser: CardView = itemView.findViewById(R.id.cardViewImageCurrentUser)
        val imageCompanionUser: ImageView = itemView.findViewById(R.id.imageCompanionUser)
        val imageCurrentUser: ImageView = itemView.findViewById(R.id.imageCurrentUser)
        val selectCurrentUserImage: ImageView = itemView.findViewById(R.id.selectCurrentUserImage)
        val selectCompanionImage: ImageView = itemView.findViewById(R.id.selectCompanionImage)
        val progressBarImageCompanion: ProgressBar = itemView.findViewById(R.id.progressBarImageCompanion)
        val progressBarImageCurrent: ProgressBar = itemView.findViewById(R.id.progressBarImageCurrent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_dialog, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userMessagesWithCompanion = list[position]
        if (userMessagesWithCompanion.sender == USER_ID_O) {////////////Если сообщение от нас
            setMessage(
                holder.constraintCurrent,
                holder.constraintCompanion,
                holder.dateMessageCurrentUser,
                holder.messageCurrentUser,
                userMessagesWithCompanion.timestamp,
                userMessagesWithCompanion.message,
                holder.selectCurrentUser,
                userMessagesWithCompanion,
                holder.cardViewImageCurrentUser,
                holder.imageCurrentUser,
                holder.selectCurrentUserImage,
                holder.progressBarImageCurrent
            )
        } else {//////////////////Если от собеседника
            setMessage(
                holder.constraintCompanion,
                holder.constraintCurrent,
                holder.dateMessageCompanion,
                holder.messageCompanion,
                userMessagesWithCompanion.timestamp,
                userMessagesWithCompanion.message,
                holder.selectCompanion,
                userMessagesWithCompanion,
                holder.cardViewImageCompanionUser,
                holder.imageCompanionUser,
                holder.selectCompanionImage,
                holder.progressBarImageCompanion
            )
        }
    }
    private fun setMessage (
        constraintVisible: ConstraintLayout,
        constraintGone: ConstraintLayout,
        dateMessage: TextView,
        messageView: TextView,
        timeStamp: Long,
        message: String,
        selectView: ImageView,
        userMessagesWithCompanion: UserMessagesWithCompanionDataClass,
        cardView: CardView,
        image: ImageView,
        selectViewImage: ImageView,
        imageProgressBar: ProgressBar
    ) {
        constraintVisible.visibility = View.VISIBLE
        constraintGone.visibility = View.GONE
        dateMessage.text = getDateFormat(timeStamp)
        if (URLUtil.isValidUrl(message)) {
            messageView.visibility = View.GONE
            cardView.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                setUserPhoto(message, imageProgressBar, image)
            }
        }else {
            messageView.text = message
        }
        cardView.setOnLongClickListener {
            onLongClick(selectViewImage, userMessagesWithCompanion)
        }
        messageView.setOnLongClickListener {
            onLongClick(selectView, userMessagesWithCompanion)
        }
        messageView.setOnClickListener {
            onClick(selectView, userMessagesWithCompanion)
        }
        cardView.setOnClickListener {
            onClick(selectViewImage, userMessagesWithCompanion)
        }
    }
    private fun onClick(
        selectView: ImageView,
        userMessagesWithCompanion: UserMessagesWithCompanionDataClass
    ) {
        if (flagRemove && selectView.visibility == View.VISIBLE) {
            selectView.visibility = View.GONE
            onClickRemoveItem.removeItem(userMessagesWithCompanion)
        }else if (flagRemove && selectView.visibility == View.GONE) {
            selectView.visibility = View.VISIBLE
            onClickRemoveItem.addItem(userMessagesWithCompanion)
        }
    }
    private fun onLongClick(
        selectView: ImageView,
        userMessagesWithCompanion: UserMessagesWithCompanionDataClass
    ): Boolean {
        flagRemove = true
        onLongTouchRecyclerViewItemDelete.longItemClick(true)
        selectView.visibility = View.VISIBLE
        onClickRemoveItem.addItem(userMessagesWithCompanion)
        return true
    }
}
