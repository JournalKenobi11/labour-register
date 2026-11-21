package com.labourregister.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "shopName")
    val shopName: String = "",

    @ColumnInfo(name = "contactName")
    val contactName: String = "",

    @ColumnInfo(name = "mobileNumber")
    val mobileNumber: String = "",

    @ColumnInfo(name = "location")
    val location: String = "",

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * True if all text fields are blank.
     */
    val isBlank: Boolean
        get() = shopName.isBlank() &&
                contactName.isBlank() &&
                mobileNumber.isBlank() &&
                location.isBlank() &&
                notes.isBlank()

    /**
     * Best primary title for the shop card or dialog header.
     */
    val displayTitle: String
        get() = when {
            shopName.isNotBlank() -> shopName.trim()
            contactName.isNotBlank() -> contactName.trim()
            location.isNotBlank() -> location.trim()
            else -> "Shop #$id"
        }
}
