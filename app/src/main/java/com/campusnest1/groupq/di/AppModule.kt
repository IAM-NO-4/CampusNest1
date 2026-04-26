package com.campusnest1.groupq.di

import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.data.HostelImplementationRepository
import com.campusnest1.groupq.data.HostelRepository
import com.campusnest1.groupq.viewmodel.HostelViewModel
import com.campusnest1.groupq.viewmodel.auth.registerViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    single { Authrepo() }
    single<HostelRepository> { HostelImplementationRepository(get()) }
    viewModel { registerViewModel() }
    viewModel { HostelViewModel(get(), get()) }
}
