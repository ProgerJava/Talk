package com.appAllFriendsNearby.talk.tools.generalStaticFunction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
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
    if (dateMessage.year == dateNow.year
        && dateMessage.month == dateNow.month
        && dateMessage.day == dateNow.day) {
        sdf = SimpleDateFormat("HH:mm")
    }else if (dateMessage.month == dateNow.month
        && dateMessage.year == dateNow.year) {
        sdf = SimpleDateFormat("d/M HH:mm")
    }

    return sdf.format(Date(timestamp)).toString()
}
fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

