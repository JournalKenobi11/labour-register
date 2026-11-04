package com.labourregister.app

import com.labourregister.app.data.LabourerEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class LabourerEntityTest {

    @Test
    fun testFormattedDailyCharge() {
        val nullCharge = LabourerEntity(id = 1, dailyChargePaise = null)
        assertEquals("-", nullCharge.formattedDailyCharge)

        val wholeRupees = LabourerEntity(id = 2, dailyChargePaise = 50000) // ₹500
        assertEquals("₹500", wholeRupees.formattedDailyCharge)

        val fractionalRupees = LabourerEntity(id = 3, dailyChargePaise = 45050) // ₹450.50
        assertEquals("₹450.50", fractionalRupees.formattedDailyCharge)
    }

    @Test
    fun testDailyChargeRupeesString() {
        val nullCharge = LabourerEntity(id = 1, dailyChargePaise = null)
        assertEquals("", nullCharge.dailyChargeRupeesString)

        val wholeRupees = LabourerEntity(id = 2, dailyChargePaise = 50000)
        assertEquals("500", wholeRupees.dailyChargeRupeesString)

        val fractionalRupees = LabourerEntity(id = 3, dailyChargePaise = 45050)
        assertEquals("450.50", fractionalRupees.dailyChargeRupeesString)
    }
}
