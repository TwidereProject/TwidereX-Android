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

            val route = generateRoute(declaration = node)
            if (route !is NestedRouteDefinition) {
                return
            }

            val packageName = node.packageName.asString()
            val routeClassName = "${node.qualifiedName?.getShortName()}Route"
            val definitionClassName = "${node.qualifiedName?.getShortName()}RouteDefinition"
            val dependencies = Dependencies(
                true,
                *(data.mapNotNull { it.containingFile } + listOfNotNull(node.containingFile)).toTypedArray()
            )
            generateFile(
                dependencies,
                packageName,
                routeClassName,
                route.copy(name = routeClassName).generateRoute()
            )
            generateFile(
                dependencies,
                packageName,
                definitionClassName,
                route.copy(name = definitionClassName).generateDefinition()
            )
        }

        private fun generateFile(
            dependencies: Dependencies,
            packageName: String,
            className: String,
            content: String
        ) {
            codeGenerator.createNewFile(
                dependencies,
                packageName,
                className
            ).use { outputStream ->
                outputStream.appendLine("package $packageName")
                outputStream.appendLine()
                outputStream.appendLine(content)
            }
        }

        private fun generateRoute(
            declaration: KSDeclaration,
            parent: RouteDefinition? = null
        ): RouteDefinition {
            val name = declaration.simpleName.getShortName()
            return when (declaration) {
                is KSClassDeclaration -> {
                    NestedRouteDefinition(
                        name = name,
                        parent = parent,
                    ).also { nestedRouteDefinition ->
                        nestedRouteDefinition.childRoute.addAll(
                            declaration.declarations.map {
                                generateRoute(it, nestedRouteDefinition)
                            }
                        )
                    }
                }
                is KSPropertyDeclaration -> {
                    ConstRouteDefinition(name, parent)
                }
                is KSFunctionDeclaration -> {
                    FunctionRouteDefinition(
                        name = name,
                        parent = parent,
                        parameters = declaration.parameters.map {
                            val parameterName = it.name?.getShortName() ?: "_"
                            val parameterType = it.type.resolve()
                            RouteParameter(
                                name = parameterName,
                                type = parameterType.declaration.qualifiedName?.asString()
                                    ?: "<ERROR>",
                                isNullable = parameterType.isMarkedNullable,
                            )
                        },
                    )
                }
                else -> throw NotImplementedError()
            }
        }
    }
}

private fun OutputStream.appendLine(str: String = "") {
    this.write("$str${System.lineSeparator()}".toByteArray())
}
