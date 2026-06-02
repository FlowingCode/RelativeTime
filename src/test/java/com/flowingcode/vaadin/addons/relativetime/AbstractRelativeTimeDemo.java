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

import com.flowingcode.vaadin.addons.demo.SourceCodeViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Shared scaffolding for the RelativeTime demo views. Both {@link RelativeTimeDemo} and
 * {@link UseCasesDemo} extend this so they don't duplicate the separator helper and the
 * "add a section + wire its fragment highlight" boilerplate.
 *
 * <p>Uses a styled {@link Div} (not {@code <hr>}) for the separator because the upstream Vaadin
 * {@code <hr>} ships with Lumo-specific CSS that doesn't render under Aura or the bare base
 * theme. A {@code <div>} sidesteps every {@code <hr>}-targeted rule.
 */
@SuppressWarnings("serial")
abstract class AbstractRelativeTimeDemo extends Div {

  /**
   * Returns a horizontal-rule-equivalent {@link Div} (class {@code relative-time-demo-separator})
   * that renders the same in Lumo, Aura, and the bare base theme.
   */
  protected static Component separator() {
    Div div = new Div();
    div.addClassName("relative-time-demo-separator");
    return div;
  }

  /**
   * Adds {@code sectionContent} to {@code parent}, preceded by a {@link #separator()} when this
   * isn't the first section, and wires hover-to-highlight on the source fragment named
   * {@code fragmentId}.
   */
  protected static void addHighlightedSection(VerticalLayout parent, String fragmentId,
      Component sectionContent) {
    if (parent.getComponentCount() > 1) {
      parent.add(separator());
    }
    SourceCodeViewer.highlightOnHover(sectionContent, fragmentId);
    parent.add(sectionContent);
  }
}
