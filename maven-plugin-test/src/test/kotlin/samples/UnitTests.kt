package samples

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.StringSpec


class UnitTests : StringSpec({
    "sample" {
        true.shouldBeTrue()
    }

})