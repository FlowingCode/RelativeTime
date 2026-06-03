# Relative Time Add-on - Specification

## 1. Overview

The Relative Time Add-on is a component that renders a date/time as a human-readable relative string ("4 hours from now", "3 days ago", "in 2 weeks") that updates in the browser as time passes and is shown in the viewer's local time.

`RelativeTime` is a thin wrapper around [`@github/relative-time-element`](https://github.com/github/relative-time-element), a small, well-maintained, accessibility-tested custom element. The wrapper exposes the element's attributes as a typed Java API and accepts the standard `java.time` types for the target date.

The component is added to the layout directly. Display options (tense, format, precision, threshold, locale, time-zone, individual date parts) are configured through setters on the `RelativeTime` instance.

## 2. Core Concepts

### 2.1 Client-Side Rendering

The relative string is computed and updated in the browser by the underlying web component. The server sends only the target datetime as an ISO-8601 string in the `datetime` attribute; no server round-trip is needed to keep the displayed text current.

This has two consequences worth being explicit about:

- The text reflects the **viewer's** clock and locale, not the server's (unless `time-zone` is set explicitly).
- The server-side component has no API to read the rendered string, there is nothing to read; the string lives only in the DOM.

### 2.2 Java-Type to Attribute Mapping

The setters accept the standard `java.time` types. Each is converted to an ISO-8601 instant before being written to the `datetime` attribute:

| Java Type | Conversion | Notes |
|-----------|------------|-------|
| `Instant` | direct | Canonical form. |
| `OffsetDateTime` | `toInstant()` | Offset applied, then discarded; wire string is the resulting UTC instant. |
| `ZonedDateTime` | `toInstant()` | Zone applied, then discarded; wire string is the resulting UTC instant. |
| `LocalDateTime` | `atZone(systemDefault()).toInstant()` | Server zone assumed; document this in the Javadoc. |
| `LocalDate` | `atStartOfDay(systemDefault()).toInstant()` | Midnight of the server zone. |

`null` clears the `datetime` attribute and the component renders as empty.

The wrapper drives the element through HTML attributes (`setAttribute`), not DOM properties: `@github/relative-time-element` is attribute-driven, and its kebab-case attribute names match the upstream docs one-to-one (§2.3).

### 2.3 Display Modes

The web component supports several formats. The Java API exposes them as enums with values that match the attribute vocabulary one-to-one, so a developer reading either side of the wrapper sees the same names:

| Enum | Values | Maps to attribute | Notes |
|------|--------|-------------------|-------|
| `Tense` | `AUTO`, `PAST`, `FUTURE` | `tense` | Default `auto`. Ignored when `format=datetime`. (Subsumes the upstream `<time-ago>` / `<time-until>` / `<time-when>` element variants, which are degenerate cases of `<relative-time>`; no separate Java classes are provided.) |
| `Format` | `RELATIVE`, `DURATION`, `DATETIME`, `AUTO`, `ELAPSED`, `MICRO` | `format` | Default `auto`, which is an alias for `relative`. |
| `Precision` | `YEAR`, `MONTH`, `DAY`, `HOUR`, `MINUTE`, `SECOND` | `precision` | Default `second`. Ignored when `format=datetime`. |
| `FormatStyle` | `LONG`, `SHORT`, `NARROW` | `format-style` | Default depends on `format`: `narrow` for `elapsed`/`micro`, `short` for `datetime`, `long` for `relative`/`auto`/`duration`. |

### 2.4 Threshold

The `threshold` attribute (default `P30D`) switches the component from relative ("3 weeks ago") to absolute ("Jan 4, 2024") once the gap to the target exceeds a configurable duration. The Java setter accepts a `java.time.Duration` and serialises it to ISO-8601 (`P30D`, `PT24H`, etc.) before writing the attribute.

```java
relativeTime.setThreshold(Duration.ofDays(30)); // → threshold="PT720H"
```

(Java's `Duration.toString()` has no day component, so `Duration.ofDays(30)` serialises to `PT720H`. The upstream element treats `PT720H` and `P30D` as equivalent, so the behaviour is identical to the documented `P30D` default.)

**Threshold is only consulted when `format=auto`/`relative` AND `tense=auto`.** Setting `tense=PAST` or `tense=FUTURE` commits the element to relative phrasing regardless of how far away the target is, so the threshold has no effect. Likewise, explicit non-relative formats (`DURATION`, `MICRO`, `ELAPSED`, `DATETIME`) ignore it. This is a common footgun: pairing `setTense(FUTURE)` with `setThreshold(...)` silently makes the threshold inert.

A negative `Duration` is rejected with `IllegalArgumentException`: Java serialises it as `PT-nS`, which the upstream duration parser rejects, silently reverting to the default `P30D`. Zero (`PT0S`) is allowed and means "always absolute".

### 2.5 Prefix

The `prefix` attribute (default `"on"`) is the word prepended to absolute dates once the threshold is crossed. `setPrefix("")` drops the prefix entirely; `setPrefix(null)` restores the default.

(Future-tense phrasing like "in 3 days" comes from `Intl.RelativeTimeFormat`, not from `prefix`.)

### 2.6 Locale

`setLocale(Locale)` writes the `lang` attribute on the element. The browser's `Intl.RelativeTimeFormat` and `Intl.DateTimeFormat` produce the localized phrasing ("hace 4 horas", "in 4 ore"). When no locale is set, the browser uses the document language.

**DOM inheritance.** When the element has no `lang` of its own, the upstream walks the DOM ancestors via `closest('[lang]')` and falls back to the `<html>` element's `lang`. This means an app-wide locale set on `<html lang="es">` (or via Vaadin's `UI.setLocale(...)`, which forwards to the document) is automatically picked up by every `RelativeTime` without per-instance configuration.

### 2.7 Time-Zone and Absolute-Date Formatting

These attributes only take effect when `format=datetime` (i.e. when the absolute date is rendered, either explicitly or after the threshold is crossed). They are not relevant for purely relative output.

| Setter | Attribute | Java type / values |
|--------|-----------|--------------------|
| `setTimeZone(ZoneId)` | `time-zone` | IANA name (e.g. `America/New_York`); validated against `ZoneId.getAvailableZoneIds()` (includes `UTC`). Offset-based zones (`ZoneOffset` like `+02:00`/`Z`, or `GMT+02:00`) throw `IllegalArgumentException`, because the browser's `Intl.DateTimeFormat` rejects them with a `RangeError` that breaks rendering. `null` removes it; when unset, the upstream walks DOM ancestors via `closest('[time-zone]')` and falls back to the `<html>` element's `time-zone` attribute, mirroring the `lang` inheritance pattern. |
| `setTimeZoneName(TimeZoneName)` | `time-zone-name` | `LONG`, `SHORT`, `SHORT_OFFSET`, `LONG_OFFSET`, `SHORT_GENERIC`, `LONG_GENERIC` |
| `setYear(DateTimePartStyle)` | `year` | `NUMERIC`, `TWO_DIGIT` |
| `setMonth(DateTimePartStyle)` | `month` | `NUMERIC`, `TWO_DIGIT`, `NARROW`, `SHORT`, `LONG` |
| `setDay(DateTimePartStyle)` | `day` | `NUMERIC`, `TWO_DIGIT` |
| `setWeekday(DateTimePartStyle)` | `weekday` | `NARROW`, `SHORT`, `LONG` |
| `setHour(DateTimePartStyle)` | `hour` | `NUMERIC`, `TWO_DIGIT` |
| `setMinute(DateTimePartStyle)` | `minute` | `NUMERIC`, `TWO_DIGIT` |
| `setSecond(DateTimePartStyle)` | `second` | `NUMERIC`, `TWO_DIGIT` |

`DateTimePartStyle` is a single enum spanning every value any of the seven date-part attributes can take; per-setter Javadoc documents the subset that's meaningful for that part. Invalid combinations are not validated on the Java side; the upstream element silently falls back.

`TimeZoneName.SHORT_OFFSET` / `LONG_OFFSET` / `SHORT_GENERIC` / `LONG_GENERIC` are written to the wire in camelCase (`shortOffset`, etc.) to match the `Intl.DateTimeFormat` vocabulary.

### 2.8 Live Update Behaviour

The upstream element auto-updates itself: an internal `setTimeout` loop re-renders the displayed phrase on each unit boundary as wall-clock time advances. No Java-side timer is involved. When the user opens a page showing `RelativeTime(Instant.now().minus(Duration.ofHours(2)))`, they'll see the phrase advance from "2 hours ago" to "3 hours ago" without any server round-trip.

The cadence depends on the displayed unit:

- Seconds-level display → one re-render per second
- Minutes-level display → one re-render per minute
- Hours-level display → one re-render per hour
- And so on, getting progressively cheaper as the unit grows.

**Format-specific quirks worth knowing:**

| Format | Live behaviour |
|---|---|
| `RELATIVE` / `AUTO` | **Collapses past times under ~55 seconds to "now"** because of a signed-int threshold in the element source (`int < 10` with negative ints, so every past second-unit elapsed counts as "now"). Then jumps to "1 minute ago" when the duration rounds up. Use this format when you want human-friendly relative phrasing and don't care about sub-minute precision. |
| `DURATION` / `ELAPSED` | Tick every second from zero (`0s`, `1s`, `2s`, …). No "now" plateau. Right choice for live elapsed-time displays (timers, "uptime" counters). |
| `MICRO` | Compact single-unit form (`5s`, `2m`, `3h`, `5d`). Re-renders only when the displayed unit changes, so it ticks less frequently than `DURATION`. |
| `DATETIME` | Does not tick; absolute dates don't change. |

**Browser-imposed limits:** when a tab is backgrounded, browsers throttle `setTimeout` callbacks to roughly once per minute. A `RelativeTime` in a hidden tab will look frozen and "catch up" only when the tab regains focus. This is browser behaviour; nothing the wrapper or the element can override.

## 3. API Design

### 3.1 Construction

```java
// Empty: datetime can be set later
RelativeTime rt = new RelativeTime();
add(rt);

// From any supported java.time type
add(new RelativeTime(Instant.now().plus(4, ChronoUnit.HOURS)));
add(new RelativeTime(LocalDate.of(2025, 1, 1)));
```

### 3.2 Setting the Target Date

```java
public class RelativeTime extends Component {                 // HasStyle inherited from Component

    public RelativeTime();                                     // empty; datetime can be set later
    public RelativeTime(Instant datetime);
    public RelativeTime(OffsetDateTime datetime);
    public RelativeTime(ZonedDateTime datetime);
    public RelativeTime(LocalDateTime datetime);               // uses ZoneId.systemDefault()
    public RelativeTime(LocalDate date);                       // uses ZoneId.systemDefault()

    public RelativeTime setDateTime(Instant datetime);
    public RelativeTime setDateTime(OffsetDateTime datetime);
    public RelativeTime setDateTime(ZonedDateTime datetime);
    public RelativeTime setDateTime(LocalDateTime datetime);   // uses ZoneId.systemDefault()
    public RelativeTime setDateTime(LocalDate date);           // uses ZoneId.systemDefault()
    public RelativeTime clear();                               // clears datetime (no cast needed)
    public Instant getDateTime();                              // last value pushed, in UTC
}
```

### 3.3 Display Configuration

```java
// Relative-format controls
public RelativeTime setTense(Tense tense);
public RelativeTime setFormat(Format format);
public RelativeTime setPrecision(Precision precision);
public RelativeTime setFormatStyle(FormatStyle style);
public RelativeTime setThreshold(Duration threshold);
public RelativeTime setPrefix(String prefix);                // "" drops prefix; null restores default
public RelativeTime setNoTitle(boolean noTitle);             // suppress the absolute-date tooltip
public RelativeTime setLocale(Locale locale);

// Absolute-format controls (apply when format=DATETIME)
public RelativeTime setTimeZone(ZoneId timeZone);
public RelativeTime setTimeZoneName(TimeZoneName value);
public RelativeTime setYear(DateTimePartStyle style);
public RelativeTime setMonth(DateTimePartStyle style);
public RelativeTime setDay(DateTimePartStyle style);
public RelativeTime setWeekday(DateTimePartStyle style);
public RelativeTime setHour(DateTimePartStyle style);
public RelativeTime setMinute(DateTimePartStyle style);
public RelativeTime setSecond(DateTimePartStyle style);
```

All setters return `this` to support fluent chaining. All reference-typed setters accept `null` to clear the corresponding attribute (falling back to the element's default). `setNoTitle(false)` is the no-op / clear equivalent for the boolean setter.

### 3.4 Usage Example

```java
RelativeTime deadline = new RelativeTime(task.getDueDate())
    .setTense(Tense.AUTO)
    .setFormat(Format.RELATIVE)
    .setFormatStyle(FormatStyle.LONG)
    .setThreshold(Duration.ofDays(30))   // switch to absolute date past a month
    .setLocale(UI.getCurrent().getLocale());

add(new Span("Due "), deadline);
```

## 4. Default Behavior

When no configuration is applied:

- The element renders the relative string in the browser's current language.
- `format` is `auto` (equivalent to `relative`), `tense` is `auto`, `precision` is `second`, `threshold` is `P30D`, `prefix` is `"on"`.
- `time-zone` is unset, so absolute-date output uses the viewer's browser default zone.
- The `title` attribute is set automatically to the absolute formatted date and is surfaced as a native tooltip.
- The element auto-updates on its own timer; no polling code is needed on the Java side.
- `RelativeTime` with no `datetime` set renders as an empty inline element.

## 5. Theming

The component is rendered as inline text and inherits font, color, and size from its parent. It is compatible with both **Lumo** and **Aura** with no theme-specific CSS. `HasStyle` (inherited from `Component`) is available for class-name and inline-style adjustments. `HasSize` is intentionally **not** implemented: the host renders inline, so `width`/`height` would be no-ops; size the surrounding container or set `display` via `getStyle()` if explicit sizing is needed.

## 6. Serialization

`RelativeTime` must be fully serializable for Vaadin session persistence. The only Java-side state is the last-applied `Instant` (backing `getDateTime()`); all other configuration lives on the `Element`'s attributes and is serialised as part of the Vaadin state node. All inputs accepted by the setters (`Instant`, `Duration`, `Locale`, `ZoneId`, enums) are themselves `Serializable`.

## 7. Dependencies

- Vaadin Flow (24.x and 25.x).
- npm: `@github/relative-time-element`, currently pinned to `5.0.0` via `@NpmPackage` on the component class.
- Lombok (per Flowing Code convention for new add-ons; not used directly in `RelativeTime` because every setter performs a side-effecting attribute write and cannot be lombok-generated).

