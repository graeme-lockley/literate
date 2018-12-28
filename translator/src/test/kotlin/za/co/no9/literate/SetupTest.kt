package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.*


class SetupTest : StringSpec({
    "Setup configuration" {
        val configuration =
                configure(File("./src/test/resources"))

        val state =
                mapOf(
                        Pair("date", Date()),
                        Pair("user", "Graeme")
                )

        loadTemplate(configuration, "sample.lmd")
                .andThen { processTemplate(state, it) }
                .shouldBe(Okay<Exception, String>("# Greeting\n\nHello Graeme\n"))
    }
})
