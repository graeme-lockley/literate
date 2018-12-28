package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class TranslateTest : StringSpec({
    "Content without any chunks" {
        extractChunks("")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(emptyMap()))
    }


    "Content with a simple chunk" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ Name\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(mapOf(
                        Pair("Name", listOf(Chunk("Hello World", false, emptyList())))
                )))
    }
})
