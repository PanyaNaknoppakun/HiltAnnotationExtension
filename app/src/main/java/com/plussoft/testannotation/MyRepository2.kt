package com.plussoft.testannotation

import android.util.Log
import com.pls.annotation.GenerateBindHiltModule
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject


interface MyRepository2 {
    fun doSomething(): String
}

@GenerateBindHiltModule(installIn = ViewModelComponent::class)
class MyRepository2Impl@Inject constructor() : MyRepository2 {
    override fun doSomething(): String {
        Log.d("MyRepository", "doSomething: ")
        return "doSomething"
    }
}