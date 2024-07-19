package com.plussoft.testannotation

import android.util.Log
import com.pls.annotation.GenerateBindHiltModule
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

interface MyRepository {
    fun doSomething(): String
}

@GenerateBindHiltModule(installIn = SingletonComponent::class)
class MyRepositoryImpl@Inject constructor() : MyRepository {
    override fun doSomething(): String {
        Log.d("MyRepository", "doSomething: ")
        return "doSomething"
    }
}