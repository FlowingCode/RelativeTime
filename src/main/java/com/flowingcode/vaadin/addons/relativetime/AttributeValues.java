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

import java.util.Locale;

/** Internal helpers for mapping enum constants to their {@code <relative-time>} wire values. */
final class AttributeValues {

  private AttributeValues() {}

  /**
   * Returns the enum constant's name in lower case (e.g. {@code FUTURE} &rarr; {@code "future"}),
   * the default wire form for enums whose attribute value matches the constant name.
   */
  static String ofName(Enum<?> value) {
    return value.name().toLowerCase(Locale.ROOT);
  }
}
