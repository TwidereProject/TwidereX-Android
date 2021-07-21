/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.route.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import java.io.OutputStream

class RouteProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val routeSymbol = resolver
            .getSymbolsWithAnnotation("com.twidere.route.processor.AppRoute")
            .filterIsInstance<KSClassDeclaration>()
        routeSymbol.forEach { it.accept(RouteVisitor(), routeSymbol.toList()) }
        return emptyList()
    }

    inner class RouteVisitor : KSEmptyVisitor<List<KSClassDeclaration>, Unit>() {
        override fun defaultHandler(node: KSNode, data: List<KSClassDeclaration>) {
            if (node !is KSClassDeclaration) {
                return
            }
            val packageName = node.packageName.asString()
            val className = "${node.qualifiedName?.getShortName()}Route"
            codeGenerator.createNewFile(
                Dependencies(
                    true,
                    *(data.mapNotNull { it.containingFile } + listOfNotNull(node.containingFile)).toTypedArray()
                ),
                packageName,
                className
            ).use { outputStream ->
                outputStream.appendLine("package $packageName")
                outputStream.appendLine()
                outputStream.appendLine("import java.net.URLEncoder")
                outputStream.appendLine()
                outputStream.appendLine("public object $className {")

                generateRoute(
                    node.declarations.toList(),
                    outputStream,
                    parentPath = "",
                )

                outputStream.appendLine("}")
            }
        }

        private fun generateRoute(
            declarations: List<KSDeclaration>,
            outputStream: OutputStream,
            parentPath: String = "",
            indent: String = "    ",
        ) {
            declarations.forEach { declaration ->
                val pathName = declaration.simpleName.getShortName()
                when (declaration) {
                    is KSClassDeclaration -> {
                        outputStream.appendLine("${indent}object $pathName {")
                        generateRoute(
                            declaration.declarations.toList(),
                            outputStream,
                            "$parentPath/$pathName",
                            indent + indent,
                        )
                        outputStream.appendLine("$indent}")
                    }
                    is KSFunctionDeclaration -> {
                        val parameterStr = declaration.parameters
                            .joinToString(", ") { parameter ->
                                val name = parameter.name?.getShortName() ?: "_"
                                val type = parameter.type.resolve()
                                    .declaration.qualifiedName?.asString()
                                    .let {
                                        if (parameter.type.resolve().isMarkedNullable) {
                                            "$it? = null"
                                        } else {
                                            it
                                        }
                                    }
                                    ?: "<ERROR>"
                                "$name: $type"
                            }
                        val query = declaration.parameters
                            .filter { it.type.resolve().isMarkedNullable }
                            .joinToString("&") { parameter ->
                                val name = parameter.name?.getShortName() ?: "_"
                                "$name=\$$name"
                            }
                            .let {
                                if (it.isNotEmpty()) {
                                    "?$it"
                                } else {
                                    it
                                }
                            }
                        val path = declaration.parameters
                            .filter { !it.type.resolve().isMarkedNullable }
                            .joinToString("/") { parameter ->
                                val name = parameter.name?.getShortName() ?: "_"
                                "{$name}"
                            }
                            .let {
                                if (it.isNotEmpty()) {
                                    "/$it"
                                } else {
                                    it
                                }
                            }
                        val pathWithParameter = declaration.parameters
                            .filter { !it.type.resolve().isMarkedNullable }
                            .joinToString("/") { parameter ->
                                val name = parameter.name?.getShortName() ?: "_"
                                "\${URLEncoder.encode($name, \"UTF-8\")}"
                            }
                            .let {
                                if (it.isNotEmpty()) {
                                    "/$it"
                                } else {
                                    it
                                }
                            }

                        outputStream.appendLine(
                            "${indent}const val $pathName = \"$parentPath/$pathName$path\""
                        )
                        outputStream.appendLine(
                            "${indent}fun $pathName($parameterStr) = \"$parentPath/$pathName$pathWithParameter${query}\""
                        )
                    }
                    is KSPropertyDeclaration -> {
                        outputStream.appendLine("${indent}const val $pathName = \"$parentPath/${pathName}\"")
                    }
                }
            }
        }
    }
}

private fun OutputStream.appendLine(str: String = "") {
    this.write("$str${System.lineSeparator()}".toByteArray())
}

class RouteProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return RouteProcessor(environment.codeGenerator)
    }
}
