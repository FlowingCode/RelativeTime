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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Realistic UI patterns showing how {@link RelativeTime} fits into common Vaadin views:
 * audit-log grids, chat timestamps, event cards, a date-picker preview, a live stopwatch, and
 * a session-expiry banner. Each section is structured so it can be lifted into a real app with
 * minimal changes. Hover over any example to highlight the corresponding source fragment.
 */
@DemoSource
@PageTitle("Use Cases")
@SuppressWarnings("serial")
@Route(value = "use-cases", layout = RelativeTimeDemoView.class)
public class UseCasesDemo extends AbstractRelativeTimeDemo {

  public UseCasesDemo() {
    VerticalLayout layout = new VerticalLayout();
    layout.setPadding(false);
    layout.setSpacing(true);

    layout.add(
        new Span(
            "Realistic UI patterns where RelativeTime fits naturally. Each timestamp re-renders"
                + " in the browser as time passes. Each example uses the format best suited to its"
                + " pattern: AUTO for human-friendly feeds, DURATION for stopwatches,"
                + " future-tense with a threshold for countdowns, and MICRO for tight spaces."
                + " Hover over any example to highlight the corresponding source fragment on"
                + " the right."));

    addUseCase(layout, "audit-log-grid",
        "Audit log Grid", buildAuditLogGrid(),
        "Vaadin Grid with a RelativeTime in a component column. Drops in alongside regular"
            + " columns and scrolls smoothly. Each row's timestamp is a tiny custom element"
            + " that handles its own updates, so the Grid stays cheap to render even with"
            + " thousands of rows. Pattern: audit logs, change history, activity tables in"
            + " admin UIs.");

    addUseCase(layout, "chat-messages",
        "Chat messages", buildChatMessages(),
        "Bubble-style messages with the timestamp tucked inside each bubble, formatted as"
            + " Format.MICRO (e.g. \"3m\", \"45s\"). Pattern: any chat, DM, or comment thread"
            + " where you want a glance-able timestamp embedded in the message without"
            + " consuming a whole column.");

    addUseCase(layout, "event-card",
        "Upcoming event card", buildEventCard(),
        "Future-tense relative time inside a card. The element's auto-tense gives \"in 2 hours\""
            + " for a future instant. Pattern: calendar widgets, meeting cards, reservation"
            + " summaries.");

    addUseCase(layout, "date-time-picker",
        "Date picker preview", buildDateTimePickerSection(),
        "DateTimePicker driving a RelativeTime preview through a value-change listener; a"
            + " locale combo drives setLocale on the same preview. A 30-day threshold flips the"
            + " preview to an absolute date for far-away picks. Pattern: any form field with a"
            + " date input where you want an \"in X days\" preview next to the input.");

    addUseCase(layout, "stopwatch",
        "Live stopwatch", buildStopwatch(),
        "A running counter using Format.DURATION (ticks every second from 0s with no \"now\""
            + " plateau). The Start button pins the datetime to now; Stop clears it. Pattern:"
            + " timers for in-progress work, build/deploy status, \"uptime since\" indicators.");

    addUseCase(layout, "session-expiry",
        "Session expiry warning", buildSessionWarning(),
        "Banner with a future-tense countdown. The \"Stay logged in\" button re-pins the"
            + " datetime, restarting the countdown. Pattern: any time-limited UI such as"
            + " session expiry, OTP or code expiry, auction or sale countdowns.");

    add(layout);
  }

  private static void addUseCase(VerticalLayout parent, String fragmentId, String title,
      Component content, String explanation) {
    Component block = useCase(title, content, explanation);
    addHighlightedSection(parent, fragmentId, block);
  }

  // --- Audit log Grid ----------------------------------------------------

  private record AuditLog(String user, String action, Instant when) {}

  // begin-block audit-log-grid
  private static Component buildAuditLogGrid() {
    Instant now = Instant.now();
    List<AuditLog> rows =
        List.of(
            new AuditLog("alice", "Created project \"Atlas\"", now.minus(Duration.ofMinutes(2))),
            new AuditLog("bob", "Updated permissions on /repos", now.minus(Duration.ofMinutes(8))),
            new AuditLog("carol", "Deleted user 'temp123'", now.minus(Duration.ofMinutes(35))),
            new AuditLog("dave", "Rotated API key", now.minus(Duration.ofHours(2))),
            new AuditLog("alice", "Approved PR #142", now.minus(Duration.ofHours(5))),
            new AuditLog("eve", "Logged in from new IP", now.minus(Duration.ofDays(1))),
            new AuditLog("bob", "Changed billing email", now.minus(Duration.ofDays(3))),
            new AuditLog("carol", "Exported user database", now.minus(Duration.ofDays(7))));

    Grid<AuditLog> grid = new Grid<>(AuditLog.class, false);
    grid.setItems(rows);
    grid.addColumn(AuditLog::user).setHeader("User").setAutoWidth(true);
    grid.addColumn(AuditLog::action).setHeader("Action").setFlexGrow(1);
    grid.addComponentColumn(log -> new RelativeTime(log.when()))
        .setHeader("When")
        .setAutoWidth(true);
    grid.setAllRowsVisible(true);
    return grid;
  }
  // end-block

  // --- Chat messages -----------------------------------------------------

  // begin-block chat-messages
  private static Component buildChatMessages() {
    Instant now = Instant.now();
    VerticalLayout chat =
        new VerticalLayout(
            chatBubble("Hi! Got a minute?", now.minus(Duration.ofMinutes(3)), true),
            chatBubble("Sure, what's up?", now.minus(Duration.ofMinutes(2)), false),
            chatBubble("Want to grab lunch?", now.minus(Duration.ofSeconds(45)), true));
    chat.setPadding(false);
    chat.setSpacing(true);
    chat.addClassName("relative-time-demo-chat");
    return chat;
  }

