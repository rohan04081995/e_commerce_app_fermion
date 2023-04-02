package com.example.fermiontask.model

import android.os.Parcel
import android.os.Parcelable

data class ProductModel(
    val productName: String?,
    val productDescription: String?,
    val productImage: String?,
    val price: Int,
    val sizesList: ArrayList<String> = ArrayList(),
    val productColorsList: ArrayList<ProductColorsModel> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.createStringArrayList() as ArrayList<String>,
        parcel.createTypedArrayList(ProductColorsModel.CREATOR) as ArrayList<ProductColorsModel>
//        TODO("sizesList"),
//        TODO("productColorsList")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeString(productImage)
        parcel.writeInt(price)
        parcel.writeStringList(sizesList)
        parcel.writeTypedList(productColorsList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductModel> {
        override fun createFromParcel(parcel: Parcel): ProductModel {
            return ProductModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductModel?> {
            return arrayOfNulls(size)
        }
    }

}