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

import com.flowingcode.vaadin.addons.demo.DemoSource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Feature-by-feature showcase of {@link RelativeTime}. Opens with the most commonly-needed
 * features (threshold-based switching, format-style variations, live-update format comparison,
 * localisation, prefix), then expands into a Past/Future date matrix that mirrors the upstream
 * <a href="https://github.com/github/relative-time-element/blob/main/examples/index.html">{@code
 * relative-time-element} examples page</a>: two fixed reference dates ({@code 1970-01-01} and the
 * {@code 2038-01-19} Y2K38 moment) are rendered through each of the three formats
 * ({@link Format#DATETIME}, {@link Format#RELATIVE}, {@link Format#DURATION}) with the same
 * attribute variations the upstream demo uses. Hover any section to highlight its source fragment.
 *
 * <p>For realistic patterns lifted into Vaadin views (Grid, chat bubble, event card, date-picker
 * preview, stopwatch, session-expiry banner), see {@link UseCasesDemo}.
 */
@DemoSource
@PageTitle("Basic Demo")
@SuppressWarnings("serial")
@Route(value = "relative-time/basic", layout = RelativeTimeDemoView.class)
public class RelativeTimeDemo extends AbstractRelativeTimeDemo {

    private static final Instant PAST = Instant.parse("1970-01-01T00:00:00Z");
    private static final Instant FUTURE = Instant.parse("2038-01-19T03:14:08Z");

    public RelativeTimeDemo() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        layout.add(
                new Span(
                        "Common features on top; Past/Future format-attribute matrices below."
                                + " Hover any section to highlight its source."));

        addSection(layout, "threshold", "Threshold", buildThresholdSection());
        addSection(layout, "format-style", "Format style", buildFormatStyleSection());
        addSection(layout, "live-update", "Live update", buildLiveUpdateSection());
        addSection(layout, "localised", "Localised", buildLocalisedSection());
        addSection(layout, "prefix", "Prefix", buildPrefixSection());
        addSection(layout, "past-date", "Past date: 1970-01-01", buildPastDateSection());
        addSection(layout, "future-date", "Future date: 2038-01-19 (Y2K38)",
                buildFutureDateSection());

        add(layout);
    }

    private static void addSection(VerticalLayout parent, String fragmentId, String title,
            Component content) {
        VerticalLayout section = new VerticalLayout(new H4(title), content);
        section.setPadding(false);
        section.setSpacing(false);
        addHighlightedSection(parent, fragmentId, section);
    }

    // --- Threshold -------------------------------------------------------

    // begin-block threshold
    private static Component buildThresholdSection() {
        Instant now = Instant.now();
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(
                new Span(
                        "Beyond the threshold, the element flips from a relative phrase to an absolute"
                                + " date."));
        section.add(row("10 days out, P30D threshold (relative)",
                new RelativeTime(now.plus(10, ChronoUnit.DAYS)).setThreshold(Duration.ofDays(30))));
        section.add(row("60 days out, P30D threshold (absolute)",
                new RelativeTime(now.plus(60, ChronoUnit.DAYS)).setThreshold(Duration.ofDays(30))));
        return section;
    }
    // end-block

    // --- Format style ---------------------------------------------------

    // begin-block format-style
    private static Component buildFormatStyleSection() {
        Instant aWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(
                new Span(
                        "FormatStyle controls how compact the relative phrase is. Same instant rendered"
                                + " three ways."));
        section.add(row("LONG / SHORT / NARROW (a week ago)",
                new RelativeTime(aWeekAgo).setFormatStyle(FormatStyle.LONG),
                new RelativeTime(aWeekAgo).setFormatStyle(FormatStyle.SHORT),
                new RelativeTime(aWeekAgo).setFormatStyle(FormatStyle.NARROW)));
        return section;
    }
    // end-block

    // --- Live update ----------------------------------------------------

    // begin-block live-update
    private static Component buildLiveUpdateSection() {
        Instant now = Instant.now();

        RelativeTime auto = new RelativeTime(now);
        RelativeTime duration = new RelativeTime(now).setFormat(Format.DURATION);
        RelativeTime micro = new RelativeTime(now).setFormat(Format.MICRO);

        Button restart = new Button("Restart", e -> {
            Instant fresh = Instant.now();
            auto.setDateTime(fresh);
            duration.setDateTime(fresh);
            micro.setDateTime(fresh);
        });

        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(
                new Span(
                        "All three rows below are pinned at the same instant and update by"
                                + " themselves. Format.AUTO (the default) sits at \"now\" for ~55"
                                + " seconds before jumping to \"1 minute ago\" because of an upstream"
                                + " signed-int threshold; DURATION and MICRO tick from zero with no"
                                + " plateau. Use the Restart button to re-pin."));
        section.add(row("Format.AUTO (default)", auto));
        section.add(row("Format.DURATION", duration));
        section.add(row("Format.MICRO", micro));
        section.add(restart);
        return section;
    }
    // end-block

    // --- Localised -------------------------------------------------------

    // begin-block localised
    private static Component buildLocalisedSection() {
        Instant recent = Instant.now().minus(4, ChronoUnit.HOURS);
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(
                new Span(
                        "The lang attribute drives Intl.RelativeTimeFormat / Intl.DateTimeFormat. setLocale"
                                + " on the Java side writes it via locale.toLanguageTag()."));
        section.add(row("English (default browser locale)",
                new RelativeTime(recent)));
        section.add(row("Spanish",
                new RelativeTime(recent).setLocale(Locale.forLanguageTag("es"))));
        section.add(row("Finnish",
                new RelativeTime(recent).setLocale(Locale.forLanguageTag("fi"))));
        section.add(row("Finnish with Format.DATETIME",
                new RelativeTime(recent)
                        .setFormat(Format.DATETIME)
                        .setLocale(Locale.forLanguageTag("fi"))));
        section.add(row("French with Format.DURATION",
                new RelativeTime(recent)
                        .setFormat(Format.DURATION)
                        .setLocale(Locale.forLanguageTag("fr"))));
        return section;
    }
    // end-block

    // --- Prefix ---------------------------------------------------------

    // begin-block prefix
    private static Component buildPrefixSection() {
        Instant farFuture = Instant.now().plus(Duration.ofDays(60));
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(
                new Span(
                        "When the threshold is crossed, the element renders an absolute date prefixed"
                                + " by the word from the prefix attribute (default \"on\"). These rows"
                                + " pin a date 60 days out, past the default P30D threshold, so the"
                                + " prefix is visible."));
        section.add(row("Default prefix (\"on\")",
                new RelativeTime(farFuture)));
        section.add(row("Custom prefix (setPrefix(\"Due\"))",
                new RelativeTime(farFuture).setPrefix("Due")));
        section.add(row("No prefix (setPrefix(\"\"))",
                new RelativeTime(farFuture).setPrefix("")));
        return section;
    }
    // end-block

    // --- Past Date -------------------------------------------------------

    // begin-block past-date
    private static Component buildPastDateSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);

        section.add(new H5("Format.DATETIME"));
        section.add(row("Default",
                new RelativeTime(PAST).setFormat(Format.DATETIME)));
        section.add(row("With time",
                new RelativeTime(PAST).setFormat(Format.DATETIME)
                        .setHour(DateTimePartStyle.NUMERIC)
                        .setMinute(DateTimePartStyle.TWO_DIGIT)
                        .setSecond(DateTimePartStyle.TWO_DIGIT)));
        section.add(row("Customised (all parts)",
                new RelativeTime(PAST).setFormat(Format.DATETIME)
                        .setWeekday(DateTimePartStyle.NARROW)
                        .setYear(DateTimePartStyle.TWO_DIGIT)
                        .setMonth(DateTimePartStyle.NARROW)
                        .setDay(DateTimePartStyle.NUMERIC)
                        .setHour(DateTimePartStyle.NUMERIC)
                        .setMinute(DateTimePartStyle.TWO_DIGIT)
                        .setSecond(DateTimePartStyle.TWO_DIGIT)));

        section.add(new H5("Format.RELATIVE (default)"));
        section.add(row("Default",
                new RelativeTime(PAST)));
        section.add(row("Fixed to future tense",
                new RelativeTime(PAST).setTense(Tense.FUTURE)));

        section.add(new H5("Format.DURATION"));
        section.add(row("Default",
                new RelativeTime(PAST).setFormat(Format.DURATION)));
        section.add(row("Month precision",
                new RelativeTime(PAST).setFormat(Format.DURATION).setPrecision(Precision.MONTH)));

        return section;
    }
    // end-block

    // --- Future Date -----------------------------------------------------

    // begin-block future-date
    private static Component buildFutureDateSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);

        section.add(new H5("Format.DATETIME"));
        section.add(row("Default",
                new RelativeTime(FUTURE).setFormat(Format.DATETIME)));
        section.add(row("With time",
                new RelativeTime(FUTURE).setFormat(Format.DATETIME)
                        .setHour(DateTimePartStyle.NUMERIC)
                        .setMinute(DateTimePartStyle.TWO_DIGIT)
                        .setSecond(DateTimePartStyle.TWO_DIGIT)));
        section.add(row("Customised (all parts)",
                new RelativeTime(FUTURE).setFormat(Format.DATETIME)
                        .setWeekday(DateTimePartStyle.NARROW)
                        .setYear(DateTimePartStyle.TWO_DIGIT)
                        .setMonth(DateTimePartStyle.NARROW)
                        .setDay(DateTimePartStyle.NUMERIC)
                        .setHour(DateTimePartStyle.NUMERIC)
                        .setMinute(DateTimePartStyle.TWO_DIGIT)
                        .setSecond(DateTimePartStyle.TWO_DIGIT)));

        section.add(new H5("Format.RELATIVE (default)"));
        section.add(row("Default",
                new RelativeTime(FUTURE)));
        section.add(row("Fixed to past tense",
                new RelativeTime(FUTURE).setTense(Tense.PAST)));

        section.add(new H5("Format.DURATION"));
        section.add(row("Default",
                new RelativeTime(FUTURE).setFormat(Format.DURATION)));
        section.add(row("Month precision",
                new RelativeTime(FUTURE).setFormat(Format.DURATION).setPrecision(Precision.MONTH)));

        return section;
    }
    // end-block

    // --- shared helper ---------------------------------------------------

    private static HorizontalLayout row(String label, Component... values) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        row.setPadding(false);
        row.addClassName("relative-time-demo-row");
        Span labelSpan = new Span(label + ":");
        labelSpan.addClassName("relative-time-demo-row-label");
        row.add(labelSpan);
        row.add(values);
        return row;
    }
}
