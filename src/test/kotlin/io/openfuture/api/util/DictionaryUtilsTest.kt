package io.openfuture.api.util

import io.openfuture.api.entity.scaffold.PropertyType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Yauheni Efimenko
 */
class DictionaryUtilsTest {

    @Test
    fun valueOfShouldReturnEnum() {
        val status = DictionaryUtils.valueOf(PropertyType::class.java, PropertyType.STRING.getId())
        Assertions.assertThat(status).isEqualTo(PropertyType.STRING)
    }

    @Test(expected = IllegalStateException::class)
    fun valueOfShouldThrowException() {
        DictionaryUtils.valueOf(PropertyType::class.java, -1)
    }

}
