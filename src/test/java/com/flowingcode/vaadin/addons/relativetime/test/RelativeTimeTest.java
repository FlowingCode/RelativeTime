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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.flowingcode.vaadin.addons.relativetime.DateTimePartStyle;
import com.flowingcode.vaadin.addons.relativetime.Format;
import com.flowingcode.vaadin.addons.relativetime.FormatStyle;
import com.flowingcode.vaadin.addons.relativetime.Precision;
import com.flowingcode.vaadin.addons.relativetime.RelativeTime;
import com.flowingcode.vaadin.addons.relativetime.Tense;
import com.flowingcode.vaadin.addons.relativetime.TimeZoneName;
import com.vaadin.flow.dom.Element;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import org.junit.Test;

public class RelativeTimeTest {

  private static final Instant FIXED = Instant.parse("2026-01-15T12:34:56Z");

  // --- datetime overloads -------------------------------------------------

  @Test
  public void setDateTime_instant_writesAttributeAndUpdatesGetter() {
    RelativeTime rt = new RelativeTime();
    rt.setDateTime(FIXED);
    assertEquals(FIXED.toString(), attr(rt, "datetime"));
    assertEquals(FIXED, rt.getDateTime());
  }

  @Test
  public void setDateTime_null_clearsAttributeAndGetter() {
    RelativeTime rt = new RelativeTime(FIXED);
    rt.setDateTime((Instant) null);
    assertNull(attr(rt, "datetime"));
    assertNull(rt.getDateTime());
  }

  @Test
  public void setDateTime_offsetDateTime_normalisesToInstant() {
    OffsetDateTime odt = OffsetDateTime.of(2026, 1, 15, 14, 34, 56, 0, ZoneOffset.ofHours(2));
    RelativeTime rt = new RelativeTime();
    rt.setDateTime(odt);
    assertEquals(odt.toInstant(), rt.getDateTime());
    assertEquals(odt.toInstant().toString(), attr(rt, "datetime"));
  }

  @Test
  public void setDateTime_zonedDateTime_normalisesToInstant() {
    ZonedDateTime zdt = ZonedDateTime.of(2026, 1, 15, 13, 34, 56, 0, ZoneId.of("Europe/Madrid"));
    RelativeTime rt = new RelativeTime();
    rt.setDateTime(zdt);
    assertEquals(zdt.toInstant(), rt.getDateTime());
  }

  @Test
  public void setDateTime_localDateTime_usesSystemDefaultZone() {
    LocalDateTime ldt = LocalDateTime.of(2026, 1, 15, 12, 34, 56);
    Instant expected = ldt.atZone(ZoneId.systemDefault()).toInstant();
    RelativeTime rt = new RelativeTime();
    rt.setDateTime(ldt);
    assertEquals(expected, rt.getDateTime());
  }

  @Test
  public void setDateTime_localDate_usesStartOfDayInSystemDefaultZone() {
    LocalDate date = LocalDate.of(2026, 1, 15);
    Instant expected = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    RelativeTime rt = new RelativeTime();
    rt.setDateTime(date);
    assertEquals(expected, rt.getDateTime());
  }

  // --- enum setters -------------------------------------------------------

  @Test
  public void setTense_writesLowercaseName() {
    RelativeTime rt = new RelativeTime();
    rt.setTense(Tense.FUTURE);
    assertEquals("future", attr(rt, "tense"));
    rt.setTense(null);
    assertNull(attr(rt, "tense"));
  }

  @Test
  public void setFormat_writesLowercaseName() {
    RelativeTime rt = new RelativeTime();
    rt.setFormat(Format.DURATION);
    assertEquals("duration", attr(rt, "format"));
    rt.setFormat(null);
    assertNull(attr(rt, "format"));
  }

  @Test
  public void setPrecision_writesLowercaseName() {
    RelativeTime rt = new RelativeTime();
    rt.setPrecision(Precision.DAY);
    assertEquals("day", attr(rt, "precision"));
    rt.setPrecision(null);
    assertNull(attr(rt, "precision"));
  }

  @Test
  public void setFormatStyle_writesKebabAttribute() {
    RelativeTime rt = new RelativeTime();
    rt.setFormatStyle(FormatStyle.NARROW);
    assertEquals("narrow", attr(rt, "format-style"));
    rt.setFormatStyle(null);
    assertNull(attr(rt, "format-style"));
  }

  // --- duration, prefix, no-title, locale --------------------------------

  @Test
  public void setThreshold_writesIso8601Duration() {
    RelativeTime rt = new RelativeTime();
    rt.setThreshold(Duration.ofDays(30));
    assertEquals("PT720H", attr(rt, "threshold"));
    rt.setThreshold(null);
    assertNull(attr(rt, "threshold"));
  }

  @Test
  public void setPrefix_emptyStringSetsEmpty_nullClears() {
    RelativeTime rt = new RelativeTime();
    rt.setPrefix("");
    assertEquals("", attr(rt, "prefix"));
    rt.setPrefix(null);
    assertNull(attr(rt, "prefix"));
  }

  @Test
  public void setNoTitle_trueSets_falseRemoves() {
    RelativeTime rt = new RelativeTime();
    rt.setNoTitle(true);
    assertTrue(rt.getElement().hasAttribute("no-title"));
    rt.setNoTitle(false);
    assertFalse(rt.getElement().hasAttribute("no-title"));
  }

