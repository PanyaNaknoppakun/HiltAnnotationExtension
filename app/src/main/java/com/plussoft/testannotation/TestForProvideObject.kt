package com.plussoft.testannotation

import com.pls.annotation.GenerateProvidesHiltModule
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@GenerateProvidesHiltModule(installIn = SingletonComponent::class)
class TestForProvideObject@Inject constructor(
    private val testObject1: TestObject1
) {
    fun getDummyValue(): String {
        return "Dummy"
    }

    fun getDummyTestObject1() : String {
        return testObject1.getDummyObject1()
    }
}
