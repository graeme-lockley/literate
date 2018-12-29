package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class ExtractChunkLinesTest : StringSpec({
    "Content without any chunks" {
        extractChunks("")
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(emptyMap()))
    }


    "Content with a simple chunk" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ Name\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(mapOf(
                        Pair("Name", listOf(ChunkLine("Hello World", "Name", false, emptyList(), 3, 3)))
                )))
    }


    "Content with an additive chunk" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ +Name\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(mapOf(
                        Pair("Name", listOf(ChunkLine("Hello World", "Name", true, emptyList(), 3, 3)))
                )))
    }


    "Content with an additive chunk and parameters" {
        extractChunks("asdfasdf\n" +
                "asdfasdf\n" +
                "~~~ +Name source=\"File.sle\" weave=false tangle=true width=123\n" +
                "Hello World\n" +
                "~~~~\n" +
                "asdfasdfaf")
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(mapOf(
                        Pair("Name", listOf(ChunkLine("Hello World", "Name", true, listOf(
                                Argument("source", "File.sle"),
                                Argument("weave", "false"),
                                Argument("tangle", "true"),
                                Argument("width", "123")
                        ), 3, 3)))
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
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(mapOf(
                        Pair("NameA", listOf(ChunkLine("Hello World", "NameA", false, emptyList(), 3, 3))),
                        Pair("NameB", listOf(ChunkLine("Bye bye love", "NameB", false, emptyList(), 7, 7)))
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
                .shouldBe(Okay<Exception, Map<String, List<ChunkLine>>>(mapOf(
                        Pair("Name", listOf(
                                ChunkLine("Hello World", "Name", false, emptyList(), 3, 3),
                                ChunkLine("Bye bye love", "Name", false, emptyList(), 7, 7)))
                )))
    }
})
