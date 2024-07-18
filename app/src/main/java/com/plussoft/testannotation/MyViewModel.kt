package com.plussoft.testannotation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(private val myRepository: MyRepository, @ApplicationContext private val appContext: Context) : ViewModel() {

    fun doSomething() {
        myRepository.doSomething()
        val status = NoAndroid(appContext).getStatusTest()
        val status2 = NoAndroid2(appContext).getStatusTest()
        Log.d("MyViewModel", "status: $status , status2: $status2")
    }
}