package com.plussoft.testannotation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(private val myRepository: MyRepository) : ViewModel() {
    fun doSomething() {
        myRepository.doSomething()
    }
}