package za.co.no9.literate

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.StringSpec


class Sample : StringSpec({


    "Something is true" {
        true.shouldBeTrue()
    }
})
