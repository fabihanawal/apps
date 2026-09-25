package com.example.amardokan

import android.app.Application
import com.example.amardokan.data.local.AmarDokanDatabase
import com.example.amardokan.data.repository.AmarDokanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AmarDokanApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AmarDokanDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AmarDokanRepository(database) }
}
