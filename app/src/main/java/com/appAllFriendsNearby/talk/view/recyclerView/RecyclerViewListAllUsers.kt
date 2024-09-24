package com.appAllFriendsNearby.talk.view.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appAllFriendsNearby.talk.R
import com.appAllFriendsNearby.talk.dataBase.USERS
import com.appAllFriendsNearby.talk.dataBase.USER_PHOTO
import com.appAllFriendsNearby.talk.dataBase.currentUser
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.dataBase.database
import com.appAllFriendsNearby.talk.dataBase.getStorageUserProfilePhoto
import com.appAllFriendsNearby.talk.dataBase.storage
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecyclerViewListAllUsers(private val cardUserDataClasses: MutableList<CardUserDataClass>) : RecyclerView.Adapter<RecyclerViewListAllUsers.MyViewHolder>() {



    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhoto)
        val userName: TextView = itemView.findViewById(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_user_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardUserDataClasses.size
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userData = cardUserDataClasses[position]
        holder.userName.text = userData.userName
        CoroutineScope(Dispatchers.Main).launch {
            val urlUserPhoto = async {getStorageUserProfilePhoto(userData.userId)}.await()
            setUserPhoto(holder.userPhoto, urlUserPhoto, holder.progressBar)
        }
    }
    private fun setUserPhoto(
        viewUserPhoto: ImageView,
        userPhotoUrl: String,
        progressBar: ProgressBar
    ) {
        Picasso.get().load(userPhotoUrl).into(viewUserPhoto, object : Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
                viewUserPhoto.visibility = View.VISIBLE
            }

            override fun onError(e: Exception) {
                showToast(progressBar.context, R.string.pictureLoadFailure)
            }
        })
    }
}