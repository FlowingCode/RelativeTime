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
 * Values for the {@code time-zone-name} attribute on {@code <relative-time>}. Forwarded to
 * {@code Intl.DateTimeFormat} when {@link Format#DATETIME} is in effect.
 */
public enum TimeZoneName {
  /** Localised name ("Pacific Daylight Time"). */
  LONG("long"),
  /** Localised abbreviation ("PDT"). */
  SHORT("short"),
  /** Localised offset ("GMT-8"). */
  SHORT_OFFSET("shortOffset"),
  /** Localised offset ("GMT-08:00"). */
  LONG_OFFSET("longOffset"),
  /** Generic non-location ("PT"). */
  SHORT_GENERIC("shortGeneric"),
  /** Generic non-location ("Pacific Time"). */
  LONG_GENERIC("longGeneric");

  private final String attributeValue;

  TimeZoneName(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  String attributeValue() {
    return attributeValue;
  }
}
