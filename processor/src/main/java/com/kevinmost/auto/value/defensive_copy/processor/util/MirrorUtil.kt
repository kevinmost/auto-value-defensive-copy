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
import kotlin.reflect.KProperty1

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

// TODO(Kevin): Does this method work...?
fun ProcessingEnvironment.getMethodBody(element: ExecutableElement): String {
  return Trees.instance(this)
      .getTree(element)
      .body
      .statements
      .joinToString(separator = "\n") { (it as JCTree).toString() }
}

@Suppress("NOTHING_TO_INLINE")
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

  operator fun <R> get(getter: KProperty1<T, R>): R = unsafe(getter)

  // These are special snowflakes; they can't use the generic getter above, because when you use an AnnotationMirror
  // to retrieve a Class<?> or Array<Class<?>>, you get back TypeMirror or Array<TypeMirror>
  operator fun <C : KClass<*>> get(getter: KProperty1<T, C>): TypeName = unsafe<TypeMirror>(getter).asTypeName
  operator fun get(getter: KProperty1<T, Array<KClass<*>>>): Array<TypeName> = unsafe<Array<TypeMirror>>(getter).map { it.asTypeName }.toTypedArray()

  @Suppress("UNCHECKED_CAST")
  private fun <R> unsafe(getter: KProperty1<T, *>): R = elementValues[getter.name] as R
}
