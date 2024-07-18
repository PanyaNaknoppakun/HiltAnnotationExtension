package com.plussoft.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateEntryPointHiltModule
