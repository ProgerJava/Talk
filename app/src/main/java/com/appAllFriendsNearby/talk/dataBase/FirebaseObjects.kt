package com.appAllFriendsNearby.talk.dataBase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

////////////////////////////Объект аутентификации
val auth by lazy {Firebase.auth}
/////////////////////////////Объект базы данных
val DATABASE_O = Firebase.database.reference
/////////////////////////////Объект хранилища
val STORAGE_O = FirebaseStorage.getInstance().reference
var currentUser = auth.currentUser
lateinit var USER_ID_O: String