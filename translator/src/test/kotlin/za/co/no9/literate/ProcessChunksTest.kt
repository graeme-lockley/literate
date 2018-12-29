package za.co.no9.literate

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec


class ProcessChunksTest : StringSpec({
    "Content without any chunk references" {
        processChunks(ChunkLine("Hello world", "Fred", false, emptyList(), 1, 2), emptyMap())
                .shouldBe(Okay<Exception, String>("Hello world"))
    }


    "Content with a single chunk reference" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                ChunkLine("this is the chunk text", "Name", false, emptyList(), 1, 2))))

        val content =
                "Hello world\n" +
                        "[=Name]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "this is the chunk text\n" +
                        "Bye bye love"

        processChunks(ChunkLine(content, "Fred", false, emptyList(), 1, 2), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }


    "Content with a single chunk reference to a multiple chunk without a separator" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                ChunkLine("ID1", "Name", false, emptyList(), 1, 2),
                                ChunkLine("ID2", "Name", false, emptyList(), 1, 2)
                        )))

        val content =
                "Hello world\n" +
                        "[=Name]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "ID1ID2\n" +
                        "Bye bye love"

        processChunks(ChunkLine(content, "Fred", false, emptyList(), 1, 2), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }


    "Content with a single chunk reference to a multiple chunk with a separator" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                ChunkLine("ID1", "Name", false, emptyList(), 1, 2),
                                ChunkLine("ID2", "Name", false, emptyList(), 1, 2)
                        )))

        val content =
                "Hello world\n" +
                        "[=Name separator=\", \"]\n" +
                        "Bye bye love"

        val output =
                "Hello world\n" +
                        "ID1, ID2\n" +
                        "Bye bye love"

        processChunks(ChunkLine(content, "Fred", false, emptyList(), 1, 2), chunks)
                .shouldBe(Okay<Exception, String>(output))
    }


    "Content with a reference to an unknown chunk" {
        val chunks =
                mapOf(
                        Pair("Name", listOf(
                                ChunkLine("this is the chunk text", "Name", false, emptyList(), 1, 2))))

        val content =
                "Hello world\n" +
                        "[=Named]\n" +
                        "Bye bye love"

        shouldThrow<ProcessException> {
            processChunks(ChunkLine(content, "Fred", false, emptyList(), 1, 2), chunks)
        }.message.shouldBe("Reference to unknown chunk Named")
    }
})
