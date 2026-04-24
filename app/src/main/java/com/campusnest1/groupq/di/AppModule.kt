package com.campusnest1.groupq.di

import com.campusnest1.groupq.data.HostelRepository
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { com.google.firebase.firestore.FirebaseFirestore.getInstance() }

    // Repository
    single { HostelRepository(get()) }
}