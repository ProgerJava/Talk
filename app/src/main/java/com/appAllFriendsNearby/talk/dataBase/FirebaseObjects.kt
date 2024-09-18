package com.appAllFriendsNearby.talk.dataBase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

////////////////////////////Объект аутентификации
val auth by lazy {Firebase.auth}
////////////////////////////Текущий пользователь
var currentUser = auth.currentUser
/////////////////////////////Объект базы данных
val database by lazy {Firebase.database.reference}
/////////////////////////////Объект хранилища
val storage by lazy {FirebaseStorage.getInstance().reference}