  private static HorizontalLayout chatBubble(String text, Instant when, boolean sent) {
    Span body = new Span(text);

    RelativeTime stamp = new RelativeTime(when).setFormat(Format.MICRO);
    stamp.addClassName("relative-time-demo-chat-bubble-stamp");

    VerticalLayout bubble = new VerticalLayout(body, stamp);
    bubble.setPadding(false);
    bubble.setSpacing(false);
    bubble.addClassName("relative-time-demo-chat-bubble");
    bubble.addClassName(sent ? "relative-time-demo-chat-bubble-sent" : "relative-time-demo-chat-bubble-received");

    HorizontalLayout row = new HorizontalLayout(bubble);
    row.setWidthFull();
    row.setJustifyContentMode(sent ? JustifyContentMode.END : JustifyContentMode.START);
    return row;
  }
  // end-block

  // --- Date picker preview ----------------------------------------------

  // begin-block date-time-picker
  private static Component buildDateTimePickerSection() {
    DateTimePicker picker = new DateTimePicker("Pick a deadline");

    ComboBox<Locale> localeCombo = new ComboBox<>("Locale");
    localeCombo.setItems(
        List.of(
            Locale.ENGLISH,
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("it"),
            Locale.forLanguageTag("fr")));
    localeCombo.setItemLabelGenerator(loc -> loc.getDisplayName(Locale.ENGLISH));

    RelativeTime preview =
        new RelativeTime()
            .setTense(Tense.AUTO)
            .setFormatStyle(FormatStyle.LONG)
            .setThreshold(Duration.ofDays(30));

    picker.addValueChangeListener(e -> preview.setDateTime(e.getValue()));
    localeCombo.addValueChangeListener(e -> preview.setLocale(e.getValue()));

    // Initial state: 5 days from now, English. Within the 30-day threshold,
    // so the preview starts as a relative phrase.
    picker.setValue(LocalDateTime.now().plusDays(5));
    localeCombo.setValue(Locale.ENGLISH);

    HorizontalLayout controls = new HorizontalLayout(picker, localeCombo);
    controls.setAlignItems(Alignment.END);

    Div previewRow = new Div(new Span("Deadline: "), preview);

    VerticalLayout section = new VerticalLayout(controls, previewRow);
    section.setPadding(false);
    section.setSpacing(true);
    return section;
  }
  // end-block

  // --- Upcoming event card ----------------------------------------------

  // begin-block event-card
  private static Component buildEventCard() {
    Instant inTwoHours = Instant.now().plus(Duration.ofHours(2));

    Span title = new Span("Daily Standup");
    title.addClassName("relative-time-demo-event-card-title");

    Div timeLine = new Div(new Span("starts "), new RelativeTime(inTwoHours));

    Span location = new Span("Conference room A");
    location.addClassName("relative-time-demo-event-card-location");

    VerticalLayout card = new VerticalLayout(title, timeLine, location);
    card.setPadding(true);
    card.setSpacing(false);
    card.addClassName("relative-time-demo-event-card");
    return card;
  }
  // end-block

  // --- Live stopwatch ----------------------------------------------------

  // begin-block stopwatch
  private static Component buildStopwatch() {
    RelativeTime elapsed = new RelativeTime().setFormat(Format.DURATION);
    Span placeholder = new Span("...");
    placeholder.addClassName("relative-time-demo-stopwatch-placeholder");

    Div display = new Div(new Span("Elapsed: "), placeholder, elapsed);

    Button startStop = new Button("Start");
    startStop.addClickListener(
        e -> {
          if (elapsed.getDateTime() != null) {
            elapsed.setDateTime((Instant) null);
            placeholder.setVisible(true);
            startStop.setText("Start");
          } else {
            elapsed.setDateTime(Instant.now());
            placeholder.setVisible(false);
            startStop.setText("Stop");
          }
        });

    VerticalLayout layout = new VerticalLayout(display, startStop);
    layout.setPadding(false);
    layout.setSpacing(true);
    return layout;
  }
  // end-block

  // --- Session expiry warning -------------------------------------------

  // begin-block session-expiry
  private static Component buildSessionWarning() {
    RelativeTime expiry = new RelativeTime(Instant.now().plus(Duration.ofMinutes(14)));

    Span icon = new Span("⚠️");
    icon.addClassName("relative-time-demo-session-banner-icon");

    Div textRow = new Div(icon, new Span("Your session expires "), expiry, new Span("."));

    Button stayLoggedIn = new Button("Stay logged in");
    stayLoggedIn.addClickListener(
        e -> expiry.setDateTime(Instant.now().plus(Duration.ofMinutes(14))));

    HorizontalLayout banner = new HorizontalLayout(textRow, stayLoggedIn);
    banner.setWidthFull();
    banner.setJustifyContentMode(JustifyContentMode.BETWEEN);
    banner.setAlignItems(Alignment.CENTER);
    banner.addClassName("relative-time-demo-session-banner");
    return banner;
  }
  // end-block

  // --- shared helper -----------------------------------------------------

  private static Component useCase(String title, Component content, String explanation) {
    VerticalLayout block = new VerticalLayout();
    block.setPadding(false);
    block.setSpacing(false);

    Span titleSpan = new Span(title);
    titleSpan.addClassName("relative-time-demo-use-case-title");

    content.getElement().getClassList().add("relative-time-demo-use-case-content");

    Span explanationSpan = new Span(explanation);
    explanationSpan.addClassName("relative-time-demo-use-case-explanation");

    block.add(titleSpan, content, explanationSpan);
    return block;
  }
}
