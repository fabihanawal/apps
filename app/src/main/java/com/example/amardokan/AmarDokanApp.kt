package com.example.amardokan

import android.app.Application
import com.example.amardokan.data.AmarDokanDatabase
import com.example.amardokan.repository.AmarDokanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AmarDokanApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AmarDokanDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AmarDokanRepository(database, applicationScope) }
}
