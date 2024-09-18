package com.appAllFriendsNearby.talk.tools.generalStaticFunction

import android.content.Context
import android.os.CountDownTimer
import android.widget.Toast

fun showToast (context: Context, id: Int) {
    Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show()
}
fun showToast (context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
