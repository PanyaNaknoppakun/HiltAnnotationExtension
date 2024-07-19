package com.pls.processor

import com.google.auto.service.AutoService
import com.pls.annotation.GenerateBindHiltModule
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
@SupportedAnnotationTypes("com.pls.annotation.GenerateBindHiltModule")
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
        val componentMap = mutableMapOf(
            "SingletonComponent" to mutableListOf<MethodSpec>(),
            "ActivityRetainedComponent" to mutableListOf<MethodSpec>(),
            "ViewModelComponent" to mutableListOf<MethodSpec>(),
            "ActivityComponent" to mutableListOf<MethodSpec>(),
            "FragmentComponent" to mutableListOf<MethodSpec>(),
            "ViewComponent" to mutableListOf<MethodSpec>(),
            "ViewWithFragmentComponent" to mutableListOf<MethodSpec>(),
            "ServiceComponent" to mutableListOf<MethodSpec>()
        )

        funcList.forEach { (methodSpec, component) ->
            componentMap[component]?.add(methodSpec)
        }

        componentMap.forEach { (component, methodSpecList) ->
            if (methodSpecList.isNotEmpty()) {
                val packageName = if (component == "SingletonComponent") "dagger.hilt.components" else "dagger.hilt.android.components"
                createFile(methodSpecList, component, packageName)
            }
        }
    }

    private fun createFile(methodSpecs : List<MethodSpec>, installIn: String, inPackage: String) {
        val className = "${installIn}_HiltModule"
        val packageName = "com.pls.autohilt"
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
            .addModifiers(Modifier.PUBLIC)
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
}
