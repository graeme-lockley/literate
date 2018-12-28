package za.co.no9.literate.tools.make

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import freemarker.template.Configuration
import za.co.no9.literate.configure
import java.io.File
import java.io.StringWriter
import java.util.*



interface Listener {
    fun processedTemplate(output: String)
}


class MyListener(val log: Log, val showProcessedOutput: Boolean):Listener {
    override fun processedTemplate(output: String) {
        if (showProcessedOutput) {
            log.info(output)
        }
    }
}


class MyLog: Log {
    override fun error(message: String) {
        System.err.println("Error: $message")
    }

    override fun info(message: String) {
        System.out.println(message)
    }
}


class Arguments(parser: ArgParser) {
    val verbose by parser
            .flagging("enable verbose mode")
            .default(false)

    val target by parser
            .storing("-T", "--target", help = "target directory of compiled code")
            .default(".")


    val source by parser
            .storing("-S", "--source", help = "source directory of files to compile")
            .default(File(".").absolutePath)
}


fun main(args: Array<String>): Unit =
        mainBody {
            val parsedArgs =
                    ArgParser(args).parseInto(::Arguments)

            val log =
                    MyLog()

            val listener =
                    MyListener(log, true)

            parsedArgs.run {
                build(listener, source, target, verbose)
            }
        }


fun build(listener: Listener, source: String, target: String, verbose: Boolean) {
    val configuration =
            configure(File("."))

    val sourceFile =
            File(source)

    val targetFile =
            File(target)

    targetFile.mkdirs()

    if (sourceFile.isDirectory) {
        sourceFile.walk().filter { it.isFile && it.name.endsWith("lmd") }.forEach {
            build(listener, configuration, it, targetFile)
        }
    }
}


fun build(log: Log, source: File, target: File) {

}

fun build(listener: Listener, configuration: Configuration, source: File, target: File) {
    configuration.setDirectoryForTemplateLoading(source.parentFile)

    val template =
            configuration.getTemplate(source.name)

    val state =
            mapOf(
                    Pair("date", Date())
            )

    val outputWriter =
            StringWriter()

    template.process(state, outputWriter)

    val output =
            outputWriter.toString()

    listener.processedTemplate(output)
}
