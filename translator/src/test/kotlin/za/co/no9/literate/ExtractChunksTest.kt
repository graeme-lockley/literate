package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class ExtractChunksTest : StringSpec({
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
                        Pair("Name", listOf(Chunk("Hello World", Location(Position(2, 0),  Position(4, 4)), false, emptyList())))
                )))
    }


    "Content with an additive chunk" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ +Name\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(mapOf(
                        Pair("Name", listOf(Chunk("Hello World", Location(Position(2, 0),  Position(4, 4)), true, emptyList())))
                )))
    }


    "Content with an additive chunk and parameters" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ +Name source=\"File.sle\" weave=false tangle=true width=123\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<Chunk>>>(mapOf(
                        Pair("Name", listOf(Chunk("Hello World", Location(Position(2, 0),  Position(4, 4)), true, listOf(
                                Argument("source", "File.sle"),
                                Argument("weave", "false"),
                                Argument("tangle", "true"),
                                Argument("width", "123")
                        ))))
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
                        Pair("NameA", listOf(Chunk("Hello World", Location(Position(2, 0),  Position(4, 4)), false, emptyList()))),
                        Pair("NameB", listOf(Chunk("Bye bye love", Location(Position(6, 0),  Position(8, 4)), false, emptyList())))
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
                                Chunk("Hello World", Location(Position(2, 0),  Position(4, 4)), false, emptyList()),
                                Chunk("Bye bye love", Location(Position(6, 0),  Position(8, 4)), false, emptyList())))
                )))
    }
})
