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

/** Values for the {@code format} attribute on {@code <relative-time>}. */
public enum Format {
  /**
   * Relative phrase ("3 days ago"). Uses {@code Intl.RelativeTimeFormat} under the hood and
   * <b>collapses past times under ~55 seconds to "now"</b> regardless of the {@link Precision}
   * setting. This is a signed-int threshold in the upstream element, not something the wrapper
   * can override. If you need continuous second-by-second ticking from zero, use {@link #DURATION}
   * or {@link #MICRO}.
   */
  RELATIVE,
  /**
   * Duration breakdown ("3d 4h", "1m 5s"). Ticks every second from zero with no "now" threshold,
   * making it the right choice for live elapsed-time displays. Re-renders on every second
   * boundary while the displayed value contains seconds.
   */
  DURATION,
  /**
   * Absolute formatted date-time. Does <b>not</b> tick: once rendered, the displayed string
   * doesn't change unless the inputs do. Use {@link RelativeTime#setTimeZone setTimeZone},
   * {@link RelativeTime#setTimeZoneName setTimeZoneName}, and the per-part setters
   * ({@link RelativeTime#setYear setYear}, etc.) to shape the output.
   */
  DATETIME,
  /**
   * Alias for {@link #RELATIVE} (the upstream default). Same behaviour, same "now" plateau for
   * past times under ~55 seconds.
   */
  AUTO,
  /**
   * Elapsed-time style. Routes through the same duration-format path as {@link #DURATION}; ticks
   * second-by-second with no "now" threshold.
   */
  ELAPSED,
  /**
   * Compact single-unit form ("3d", "5h", "2m"). Re-renders only when the displayed unit changes,
   * so it ticks less frequently than {@link #DURATION}. Good when space is tight (badges, table
   * cells).
   */
  MICRO;

  String attributeValue() {
    return AttributeValues.ofName(this);
  }
}
