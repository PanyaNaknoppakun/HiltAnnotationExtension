package com.plussoft.testannotation

import android.content.Context
import com.pls.annotation.GenerateEntryPointHiltModule
import com.pls.autohilt.NonAndroid_HiltEntryPoint
import com.plussoft.testannotation.extension.getEntryPoint
import javax.inject.Inject

@GenerateEntryPointHiltModule
class NoAndroid(context: Context) {
    init {
        context.getEntryPoint(NonAndroid_HiltEntryPoint::class.java).inject(this)
    }
    @Inject
    lateinit var testForProvideObject: TestForProvideObject
    @Inject
    lateinit var myRepository: MyRepository

    fun getStatusTest(): String {
        return myRepository.doSomething()
    }
}
