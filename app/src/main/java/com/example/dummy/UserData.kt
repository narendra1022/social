package com.example.dummy

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    var name: String? = "null",
    var contactNo: String? = "null",
    var userLocation: String? = "null",
    var profilePicUrl: String? = "null",
    var addanothernumber: String? = "null",
    var addEmail: String? = "null"
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(contactNo)
        parcel.writeString(userLocation)
        parcel.writeString(profilePicUrl)
        parcel.writeString(addanothernumber)
        parcel.writeString(addEmail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}
