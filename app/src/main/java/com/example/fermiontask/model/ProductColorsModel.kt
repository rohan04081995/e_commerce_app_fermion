package com.example.fermiontask.model

import android.os.Parcel
import android.os.Parcelable

data class ProductColorsModel(val colorName: String?, val colorCode: String?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(colorName)
        parcel.writeString(colorCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductColorsModel> {
        override fun createFromParcel(parcel: Parcel): ProductColorsModel {
            return ProductColorsModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductColorsModel?> {
            return arrayOfNulls(size)
        }
    }
}
