/*-
 * #%L
 * Relative Time Add-On
 * %%
 * Copyright (C) 2026 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.flowingcode.vaadin.addons.relativetime.test;

import com.flowingcode.vaadin.addons.relativetime.DateTimePartStyle;
import com.flowingcode.vaadin.addons.relativetime.Format;
import com.flowingcode.vaadin.addons.relativetime.FormatStyle;
import com.flowingcode.vaadin.addons.relativetime.Precision;
import com.flowingcode.vaadin.addons.relativetime.RelativeTime;
import com.flowingcode.vaadin.addons.relativetime.Tense;
import com.flowingcode.vaadin.addons.relativetime.TimeZoneName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;

public class SerializationTest {

  private void testSerializationOf(Object obj) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(obj);
    }
    try (ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
      obj.getClass().cast(in.readObject());
    }
  }

  @Test
  public void testSerialization() throws ClassNotFoundException, IOException {
    try {
      testSerializationOf(new RelativeTime());
    } catch (Exception e) {
      Assert.fail("Problem while testing serialization: " + e.getMessage());
    }
  }

  @Test
  public void testSerializationOfConfiguredInstance() throws ClassNotFoundException, IOException {
    try {
      RelativeTime configured =
          new RelativeTime(Instant.parse("2026-01-01T00:00:00Z"))
              .setTense(Tense.FUTURE)
              .setFormat(Format.RELATIVE)
              .setPrecision(Precision.DAY)
              .setFormatStyle(FormatStyle.LONG)
              .setThreshold(Duration.ofDays(30))
              .setPrefix("")
              .setNoTitle(true)
              .setLocale(Locale.forLanguageTag("es-ES"))
              .setTimeZone(ZoneId.of("America/New_York"))
              .setTimeZoneName(TimeZoneName.SHORT_OFFSET)
              .setYear(DateTimePartStyle.NUMERIC)
              .setMonth(DateTimePartStyle.LONG)
              .setDay(DateTimePartStyle.TWO_DIGIT)
              .setWeekday(DateTimePartStyle.SHORT)
              .setHour(DateTimePartStyle.TWO_DIGIT)
              .setMinute(DateTimePartStyle.TWO_DIGIT)
              .setSecond(DateTimePartStyle.TWO_DIGIT);
      testSerializationOf(configured);
    } catch (Exception e) {
      Assert.fail("Problem while testing serialization: " + e.getMessage());
    }
  }
}
