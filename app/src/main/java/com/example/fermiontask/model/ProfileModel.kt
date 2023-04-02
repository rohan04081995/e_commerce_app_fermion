package com.example.fermiontask.model

import android.graphics.Bitmap

data class ProfileModel(
    var phoneNumber: String,
    var emailAddress: String,
    var deliveryAddress: String,
    var bitmap: Bitmap?
)
