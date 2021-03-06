/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.datadog;

import io.micrometer.core.Issue;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

/**
 * Tests for {@link DatadogNamingConvention}.
 *
 * @author Jon Schneider
 * @author Johnny Lim
 */
class DatadogNamingConventionTest {
    private DatadogNamingConvention convention = new DatadogNamingConvention();

    @Test
    void nameStartsWithLetter() {
        assertThat(convention.name("123", Meter.Type.GAUGE, null)).isEqualTo("m.123");
    }

    @Test
    void tagKeyStartsWithLetter() {
        assertThat(convention.tagKey("123")).isEqualTo("m.123");
    }

    @Test
    void tagKeyWhenStartsWithNumberShouldRespectDelegateNamingConvention() {
        String tagKey = "123";

        NamingConvention delegate = mock(NamingConvention.class);
        given(delegate.tagKey(eq(tagKey))).willReturn("123456");

        DatadogNamingConvention convention = new DatadogNamingConvention(delegate);

        assertThat(convention.tagKey(tagKey)).isEqualTo("m.123456");
    }

    @Test
    void dotNotationIsConvertedToCamelCase() {
        assertThat(convention.name("gauge.size", Meter.Type.GAUGE, null)).isEqualTo("gauge.size");
    }

    @Issue("#589")
    @Test
    void jsonSpecialCharactersAreEscaped() {
        assertThat(convention.name("name\"", Meter.Type.GAUGE, null)).isEqualTo("name\\\"");
        assertThat(convention.tagKey("key\"")).isEqualTo("key\\\"");
        assertThat(convention.tagValue("value\"")).isEqualTo("value\\\"");
    }
}
