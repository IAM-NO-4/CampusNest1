package com.campusnest1.groupq.di

import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.data.*
import com.campusnest1.groupq.viewmodel.EventViewModel
import com.campusnest1.groupq.viewmodel.HostelViewModel
import com.campusnest1.groupq.viewmodel.auth.registerViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    single {Firebase.auth }

    single { Authrepo() }
    single<AuthRepository> { AuthImplementationRepository(get()) }
    single<HostelRepository> { HostelImplementationRepository(get()) }
    single<EventRepository> { EventImplementationRepository(get()) }

    viewModel { registerViewModel() }
    viewModel { HostelViewModel(get(), get()) }
    viewModel{ EventViewModel(get(), get()) }
}
