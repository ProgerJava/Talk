package com.appAllFriendsNearby.talk.tools.generalStaticFunction

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.appAllFriendsNearby.talk.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.Date

fun showToast (context: Context, id: Int) {
    Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show()
}
fun showToast (context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
/////////////////Подгружаем фото
suspend fun setUserPhoto(url: String, progressBar: ProgressBar, userPhoto: ImageView) = coroutineScope {
    Picasso.get().load(url).into(userPhoto, object : Callback {
        override fun onSuccess() {
            progressBar.visibility = View.GONE
            userPhoto.visibility = View.VISIBLE
        }

        override fun onError(e: Exception) {
            showToast(userPhoto.context, R.string.pictureLoadFailure)
        }
    })
}
/////////////////////////Устанавливаем формат времени
@SuppressLint("SimpleDateFormat")
fun getDateFormat(timestamp: Long): String {
    val dateMessage = Date(timestamp)
    val dateNow = Date()
    var sdf = SimpleDateFormat("d/M/yyyy HH:mm")
    if (dateMessage.month == dateNow.month
        && dateMessage.year == dateNow.year
        && dateMessage.day == dateNow.day) {
        sdf = SimpleDateFormat("HH:mm")
    }

    return sdf.format(Date(timestamp)).toString()
}
