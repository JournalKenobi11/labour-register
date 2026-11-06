package com.labourregister.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "labourers")
data class LabourerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "skill")
    val skill: String? = null,

    @ColumnInfo(name = "village")
    val village: String? = null,

    @ColumnInfo(name = "daily_charge_paise")
    val dailyChargePaise: Long? = null,

    @ColumnInfo(name = "mobile_number")
    val mobileNumber: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Formats daily charge in rupees for display in the table, e.g., "₹500" or "₹450.50",
     * or "-" if null.
     */
    val formattedDailyCharge: String
        get() = when (dailyChargePaise) {
            null -> "-"
            else -> {
                val rupees = dailyChargePaise / 100.0
                if (dailyChargePaise % 100L == 0L) {
                    "₹${dailyChargePaise / 100L}"
                } else {
                    String.format(Locale.US, "₹%.2f", rupees)
                }
            }
        }

    /**
     * Plain decimal string representation of daily charge in rupees (without currency symbol)
     * used for CSV export and pre-filling the edit cell dialog.
     */
    val dailyChargeRupeesString: String
        get() = when (dailyChargePaise) {
            null -> ""
            else -> {
                val rupees = dailyChargePaise / 100.0
                if (dailyChargePaise % 100L == 0L) {
                    (dailyChargePaise / 100L).toString()
                } else {
                    String.format(Locale.US, "%.2f", rupees)
                }
            }
        }
}
