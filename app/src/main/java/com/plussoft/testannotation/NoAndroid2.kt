package com.plussoft.testannotation

import android.content.Context
import com.plussoft.annotation.GenerateEntryPointHiltModule
import com.plussoft.testannotation.extension.getEntryPoint
import javax.inject.Inject

@GenerateEntryPointHiltModule
class NoAndroid2(context: Context) {
    init {
        context.getEntryPoint(HiltABCEntryPoint::class.java).inject(this)
    }
    @Inject
    lateinit var myRepository: MyRepository

    fun getStatusTest(): String {
        return myRepository.doSomething() + "2"
    }
}