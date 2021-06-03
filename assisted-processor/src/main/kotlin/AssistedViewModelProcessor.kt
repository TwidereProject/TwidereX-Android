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
package com.twidere.assisted

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import java.io.OutputStream

private fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

class AssistedViewModelProcessor(
    val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val factorySymbol =
            resolver.getSymbolsWithAnnotation("dagger.assisted.AssistedFactory")
        val holder =
            resolver.getSymbolsWithAnnotation("dagger.hilt.android.AndroidEntryPoint")
        val factory = factorySymbol.filterIsInstance<KSClassDeclaration>().toList()
        holder.forEach { it.accept(HolderVisitor(), factory) }
        return emptyList()
    }

    inner class HolderVisitor : KSEmptyVisitor<List<KSClassDeclaration>, Unit>() {
        override fun defaultHandler(node: KSNode, data: List<KSClassDeclaration>) {
            if (node !is KSClassDeclaration) {
                return
            }
            val packageName = node.packageName.asString()
            val className = "${node.qualifiedName?.getShortName()}AssistedViewModelHolder"
            codeGenerator.createNewFile(
                Dependencies(
                    true,
                    *(data.mapNotNull { it.containingFile } + listOfNotNull(node.containingFile)).toTypedArray()
                ),
                packageName,
                className
            ).use { outputStream ->
                outputStream.appendText("package $packageName${System.lineSeparator()}")
                outputStream.appendText(System.lineSeparator())
                outputStream.appendText("public class $className @javax.inject.Inject constructor(${System.lineSeparator()}")

                data.forEach {
                    val name = it.qualifiedName?.asString() ?: "<ERROR>"
                    outputStream.appendText(
                        "    ${
                        name.replace(
                            ".",
                            "_"
                        )
                        }: $name,${System.lineSeparator()}"
                    )
                }

                outputStream.appendText(") {${System.lineSeparator()}")

                outputStream.appendText("    val factory = listOf(${System.lineSeparator()}")
                data.forEach {
                    val name = it.qualifiedName?.asString() ?: "<ERROR>"
                    outputStream.appendText(
                        "        ${
                        name.replace(
                            ".",
                            "_"
                        )
                        },${System.lineSeparator()}"
                    )
                }
                outputStream.appendText("    )${System.lineSeparator()}")

                outputStream.appendText("}${System.lineSeparator()}")
            }
        }
    }
}

class AssistedViewModelProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return AssistedViewModelProcessor(environment.codeGenerator)
    }
}