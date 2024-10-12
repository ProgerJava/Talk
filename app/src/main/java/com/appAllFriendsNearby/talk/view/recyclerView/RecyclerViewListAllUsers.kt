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
import com.appAllFriendsNearby.talk.dataBase.dataClass.CardUserDataClass
import com.appAllFriendsNearby.talk.tools.generalStaticFunction.showToast
import com.appAllFriendsNearby.talk.view.OnClickRecyclerViewItemUsersOrDialogs
import com.google.android.material.animation.AnimationUtils.lerp
import com.google.android.material.carousel.MaskableFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecyclerViewListAllUsers(
    private val cardUserDataClasses: MutableList<CardUserDataClass>,
    private val onClickRecyclerViewItemUsersOrDialogs: OnClickRecyclerViewItemUsersOrDialogs

) : RecyclerView.Adapter<RecyclerViewListAllUsers.MyViewHolder>() {

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val userPhoto: ImageView = itemView.findViewById(R.id.userPhoto)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val connect: View = itemView.findViewById(R.id.connectDote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_user_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cardUserDataClasses.size
    }

    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userData = cardUserDataClasses[position]
        val userName = userData.userName
        if (userName.length > 9) {
            holder.userName.text = userName.substring(0, 9) + "…"
        }else {
            holder.userName.text = userName

        }
        CoroutineScope(Dispatchers.Main).launch {
            setUserPhoto(holder.userPhoto, userData.userPhoto, holder.progressBar)
        }
        if (userData.userConnection == true) {
            holder.connect.background = holder.connect.context.getDrawable(R.drawable.style_card_connection)
        }else {
            holder.connect.background = holder.connect.context.getDrawable(R.drawable.style_card_disconnection)
        }
        holder.userPhoto.setOnClickListener {
            onClickRecyclerViewItemUsersOrDialogs.itemClick(userData.userId)
        }
        (holder.itemView as MaskableFrameLayout).setOnMaskChangedListener {
                maskRect ->
            holder.userName.translationX = maskRect.left
            holder.userName.alpha = lerp(1F, 0F, 0F, 80F, maskRect.left)
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