HiltAnnotationExtension
=======================

HiltAnnotationExtension is a Kotlin library that simplifies the creation of Hilt modules and entry points using annotations and code generation.

## Installation

Add the following dependencies to your project’s `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.PanyaNaknoppakun:HiltAnnotationExtension:1.0.3")
    kapt("com.github.PanyaNaknoppakun:HiltAnnotationExtension:1.0.3")
}
```

## Annotations
`@GenerateBindHiltModule`
This annotation generates a Hilt module that binds the annotated class to its interface.

### Example
```kotlin
package com.example

import com.pls.annotation.GenerateBindHiltModule
import javax.inject.Inject

interface MyRepository {
    fun doSomething()
}

@GenerateBindHiltModule
class MyRepositoryImpl @Inject constructor() : MyRepository {
    override fun doSomething() {
        // Implementation here
    }
}
```

This will generate the following module:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MyRepositoryHiltModule {
    @Binds
    abstract fun bindMyRepository(myRepositoryImpl: MyRepositoryImpl): MyRepository
}
```

`@GenerateEntryPointHiltModule`
This annotation generates a Hilt entry point for the annotated class.

### Example
```kotlin
package com.example

import com.pls.annotation.GenerateEntryPointHiltModule

@GenerateEntryPointHiltModule
class NoAndroid {
// Implementation here
}
```
This will generate the following entry point:
```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NonAndroid_HiltEntryPoint {
    fun inject(noAndroid: NoAndroid)
}
```
## Usage
1. Annotate Your Classes: Use @GenerateBindHiltModule and @GenerateEntryPointHiltModule on your classes as shown in the examples above.
2. Build Your Project: The annotation processor will generate the necessary Hilt modules and entry points.
3. Use the Generated Code: You can now use the generated modules and entry points in your application.

##  License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
