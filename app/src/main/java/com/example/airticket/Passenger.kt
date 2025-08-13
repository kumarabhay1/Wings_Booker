package com.example.airticket.model

import android.os.Parcel
import android.os.Parcelable

data class Passenger(
    val fullName: String,
    val email: String,
    val phone: String,
    val age: String,
    val gender: String,
    val nationality: String,
    val from: String,   // Added
    val to: String      // Added
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",    // Read 'from'
        parcel.readString() ?: ""     // Read 'to'
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(age)
        parcel.writeString(gender)
        parcel.writeString(nationality)
        parcel.writeString(from)   // Write 'from'
        parcel.writeString(to)     // Write 'to'
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Passenger> {
        override fun createFromParcel(parcel: Parcel): Passenger = Passenger(parcel)
        override fun newArray(size: Int): Array<Passenger?> = arrayOfNulls(size)
    }
}
