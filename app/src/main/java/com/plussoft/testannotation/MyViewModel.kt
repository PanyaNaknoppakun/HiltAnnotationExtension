package com.plussoft.testannotation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository,
    @ApplicationContext private val appContext: Context,
    private val testObject2: TestObject2) : ViewModel() {

    @Inject
    lateinit var testForProvideObject: TestForProvideObject

    fun doSomething() {
        myRepository.doSomething()
        val status = NoAndroid(appContext).getStatusTest()
        val status2 = NoAndroid2(appContext).getStatusTest()
        val dummyTestObject1 = testForProvideObject.getDummyTestObject1()
        Log.d("MyViewModel", "dummyTestObject1: $dummyTestObject1")
        Log.d("MyViewModel", "status: $status , status2: $status2")
    }
}