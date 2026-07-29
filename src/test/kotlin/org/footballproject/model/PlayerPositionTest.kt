package org.footballproject.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlayerPositionTest {

    @Test
    fun mapsEachApiPositionStringToItsEnumValue() {
        assertThat(PlayerPosition.fromApiPosition("Goalkeeper")).isEqualTo(PlayerPosition.GOALKEEPER)
        assertThat(PlayerPosition.fromApiPosition("Defender")).isEqualTo(PlayerPosition.DEFENDER)
        assertThat(PlayerPosition.fromApiPosition("Midfielder")).isEqualTo(PlayerPosition.MIDFIELDER)
        assertThat(PlayerPosition.fromApiPosition("Attacker")).isEqualTo(PlayerPosition.ATTACKER)
    }

    @Test
    fun returnsNullForAnUnrecognizedPositionString() {
        assertThat(PlayerPosition.fromApiPosition("Wing-back")).isNull()
        assertThat(PlayerPosition.fromApiPosition("")).isNull()
    }
}
