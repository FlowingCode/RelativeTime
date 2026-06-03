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

package com.flowingcode.vaadin.addons.relativetime;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Renders a date/time as a human-readable relative string ("4 hours ago", "in 2 weeks") that
 * updates in the browser as time passes and is shown in the viewer's local time.
 *
 * <p>Thin wrapper around the <a
 * href="https://github.com/github/relative-time-element">{@code @github/relative-time-element}</a>
 * custom element. The Java setters write the corresponding attributes on the element and the
 * browser does all the formatting and updating work.
 *
 * <p><b>Client-side rendering.</b> The rendered text reflects the viewer's clock and locale, not
 * the server's. There is no server-side API to read the displayed string; the string lives only
 * in the DOM.
 *
 * <p><b>Attributes, not properties.</b> Setters write HTML attributes
 * ({@code setAttribute}), not DOM properties, because the upstream element is
 * attribute-driven and its kebab-case attribute names match the upstream docs
 * one-to-one. {@code setProperty} would force diverging camelCase names.
 *
 * <p>{@code LocalDateTime} and {@code LocalDate} inputs are interpreted in the server's
 * {@link ZoneId#systemDefault() default zone} before being serialised as an instant.
 */
@Tag("relative-time")
@NpmPackage(value = "@github/relative-time-element", version = "5.0.0")
@JsModule("@github/relative-time-element")
@SuppressWarnings("serial")
public class RelativeTime extends Component {

  private static final String ATTR_DATETIME = "datetime";
  private static final String ATTR_TENSE = "tense";
  private static final String ATTR_FORMAT = "format";
  private static final String ATTR_PRECISION = "precision";
  private static final String ATTR_FORMAT_STYLE = "format-style";
  private static final String ATTR_THRESHOLD = "threshold";
  private static final String ATTR_PREFIX = "prefix";
  private static final String ATTR_NO_TITLE = "no-title";
  private static final String ATTR_LANG = "lang";
  private static final String ATTR_TIME_ZONE = "time-zone";
  private static final String ATTR_TIME_ZONE_NAME = "time-zone-name";
  private static final String ATTR_YEAR = "year";
  private static final String ATTR_MONTH = "month";
  private static final String ATTR_DAY = "day";
  private static final String ATTR_WEEKDAY = "weekday";
  private static final String ATTR_HOUR = "hour";
  private static final String ATTR_MINUTE = "minute";
  private static final String ATTR_SECOND = "second";

  /** Styles accepted by the numeric date-part attributes (year, day, hour, minute, second). */
  private static final Set<DateTimePartStyle> NUMERIC_STYLES =
      Set.of(DateTimePartStyle.NUMERIC, DateTimePartStyle.TWO_DIGIT);
  /** Styles accepted by the textual date-part attribute (weekday). */
  private static final Set<DateTimePartStyle> TEXT_STYLES =
      Set.of(DateTimePartStyle.NARROW, DateTimePartStyle.SHORT, DateTimePartStyle.LONG);
  /** Styles accepted by the month attribute (numeric and textual). */
  private static final Set<DateTimePartStyle> ALL_STYLES =
      EnumSet.allOf(DateTimePartStyle.class);

  private Instant lastDateTime;

  /** Creates an empty component. {@link #setDateTime} can be called later. */
  public RelativeTime() {}

  /** Creates a component bound to the given instant. */
  public RelativeTime(Instant datetime) {
    setDateTime(datetime);
  }

  /** Creates a component bound to the given offset date-time. */
  public RelativeTime(OffsetDateTime datetime) {
    setDateTime(datetime);
  }

  /** Creates a component bound to the given zoned date-time. */
  public RelativeTime(ZonedDateTime datetime) {
    setDateTime(datetime);
  }

  /**
   * Creates a component bound to the given local date-time, interpreted in the server's
   * {@link ZoneId#systemDefault() default zone}.
   */
  public RelativeTime(LocalDateTime datetime) {
    setDateTime(datetime);
  }

  /**
   * Creates a component bound to midnight of the given date, in the server's
   * {@link ZoneId#systemDefault() default zone}.
   */
  public RelativeTime(LocalDate date) {
    setDateTime(date);
  }

  /**
   * Sets the target instant. {@code null} clears the {@code datetime} attribute and the component
   * renders as empty.
   */
  public RelativeTime setDateTime(Instant datetime) {
    lastDateTime = datetime;
    setOrRemove(ATTR_DATETIME, datetime == null ? null : datetime.toString());
    return this;
  }

  /**
   * Sets the target as an {@link OffsetDateTime}. The offset is applied via {@code toInstant()};
   * the wire string is the resulting UTC instant (the offset itself is not retained).
   */
  public RelativeTime setDateTime(OffsetDateTime datetime) {
    return setDateTime(datetime == null ? null : datetime.toInstant());
  }

  /**
   * Sets the target as a {@link ZonedDateTime}. The zone is applied via {@code toInstant()}; the
   * wire string is the resulting UTC instant (the zone itself is not retained).
   */
  public RelativeTime setDateTime(ZonedDateTime datetime) {
    return setDateTime(datetime == null ? null : datetime.toInstant());
  }

  /**
   * Sets the target as a {@link LocalDateTime}, interpreted in the server's
   * {@link ZoneId#systemDefault() default zone}.
   */
  public RelativeTime setDateTime(LocalDateTime datetime) {
    return setDateTime(
        datetime == null ? null : datetime.atZone(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Sets the target to midnight of the given date, in the server's
   * {@link ZoneId#systemDefault() default zone}.
   */
  public RelativeTime setDateTime(LocalDate date) {
    return setDateTime(
        date == null ? null : date.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Clears the target datetime: removes the {@code datetime} attribute and the component renders
   * as empty. Equivalent to {@code setDateTime((Instant) null)} but without the cast that the
   * overloaded {@code setDateTime(null)} would otherwise require.
   */
  public RelativeTime clear() {
    return setDateTime((Instant) null);
  }

  /**
   * Returns the last instant applied via any of the {@code setDateTime} overloads, normalised to
   * UTC. Returns {@code null} if no datetime has been set or it was cleared.
   */
  public Instant getDateTime() {
    return lastDateTime;
  }

  /**
   * Sets the {@code tense} attribute (default {@code auto}). Ignored when {@link #setFormat
   * format} is {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setTense(Tense tense) {
    setOrRemove(ATTR_TENSE, tense == null ? null : tense.attributeValue());
    return this;
  }

  /**
   * Sets the {@code format} attribute (default {@code auto}, which is an alias for
   * {@link Format#RELATIVE}). {@code null} removes the attribute.
   */
  public RelativeTime setFormat(Format format) {
    setOrRemove(ATTR_FORMAT, format == null ? null : format.attributeValue());
    return this;
  }

  /**
   * Sets the {@code precision} attribute (default {@code second}). Ignored when {@link #setFormat
   * format} is {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setPrecision(Precision precision) {
    setOrRemove(ATTR_PRECISION, precision == null ? null : precision.attributeValue());
    return this;
  }

  /**
   * Sets the {@code format-style} attribute. The default depends on the {@link #setFormat format}
   * in effect: {@code narrow} for {@link Format#ELAPSED}/{@link Format#MICRO}, {@code short} for
   * {@link Format#DATETIME}, {@code long} for {@link Format#RELATIVE}/{@link Format#AUTO}/{@link
   * Format#DURATION}. {@code null} removes the attribute.
   */
  public RelativeTime setFormatStyle(FormatStyle style) {
    setOrRemove(ATTR_FORMAT_STYLE, style == null ? null : style.attributeValue());
    return this;
  }

  /**
   * Sets the {@code threshold} attribute as an ISO-8601 duration ({@code P30D}, {@code PT24H}).
   * Default is {@code P30D}. Once the gap to the target exceeds this duration the element switches
   * from a relative phrase to an absolute date. {@code null} removes the attribute.
   *
   * <p><b>Only consulted when {@link #setFormat format} is {@link Format#AUTO}/{@link
   * Format#RELATIVE} AND {@link #setTense tense} is {@link Tense#AUTO}.</b> Pairing this with
   * {@code setTense(PAST)} or {@code setTense(FUTURE)} silently makes the threshold inert,
   * because the element then commits to relative phrasing regardless of distance.
   */
  public RelativeTime setThreshold(Duration threshold) {
    setOrRemove(ATTR_THRESHOLD, threshold == null ? null : threshold.toString());
    return this;
  }

  /**
   * Sets the {@code prefix} attribute (default {@code "on"}), the word prepended to absolute dates
   * when the relative-format threshold is crossed. Pass an empty string to drop the prefix
   * entirely; {@code null} removes the attribute and restores the default.
   */
  public RelativeTime setPrefix(String prefix) {
    setOrRemove(ATTR_PREFIX, prefix);
    return this;
  }

  /**
   * When {@code true}, adds the {@code no-title} attribute, suppressing the automatic absolute-date
   * tooltip. {@code false} removes it (the element's default behaviour shows the tooltip).
   */
  public RelativeTime setNoTitle(boolean noTitle) {
    if (noTitle) {
      getElement().setAttribute(ATTR_NO_TITLE, true);
    } else {
      getElement().removeAttribute(ATTR_NO_TITLE);
    }
    return this;
  }

  /**
   * Sets the {@code lang} attribute from a {@link Locale} (rendered as a BCP&nbsp;47 language tag).
   * The browser uses {@code Intl.RelativeTimeFormat} / {@code Intl.DateTimeFormat} to localise the
   * phrasing. {@code null} removes the attribute; when unset, the upstream walks DOM ancestors
   * via {@code closest('[lang]')} and falls back to the document's {@code lang}, so an app-wide
   * locale on {@code <html lang="...">} is picked up automatically.
   *
   * <p>Note: {@link Locale#ROOT} (and {@code new Locale("")}) serialises to {@code lang="und"},
   * which the browser treats as "undetermined" and silently resolves to the document/default
   * locale, so passing the root locale looks like it was ignored. Pass {@code null} to clear
   * instead.
   */
  public RelativeTime setLocale(Locale locale) {
    setOrRemove(ATTR_LANG, locale == null ? null : locale.toLanguageTag());
    return this;
  }

  /**
   * Sets the {@code time-zone} attribute from a {@link ZoneId} (e.g. {@code America/New_York}).
   * When set, the element formats the date in this zone instead of the viewer's. {@code null}
   * removes the attribute; when unset, the upstream walks DOM ancestors via {@code
   * closest('[time-zone]')} and falls back to the document's {@code time-zone} attribute (and,
   * absent that, the browser default).
   */
  public RelativeTime setTimeZone(ZoneId timeZone) {
    setOrRemove(ATTR_TIME_ZONE, timeZone == null ? null : timeZone.getId());
    return this;
  }

  /**
   * Sets the {@code time-zone-name} attribute, controlling whether and how the time-zone name is
   * appended in absolute-date output. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setTimeZoneName(TimeZoneName value) {
    setOrRemove(ATTR_TIME_ZONE_NAME, value == null ? null : value.attributeValue());
    return this;
  }

  /**
   * Sets the {@code year} attribute. Accepts {@link DateTimePartStyle#NUMERIC} or
   * {@link DateTimePartStyle#TWO_DIGIT}. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setYear(DateTimePartStyle style) {
    return setDatePart(ATTR_YEAR, style, NUMERIC_STYLES);
  }

  /**
   * Sets the {@code month} attribute. Accepts {@link DateTimePartStyle#NUMERIC},
   * {@link DateTimePartStyle#TWO_DIGIT}, {@link DateTimePartStyle#NARROW},
   * {@link DateTimePartStyle#SHORT}, or {@link DateTimePartStyle#LONG}. Only relevant when
   * {@link #setFormat format} is {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setMonth(DateTimePartStyle style) {
    return setDatePart(ATTR_MONTH, style, ALL_STYLES);
  }

  /**
   * Sets the {@code day} attribute. Accepts {@link DateTimePartStyle#NUMERIC} or
   * {@link DateTimePartStyle#TWO_DIGIT}. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setDay(DateTimePartStyle style) {
    return setDatePart(ATTR_DAY, style, NUMERIC_STYLES);
  }

  /**
   * Sets the {@code weekday} attribute. Accepts {@link DateTimePartStyle#NARROW},
   * {@link DateTimePartStyle#SHORT}, or {@link DateTimePartStyle#LONG}. Only relevant when
   * {@link #setFormat format} is {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setWeekday(DateTimePartStyle style) {
    return setDatePart(ATTR_WEEKDAY, style, TEXT_STYLES);
  }

  /**
   * Sets the {@code hour} attribute. Accepts {@link DateTimePartStyle#NUMERIC} or
   * {@link DateTimePartStyle#TWO_DIGIT}. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setHour(DateTimePartStyle style) {
    return setDatePart(ATTR_HOUR, style, NUMERIC_STYLES);
  }

  /**
   * Sets the {@code minute} attribute. Accepts {@link DateTimePartStyle#NUMERIC} or
   * {@link DateTimePartStyle#TWO_DIGIT}. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setMinute(DateTimePartStyle style) {
    return setDatePart(ATTR_MINUTE, style, NUMERIC_STYLES);
  }

  /**
   * Sets the {@code second} attribute. Accepts {@link DateTimePartStyle#NUMERIC} or
   * {@link DateTimePartStyle#TWO_DIGIT}. Only relevant when {@link #setFormat format} is
   * {@link Format#DATETIME}. {@code null} removes the attribute.
   */
  public RelativeTime setSecond(DateTimePartStyle style) {
    return setDatePart(ATTR_SECOND, style, NUMERIC_STYLES);
  }

  /**
   * Writes a date-part attribute after checking the style is in the part's allowed subset. The
   * upstream element silently ignores or coerces out-of-subset values, so we fail fast instead.
   */
  private RelativeTime setDatePart(String attribute, DateTimePartStyle style,
      Set<DateTimePartStyle> allowed) {
    if (style != null && !allowed.contains(style)) {
      throw new IllegalArgumentException(
          attribute + " does not accept " + style + "; allowed values: " + allowed);
    }
    setOrRemove(attribute, style == null ? null : style.attributeValue());
    return this;
  }

  private void setOrRemove(String attribute, String value) {
    if (value == null) {
      getElement().removeAttribute(attribute);
    } else {
      getElement().setAttribute(attribute, value);
    }
  }
}
