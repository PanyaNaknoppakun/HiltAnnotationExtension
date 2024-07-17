package com.plussoft.testannotation

import android.util.Log
import com.plussoft.annotation.GenerateBindHiltModule
import javax.inject.Inject

interface MyRepository {
    fun doSomething()
}

@GenerateBindHiltModule(type = MyRepository::class)
class MyRepositoryImpl@Inject constructor() : MyRepository {
    override fun doSomething() {
        Log.d("MyRepository", "doSomething: ")
    }
}