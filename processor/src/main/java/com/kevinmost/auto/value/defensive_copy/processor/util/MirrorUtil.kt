package com.kevinmost.auto.value.defensive_copy.processor.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.sun.source.util.Trees
import com.sun.tools.javac.tree.JCTree
import java.lang.reflect.Type
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

val TypeMirror.asTypeName: TypeName
  get() = TypeName.get(this)
val Type.asTypeName: TypeName
  get() = TypeName.get(this)
val TypeElement.asTypeName: TypeName
  get() = asType().asTypeName
val Class<*>.asTypeName: ClassName
  get() = ClassName.get(this)
val KClass<*>.asTypeName: ClassName
  get() = ClassName.get(this.java)

val TypeName.rawType: TypeName
  get() = (this as? ParameterizedTypeName)?.rawType ?: this

fun ProcessingEnvironment.getMethodBody(element: ExecutableElement): String {
  return Trees.instance(this)
      .getTree(element)
      .body
      .statements
      .joinToString(separator = "\n") { (it as JCTree).toString() }
}

// Hides the ugliness of MirroredTypeExceptions from the user
class WrappedAnnotation<T : Annotation> internal constructor(
    processingEnvironment: ProcessingEnvironment,
    mirror: AnnotationMirror
) {

  // rawType is safe to use here because annotation classes can't have type-params
  val annotationType: ClassName = mirror.annotationType.asTypeName as ClassName

  val elementValues: Map<String, Any?> = processingEnvironment.elementUtils.getElementValuesWithDefaults(
      mirror)
      .map { Pair(it.key.simpleName.toString(), it.value.value) }
      .toMap()

  val isNullityAnnotation = annotationType.simpleName() in setOf("NotNull", "NonNull", "Nullable")

  operator fun get(parameterName: String): Any? = elementValues[parameterName]
}
