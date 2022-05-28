import com.annotation.Column
import com.annotation.Entity
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import kotlin.reflect.KClass
import java.io.OutputStreamWriter

class DBAnnotationProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allFiles = resolver.findAnnotations(Entity::class)
        if(!allFiles.iterator().hasNext()) return emptyList()
        logger.warn(allFiles.toList().toString())
        if (invoked) {
            return emptyList()
        }
        invoked = true

        codeGenerator.createNewFile(Dependencies(false), "com.entity", "EntityProcessor", "kt").use { output ->

            OutputStreamWriter(output).use { writer ->
                writer.write("package com.entity\n\n")
                val it = allFiles.iterator()
                while(it.hasNext()){
                    val cl = it.next()
                    writer.write("import ${cl.qualifiedName?.getQualifier()}.${cl.qualifiedName?.getShortName()}\n")
                }
                val visitor = ClassVisitor()

                resolver.findAnnotations(Entity::class).forEach {
                    it.accept(visitor, writer)
                }

                writer.write("\n}")
                writer.flush()
                writer.close()
            }
            output.close()
        }
        return emptyList()
    }
}

class ClassVisitor : KSTopDownVisitor<OutputStreamWriter, Unit>() {
    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {
    }

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)
        var fnStr = "\nfun ${classDeclaration.simpleName.asString()}.getColumnNames(): List<String>{\n   return listOf("
        var fnSetter = "\nfun ${classDeclaration.simpleName.asString()}.setVal(name: String, value: String?){\n   when (name) {\n"
        classDeclaration.getAllProperties().forEach {propery ->

            propery.annotations.toList().forEach{
                if(it.shortName.getShortName() == Column::class.simpleName){
                    val colName = it.arguments.get(0).value as String
                    fnStr += "\"$colName\", "
                    fnSetter += "      \"$colName\" -> ${propery.simpleName.getShortName()} = value \n"
                }
            }
        }
        fnSetter += "   }"
        fnStr += ")\n}\n"
        data.write("    $fnStr")
        data.write("    $fnSetter")
    }
}

class DBAnnotationProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DBAnnotationProcessor(environment.codeGenerator, environment.logger)
    }
}
private fun Resolver.findAnnotations(
    kClass: KClass<*>,
) = getSymbolsWithAnnotation(
    kClass.qualifiedName.toString())
    .filterIsInstance<KSClassDeclaration>()