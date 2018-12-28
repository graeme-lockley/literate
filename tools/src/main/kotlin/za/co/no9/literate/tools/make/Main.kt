package za.co.no9.literate.tools.make

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import java.io.File


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

            parsedArgs.run {
                build(source, target, verbose)
            }
        }


fun build(source: String, target: String, verbose: Boolean) {

}

fun build(log: Log, file: File, file1: File) {

}
