package com.kevinmost.auto.value.defensive_copy.processor.util

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.element.Modifier

fun JavaFile.modify(block: JavaFile.Builder.() -> Unit): JavaFile = toBuilder().apply(block).build()
fun AnnotationSpec.modify(block: AnnotationSpec.Builder.() -> Unit): AnnotationSpec = toBuilder().apply(block).build()
fun ParameterSpec.modify(block: ParameterSpec.Builder.() -> Unit): ParameterSpec = toBuilder().apply(block).build()
fun FieldSpec.modify(block: FieldSpec.Builder.() -> Unit): FieldSpec = toBuilder().apply(block).build()
fun MethodSpec.modify(block: MethodSpec.Builder.() -> Unit): MethodSpec = toBuilder().apply(block).build()

operator fun TypeSpec.Builder.plusAssign(member: FieldSpec) {
  addField(member)
}

operator fun TypeSpec.Builder.plusAssign(member: MethodSpec) {
  addMethod(member)
}

operator fun MethodSpec.Builder.plusAssign(codeBlock: CodeBlock) {
  addCode(codeBlock)
}

operator fun AnnotationSpec.Builder.plusAssign(member: Pair<String, CodeBlock>) {
  addMember(member.first, member.second)
}

fun codeBlock(block: CodeBlockBuilder.() -> String): CodeBlock {
  val builder = CodeBlockBuilder()
  val str = block(builder)
  return builder.build(str)
}

class CodeBlockBuilder internal constructor() {
  private val types = mutableListOf<TypeName>()

  fun TypeName.type(): String {
    types += this
    return "\$T"
  }

  fun build(str: String): CodeBlock {
    return CodeBlock.of(str, *types.toTypedArray())
  }
}

fun buildClassFile(
    packageName: String,
    className: String,
    superClass: String,
    modifiers: Set<Modifier> = setOf(Modifier.FINAL),
    modifyFileBlock: JavaFile.Builder.() -> Unit = {},
    modifyTypeBlock: TypeSpec.Builder.() -> Unit
): JavaFile = JavaFile.builder(packageName, TypeSpec.classBuilder(className)
    .addModifiers(*modifiers.toTypedArray())
    .superclass(ClassName.get(packageName, superClass))
    .apply(modifyTypeBlock)
    .build()
).apply(modifyFileBlock).build()

fun buildAnnotation(
    type: ClassName,
    members: Map<String, CodeBlock> = emptyMap()
): AnnotationSpec = AnnotationSpec.builder(type)
    .apply { members.forEach { addMember(it.key, it.value) } }
    .build()

fun buildParam(
    name: String,
    type: TypeName,
    modifiers: List<Modifier> = emptyList(),
    annotations: List<AnnotationSpec> = emptyList()
): ParameterSpec = ParameterSpec.builder(type, name, *modifiers.toTypedArray())
    .addAnnotations(annotations)
    .build()

fun buildField(
    name: String,
    type: TypeName,
    modifiers: List<Modifier> = emptyList(),
    annotations: List<AnnotationSpec> = emptyList(),
    initializer: CodeBlock? = null
): FieldSpec = FieldSpec.builder(type, name, *modifiers.toTypedArray())
    .addAnnotations(annotations)
    .apply {
      if (initializer != null) {
        initializer(initializer)
      }
    }
    .build()

fun buildMethod(
    annotations: List<AnnotationSpec> = emptyList(),
    modifiers: List<Modifier> = emptyList(),
    typeVariables: List<TypeVariableName> = emptyList(),
    name: String,
    returns: TypeName,
    params: List<ParameterSpec> = emptyList(),
    exceptions: List<TypeName> = emptyList(),
    block: MethodSpec.Builder.() -> Unit
): MethodSpec = MethodSpec.methodBuilder(name)
    .addAnnotations(annotations)
    .addExceptions(exceptions)
    .addTypeVariables(typeVariables)
    .addModifiers(modifiers)
    .returns(returns)
    .addParameters(params)
    .apply(block)
    .build()

fun buildConstructor(
    annotations: List<AnnotationSpec> = emptyList(),
    modifiers: List<Modifier> = emptyList(),
    typeVariables: List<TypeVariableName> = emptyList(),
    params: List<ParameterSpec> = emptyList(),
    exceptions: List<TypeName> = emptyList(),
    block: MethodSpec.Builder.() -> Unit
): MethodSpec = MethodSpec.constructorBuilder()
    .addAnnotations(annotations)
    .addExceptions(exceptions)
    .addTypeVariables(typeVariables)
    .addModifiers(modifiers)
    .addParameters(params)
    .apply(block)
    .build()

inline fun MethodSpec.Builder.addControlFlow(
    statement: String,
    args: List<String> = emptyList(),
    block: MethodSpec.Builder.() -> Unit
) {
  beginControlFlow(statement, *args.toTypedArray())
  block(this)
  endControlFlow()
}

inline internal fun MethodSpec.Builder.addMultiline(
    firstLine: String,
    lastLine: String,
    block: MultilineCodeBuilder.() -> Unit
) {
  this += MultilineCodeBuilder(firstLine, lastLine).apply { block(this) }.build()
}

internal class MultilineCodeBuilder(private val firstLine: String, private val lastLine: String) {

  private val lines = mutableListOf<Pair<String, Array<out String>>>()

  fun addIndentedStatement(code: String, vararg args: String) {
    lines += code to args
  }

  internal fun build(): CodeBlock {
    return CodeBlock.builder()
        .add(firstLine)
        .add("\n")
        .add("\$>\$>")
        .add("\$[")
        .apply {
          lines.forEach { line ->
            add(line.first, *line.second)
            add("\n")
          }
        }
        .add("\$]")
        .add("\$<\$<")
        .add(lastLine)
        .add("\n")
        .build()
  }
}

