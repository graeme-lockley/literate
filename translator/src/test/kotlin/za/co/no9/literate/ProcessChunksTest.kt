package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class ProcessChunksTest : StringSpec({
    "Content without any chunk references" {
        processChunks(Chunk("Hello world", homeLocation, false, emptyList()), emptyMap())
                .shouldBe(Okay<Exception, String>("Hello world"))
    }


    "Content with a single chunk reference" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                Chunk("this is the chunk text", homeLocation, false, emptyList()))))

        val content =
                "Hello world\n" +
                        "[=Name]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "this is the chunk text\n" +
                        "Bye bye love"

        processChunks(Chunk(content, homeLocation, false, emptyList()), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }


    "Content with a single chunk reference to a multiple chunk without a separator" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                Chunk("ID1", homeLocation, false, emptyList()),
                                Chunk("ID2", homeLocation, false, emptyList())
                        )))

        val content =
                "Hello world\n" +
                        "[=Name]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "ID1ID2\n" +
                        "Bye bye love"

        processChunks(Chunk(content, homeLocation, false, emptyList()), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }


    "Content with a single chunk reference to a multiple chunk with a separator" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                Chunk("ID1", homeLocation, false, emptyList()),
                                Chunk("ID2", homeLocation, false, emptyList())
                        )))

        val content =
                "Hello world\n" +
                        "[=Name separator=\", \"]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "ID1, ID2\n" +
                        "Bye bye love"

        processChunks(Chunk(content, homeLocation, false, emptyList()), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }
})
