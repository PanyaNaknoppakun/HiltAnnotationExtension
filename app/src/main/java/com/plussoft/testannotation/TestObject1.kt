package com.plussoft.testannotation

import com.pls.annotation.GenerateProvidesHiltModule
import dagger.hilt.components.SingletonComponent

@GenerateProvidesHiltModule(installIn = SingletonComponent::class)
object TestObject1 {
    fun getDummyObject1(): String {
        return "DummyObject1"
    }
}