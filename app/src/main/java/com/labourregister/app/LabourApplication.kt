package com.labourregister.app

import android.app.Application
import com.labourregister.app.data.LabourDatabase
import com.labourregister.app.data.LabourRepository

class LabourApplication : Application() {

    val database: LabourDatabase by lazy {
        LabourDatabase.getDatabase(this)
    }

    val repository: LabourRepository by lazy {
        LabourRepository(database.labourerDao())
    }
}
