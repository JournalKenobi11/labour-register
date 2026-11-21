package com.labourregister.app

import android.app.Application
import com.labourregister.app.data.LabourDatabase
import com.labourregister.app.data.LabourRepository
import com.labourregister.app.data.ShopInquiryRepository

class LabourApplication : Application() {

    val database: LabourDatabase by lazy {
        LabourDatabase.getDatabase(this)
    }

    val repository: LabourRepository by lazy {
        LabourRepository(database.labourerDao())
    }

    val shopInquiryRepository: ShopInquiryRepository by lazy {
        ShopInquiryRepository(database.shopInquiryDao())
    }
}