  @Test
  public void setLocale_writesLanguageTagAsLang() {
    RelativeTime rt = new RelativeTime();
    rt.setLocale(Locale.forLanguageTag("es-ES"));
    assertEquals("es-ES", attr(rt, "lang"));
    rt.setLocale(null);
    assertNull(attr(rt, "lang"));
  }

  // --- time-zone, time-zone-name, date parts -----------------------------

  @Test
  public void setTimeZone_writesZoneIdAsTimeZone() {
    RelativeTime rt = new RelativeTime();
    rt.setTimeZone(ZoneId.of("America/New_York"));
    assertEquals("America/New_York", attr(rt, "time-zone"));
    rt.setTimeZone(null);
    assertNull(attr(rt, "time-zone"));
  }

  @Test
  public void setTimeZoneName_writesCamelCaseValue() {
    RelativeTime rt = new RelativeTime();
    rt.setTimeZoneName(TimeZoneName.SHORT_OFFSET);
    assertEquals("shortOffset", attr(rt, "time-zone-name"));
    rt.setTimeZoneName(TimeZoneName.LONG);
    assertEquals("long", attr(rt, "time-zone-name"));
    rt.setTimeZoneName(null);
    assertNull(attr(rt, "time-zone-name"));
  }

  @Test
  public void datePartSetters_writeAttribute_andTwoDigitSerialisesWithHyphen() {
    RelativeTime rt = new RelativeTime();

    rt.setYear(DateTimePartStyle.NUMERIC);
    assertEquals("numeric", attr(rt, "year"));

    rt.setMonth(DateTimePartStyle.LONG);
    assertEquals("long", attr(rt, "month"));

    rt.setDay(DateTimePartStyle.TWO_DIGIT);
    assertEquals("2-digit", attr(rt, "day"));

    rt.setWeekday(DateTimePartStyle.NARROW);
    assertEquals("narrow", attr(rt, "weekday"));

    rt.setHour(DateTimePartStyle.TWO_DIGIT);
    assertEquals("2-digit", attr(rt, "hour"));

    rt.setMinute(DateTimePartStyle.NUMERIC);
    assertEquals("numeric", attr(rt, "minute"));

    rt.setSecond(DateTimePartStyle.TWO_DIGIT);
    assertEquals("2-digit", attr(rt, "second"));
  }

  @Test
  public void datePartSetters_nullRemovesAttribute() {
    RelativeTime rt =
        new RelativeTime()
            .setYear(DateTimePartStyle.NUMERIC)
            .setMonth(DateTimePartStyle.LONG)
            .setDay(DateTimePartStyle.TWO_DIGIT)
            .setWeekday(DateTimePartStyle.SHORT)
            .setHour(DateTimePartStyle.NUMERIC)
            .setMinute(DateTimePartStyle.NUMERIC)
            .setSecond(DateTimePartStyle.NUMERIC);

    rt.setYear(null);
    rt.setMonth(null);
    rt.setDay(null);
    rt.setWeekday(null);
    rt.setHour(null);
    rt.setMinute(null);
    rt.setSecond(null);

    assertNull(attr(rt, "year"));
    assertNull(attr(rt, "month"));
    assertNull(attr(rt, "day"));
    assertNull(attr(rt, "weekday"));
    assertNull(attr(rt, "hour"));
    assertNull(attr(rt, "minute"));
    assertNull(attr(rt, "second"));
  }

  // --- fluent chain returns this -----------------------------------------

  @Test
  public void settersReturnSameInstance() {
    RelativeTime rt = new RelativeTime();
    assertEquals(rt, rt.setDateTime(FIXED));
    assertEquals(rt, rt.setTense(Tense.AUTO));
    assertEquals(rt, rt.setFormat(Format.RELATIVE));
    assertEquals(rt, rt.setPrecision(Precision.HOUR));
    assertEquals(rt, rt.setFormatStyle(FormatStyle.LONG));
    assertEquals(rt, rt.setThreshold(Duration.ofDays(1)));
    assertEquals(rt, rt.setPrefix("on"));
    assertEquals(rt, rt.setNoTitle(true));
    assertEquals(rt, rt.setLocale(Locale.ENGLISH));
    assertEquals(rt, rt.setTimeZone(ZoneId.of("UTC")));
    assertEquals(rt, rt.setTimeZoneName(TimeZoneName.SHORT));
    assertEquals(rt, rt.setYear(DateTimePartStyle.NUMERIC));
    assertEquals(rt, rt.setMonth(DateTimePartStyle.LONG));
    assertEquals(rt, rt.setDay(DateTimePartStyle.TWO_DIGIT));
    assertEquals(rt, rt.setWeekday(DateTimePartStyle.SHORT));
    assertEquals(rt, rt.setHour(DateTimePartStyle.NUMERIC));
    assertEquals(rt, rt.setMinute(DateTimePartStyle.NUMERIC));
    assertEquals(rt, rt.setSecond(DateTimePartStyle.NUMERIC));
  }

  private static String attr(RelativeTime rt, String name) {
    Element el = rt.getElement();
    return el.hasAttribute(name) ? el.getAttribute(name) : null;
  }
}
