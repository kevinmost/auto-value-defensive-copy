package com.kevinmost.auto.value.defensive_copy.processor.util

import com.google.auto.value.extension.AutoValueExtension.Context
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

val Context.autoValueProperties: List<AutoValueProperty>
  get() = properties().map {
    val (name: String, element: ExecutableElement) = it
    AutoValueProperty(
        name = name,
        returnType = element.returnType.asTypeName,
        modifiers = element.modifiers,
        _annotations = element.annotationMirrors
            .map { mirror -> WrappedAnnotation<Annotation>(processingEnvironment(), mirror) }
            .associateBy { wrapped -> wrapped.annotationType }
    )
  }

data class AutoValueProperty internal constructor(
    val name: String,
    val returnType: TypeName,
    val modifiers: Set<Modifier>,
    private val _annotations: Map<ClassName, WrappedAnnotation<*>>
) {
  val annotations: List<WrappedAnnotation<*>> = _annotations.values.toList()


  @Suppress("UNCHECKED_CAST")
  operator fun <T : Annotation> get(annotationType: ClassName): WrappedAnnotation<T>? = _annotations[annotationType] as WrappedAnnotation<T>?

  operator fun <T : Annotation> get(annotationType: KClass<T>): WrappedAnnotation<T>? = get(annotationType.java)

  operator fun <T : Annotation> get(annotationType: Class<T>): WrappedAnnotation<T>? = get(ClassName.get(annotationType))

  override fun toString(): String = buildString {
    appendln("${annotations.joinToString(separator = "\n")} ${modifiers.joinToString(separator = " ")} $returnType $name()")
  }
}
