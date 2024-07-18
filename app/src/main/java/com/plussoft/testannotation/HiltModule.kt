package com.plussoft.testannotation

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class HiltModule {
//    @Binds
//    abstract fun bindMyRepository(`impl`: MyRepositoryImpl) : MyRepository
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//class ProvideEntryPoint {
//
//    @Singleton
//    @Provides
//    fun provideContext(): Context {
//        return Application().applicationContext
//
//    }
//}
//
//
//@EntryPoint
//@InstallIn(SingletonComponent::class)
//interface HiltABCEntryPoint2 {
//    public fun inject(className: NoAndroid) {
//    }
//
//    public fun inject(className: NoAndroid2) {
//    }
//}