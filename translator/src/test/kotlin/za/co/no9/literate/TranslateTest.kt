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


    "Content with a multiple chunks with different names" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ NameA\n" +
                "Hello World\n" +
                "~~~~\n" +
                "some or other test\n" +
                "~~~ NameB\n" +
                "Bye bye love\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(mapOf(
                        Pair("NameA", listOf(Chunk("Hello World", false, emptyList()))),
                        Pair("NameB", listOf(Chunk("Bye bye love", false, emptyList())))
                )))
    }


    "Content with a multiple chunks with the same name" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ Name\n" +
                "Hello World\n" +
                "~~~~\n" +
                "some or other test\n" +
                "~~~ Name\n" +
                "Bye bye love\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(mapOf(
                        Pair("Name", listOf(
                                Chunk("Hello World", false, emptyList()),
                                Chunk("Bye bye love", false, emptyList())))
                )))
    }
})
