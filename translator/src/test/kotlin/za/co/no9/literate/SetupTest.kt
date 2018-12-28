package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.io.StringWriter
import java.util.*


class SetupTest : StringSpec({
    "Setup configuration" {
        val srcDirectory =
                File("./src/test/resources")

        val configuration =
                configure(File("./src/test/resources"))

        val state =
                mapOf(
                        Pair("date", Date()),
                        Pair("user", "Graeme")
                )

        val template =
                configuration.getTemplate("sample.lmd")

        val output =
                StringWriter()


        template.process(state, output)

        output.toString()
                .shouldBe("# Greeting\n\nHello Graeme\n")
    }
})
