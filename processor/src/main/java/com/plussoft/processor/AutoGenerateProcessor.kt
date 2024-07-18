package com.plussoft.processor


import com.google.auto.service.AutoService
import com.plussoft.annotation.GenerateBindHiltModule
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dagger.Module
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("com.plussoft.annotation.GenerateBindHiltModule")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class AutoGenerateProcessor : AbstractProcessor() {
    private val generateMyClass = GenerateBindHiltModule::class

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val funcList = roundEnv.getElementsAnnotatedWith(generateMyClass.java)
            .filterIsInstance<TypeElement>()
            .map { typeElement: TypeElement ->
                val installIn =
                    getComponentName(typeElement.getAnnotation(generateMyClass.java).toString())
                Pair(generateHiltModuleWithBinding(typeElement), installIn)
            }

        if (funcList.isNotEmpty()) {
            createClass(funcList)
        }
        return true
    }

    private fun generateHiltModuleWithBinding(element: TypeElement): MethodSpec {
        val interfaceType: TypeMirror = element.interfaces.firstOrNull() ?: element.superclass
        val typeName = TypeName.get(interfaceType)
        val targetClassName = element.asType().toString()
        val targetClass = ClassName.bestGuess(targetClassName)
        val interfaceName: Name = processingEnv.typeUtils.asElement(interfaceType).simpleName
        return MethodSpec.methodBuilder("bind${interfaceName}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(ClassName.get("dagger", "Binds"))
            .addParameter(targetClass, "impl")
            .returns(typeName)
            .build()
    }

    private fun getComponentName(org: String): String {
        val orgList = org.split(".")
        val name = orgList.get(orgList.size - 2)
        return name
    }

    private fun createClass(funcList: List<Pair<MethodSpec, String>>) {
        val singletonComponentMethodSpecList = mutableListOf<MethodSpec>()
        val activityRetainedComponentMethodSpecList = mutableListOf<MethodSpec>()
        val viewModelComponentMethodSpecList = mutableListOf<MethodSpec>()
        val activityComponentMethodSpecList = mutableListOf<MethodSpec>()
        val fragmentComponentMethodSpecList = mutableListOf<MethodSpec>()
        val viewComponentMethodSpecList = mutableListOf<MethodSpec>()
        val viewWithFragmentComponentMethodSpecList = mutableListOf<MethodSpec>()
        val serviceComponentMethodSpecList = mutableListOf<MethodSpec>()
        funcList.forEach {
            when (it.second) {
                "SingletonComponent" -> {
                    singletonComponentMethodSpecList.add(it.first)
                }

                "ActivityRetainedComponent" -> {
                    activityRetainedComponentMethodSpecList.add(it.first)
                }

                "ViewModelComponent" -> {
                    viewModelComponentMethodSpecList.add(it.first)
                }

                "ActivityComponent" -> {
                    activityComponentMethodSpecList.add(it.first)
                }

                "FragmentComponent" -> {
                    fragmentComponentMethodSpecList.add(it.first)
                }

                "ViewComponent" -> {
                    viewComponentMethodSpecList.add(it.first)
                }

                "ViewWithFragmentComponent" -> {
                    viewWithFragmentComponentMethodSpecList.add(it.first)
                }

                "ServiceComponent" -> {
                    serviceComponentMethodSpecList.add(it.first)
                }
            }
        }
        if (singletonComponentMethodSpecList.isNotEmpty()) {
            createFile(singletonComponentMethodSpecList, "SingletonComponent", "dagger.hilt.components")
        }
        if (activityRetainedComponentMethodSpecList.isNotEmpty()) {
            createFile(activityRetainedComponentMethodSpecList, "ActivityRetainedComponent")
        }
        if (viewModelComponentMethodSpecList.isNotEmpty()) {
            createFile(viewModelComponentMethodSpecList, "ViewModelComponent")
        }
        if (activityComponentMethodSpecList.isNotEmpty()) {
            createFile(activityComponentMethodSpecList, "ActivityComponent")
        }
        if (fragmentComponentMethodSpecList.isNotEmpty()) {
            createFile(fragmentComponentMethodSpecList, "FragmentComponent")
        }
        if (viewComponentMethodSpecList.isNotEmpty()) {
            createFile(viewComponentMethodSpecList, "ViewComponent")
        }
        if (viewWithFragmentComponentMethodSpecList.isNotEmpty()) {
            createFile(viewWithFragmentComponentMethodSpecList, "ViewWithFragmentComponent")
        }
        if (serviceComponentMethodSpecList.isNotEmpty()) {
            createFile(serviceComponentMethodSpecList, "ServiceComponent")
        }
    }

    private fun createFile(methodSpecs : List<MethodSpec>, installIn: String, inPackage: String = "dagger.hilt.android.components") {
        val className = "${installIn}HiltModule"
        val packageName = "com.plussoft.testannotation"
        val moduleName = ClassName.get(packageName, className)

        // Create the Hilt module class
        val classSpec = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)
            .addAnnotation(
                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                    .addMember(
                        "value",
                        "\$T.class",
                        ClassName.get(inPackage, installIn)
                    )
                    .build()
            )
            .addMethods(
                methodSpecs
            )
            .build()

        val javaFile = JavaFile.builder(packageName, classSpec)
            .build()

        try {
            javaFile.writeTo(processingEnv.filer)
            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Generated file: ${packageName}.${className}"
            )
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to create file: ${e.message}"
            )
        }
    }

//    private fun generateHiltModuleWithBindingJava(element: TypeElement) {
//        val interfaceType: TypeMirror = element.interfaces.firstOrNull() ?: element.superclass
//        val typeName = TypeName.get(interfaceType)
//        val interfaceName: Name = processingEnv.typeUtils.asElement(interfaceType).simpleName
//        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
//        val className = "${interfaceName}HiltModule"
//        val moduleName = ClassName.get(packageName, className)
//        val targetClassName = element.asType().toString()
//        val targetClass = ClassName.bestGuess(targetClassName)
//
//        // Create the Hilt module class
//        val classSpec = TypeSpec.classBuilder(moduleName)
//            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//            .addAnnotation(Module::class.java)
//            .addAnnotation(
//                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
//                    .addMember(
//                        "value",
//                        "\$T.class",
//                        ClassName.get("dagger.hilt.components", "SingletonComponent")
//                    )
//                    .build()
//            )
//            .addMethod(
//                MethodSpec.methodBuilder("bind${interfaceName}")
//                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//                    .addAnnotation(ClassName.get("dagger", "Binds"))
//                    .addParameter(targetClass, "impl")
//                    .returns(typeName)
//                    .build()
//            )
//            .addOriginatingElement(element)
//            .build()
//
//        val javaFile = JavaFile.builder(packageName, classSpec)
//            .build()
//
//        try {
//            javaFile.writeTo(processingEnv.filer)
//            processingEnv.messager.printMessage(
//                Diagnostic.Kind.NOTE,
//                "Generated file: ${packageName}.${className}"
//            )
//        } catch (e: IOException) {
//            processingEnv.messager.printMessage(
//                Diagnostic.Kind.ERROR,
//                "Failed to create file: ${e.message}"
//            )
//        }
//    }

}
