package com.plussoft.testannotation

import com.pls.annotation.GenerateProvidesHiltModule
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent


@GenerateProvidesHiltModule(installIn = ActivityRetainedComponent::class)
class TestObject2 {
    fun getDummyObject2(): String {
        return "DummyObject2"
    }
}