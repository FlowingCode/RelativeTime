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

/**
 * Values for the {@code Intl.DateTimeFormat} date-part attributes ({@code year}, {@code month},
 * {@code day}, {@code hour}, {@code minute}, {@code second}, {@code weekday}). Not every part
 * accepts every value; each setter on {@link RelativeTime} documents its valid subset.
 *
 * <p>Only relevant when {@link Format#DATETIME} is in effect.
 */
public enum DateTimePartStyle {
  /** Numeric ("1", "2", "12"). Accepted by year, month, day, hour, minute, second. */
  NUMERIC("numeric"),
  /** Two-digit, zero-padded ("01", "12"). Accepted by year, month, day, hour, minute, second. */
  TWO_DIGIT("2-digit"),
  /** Narrow ("M", "T"). Accepted by month and weekday. */
  NARROW("narrow"),
  /** Short ("Mon", "Tue"). Accepted by month and weekday. */
  SHORT("short"),
  /** Long ("Monday", "Tuesday"). Accepted by month and weekday. */
  LONG("long");

  private final String attributeValue;

  DateTimePartStyle(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  String attributeValue() {
    return attributeValue;
  }
}
