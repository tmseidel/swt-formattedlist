/*******************************************************************************
 * Copyright (c) 2012 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.remus.widgets.list;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class RichtextList implements ISelectionProvider {

	private static Map<String, StringProvider> providers = new HashMap<String, StringProvider>();

	private Browser browser;

	private String title;

	private boolean titleVisible;

	private String htmlTemplateListImage;

	private String htmlTemplateListPlain;

	private List<ListElement> elements;

	private ListenerList selectionChangedListeners = new ListenerList();

	private String jqueryUrl;

	private abstract static class StringProvider {
		public abstract String getStringFromObj(ListElement element);
	}

	static {
		providers.put("id", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return StringEscapeUtils.escapeJavaScript(element.getId());
			}
		});
		providers.put("header", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getHeader() != null ? element.getHeader() :"";
			}
		});
		providers.put("footer", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getFooter() != null ? element.getFooter() :"";
			}
		});
		providers.put("imageUrl", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getImageUrl();
			}
		});
		providers.put("timeLabel", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getTimeLabel() != null ?  StringEscapeUtils.escapeHtml(element.getTimeLabel()) : "";
			}
		});
		providers.put("timeTooltip", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getTimeTooltip() != null ?  StringEscapeUtils.escapeHtml(element.getTimeTooltip()) : "";
			}
		});
		providers.put("content", new StringProvider() {
			@Override
			public String getStringFromObj(ListElement element) {
				return element.getContent() != null ?  element.getContent() : "";
			}
		});
	}

	/**
	 * BrowserFunction that is called if the wrapped Ckeditor is initialized.
	 * 
	 * @author Tom Seidel <tom.seidel@remus-software.org>
	 */
	private class RenderCompleteFunction extends BrowserFunction {

		public RenderCompleteFunction(Browser browser) {
			super(browser, "_delegate_init");
		}

		@Override
		public Object function(Object[] arguments) {
			initialize();
			return null;
		}

	}

	private class SelectionChangedFunction extends BrowserFunction {

		public SelectionChangedFunction(Browser browser) {
			super(browser, "_delegate_select");
		}

		@Override
		public Object function(Object[] arguments) {
			if (arguments.length > 0) {
				String string = arguments[0].toString();
				for (ListElement element : elements) {
					if (element.getId().equals(string)) {
						SelectionChangedEvent event = new SelectionChangedEvent(
								RichtextList.this, new StructuredSelection(
										element));
						fireSelectionChanged(event);
					}
				}

			}
			return null;
		}

	}

	public RichtextList(Composite parent, int style, String jqueryUrl) {
		this.jqueryUrl = jqueryUrl;
		browser = new Browser(parent, style);
		browser.setMenu(new Menu(browser));
		elements = new ArrayList<ListElement>();
		URL baseUrl;
		try {
			baseUrl = FileLocator.resolve(FileLocator.find(ListActivator
					.getDefault().getBundle(), new Path(
					"/html/initial.html"), Collections.EMPTY_MAP));
			browser.setUrl(baseUrl.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		new RenderCompleteFunction(browser);
	}

	public void initialize() {
		new SelectionChangedFunction(browser);
		addElements(elements);

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {

		browser.execute("$(\"#_title\").html(\""
				+ StringEscapeUtils.escapeJavaScript(title) + "\")");
		this.title = title;
	}

	public boolean isTitleVisible() {
		return titleVisible;
	}

	public void setTitleVisible(boolean titleVisible) {

		browser.execute("$(\".header-inner\").css(\"display\",\""
				+ (titleVisible ? "" : "none") + "\");");
		this.titleVisible = titleVisible;
	}

	public boolean addElement(ListElement elm, int index, boolean fade) {

		removeElement(elm);
		String refId = "";
		if (index >= 0 && index < elements.size()) {
			refId = elements.get(index).getId();
		}

		_internallyAdd(elm, refId, false, fade);
		elements.add(elm);

		return true;

	}

	private void _internallyAdd(ListElement elm, String refId, boolean b,
			boolean c) {
		String html;
		if (elm.getImageUrl()!= null &&  !elm.getImageUrl().isEmpty()) {
			html = getListElementWithImage();
		} else {
			html = getListElementPlain();
		}
		html = replace(html, elm);
		browser.execute("add('" + StringEscapeUtils.escapeJavaScript(html)
				+ "','" + refId + "'," + b + "," + c + ");");

	}

	public void removeElement(ListElement elm) {
		if (elements.contains(elm)) {
			_internallyRemove(elm);
			elements.remove(elm);
		}

	}

	private void _internallyRemove(ListElement element) {
		browser.execute("$(\"#" + providers.get("id").getStringFromObj(element)
				+ "\").remove();");

	}

	public boolean addElements(Collection<ListElement> elements) {
		for (ListElement listElement : elements) {
			addElement(listElement, -1, false);

		}
		return true;
	}

	private String replace(String listElementWithImage, ListElement listElement) {
		Matcher m = Pattern.compile("\\[(.*?)\\]")
				.matcher(listElementWithImage);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {

			// What to replace
			String toReplace = m.group(1);

			// New value to insert
			if (providers.containsKey(toReplace)) {

				String toInsert = providers.get(toReplace).getStringFromObj(
						listElement);

				// Append replaced match.
				m.appendReplacement(sb, "" + toInsert);
			}

		}
		m.appendTail(sb);

		return sb.toString();
	}

	private String getListElementWithImage() {
		if (this.htmlTemplateListImage == null) {
			InputStream resourceAsStream = getClass().getResourceAsStream(
					"list-element-image.html");
			this.htmlTemplateListImage = slurp(resourceAsStream, 4096);
		}
		return htmlTemplateListImage;

	}

	private String getListElementPlain() {
		if (this.htmlTemplateListPlain == null) {
			InputStream resourceAsStream = getClass().getResourceAsStream(
					"list-element-plain.html");
			this.htmlTemplateListPlain = slurp(resourceAsStream, 4096);
		}
		return htmlTemplateListPlain;

	}
	
	private String getInitialHtml() {
		InputStream resourceAsStream = getClass().getResourceAsStream("initial.html");
		String slurp = slurp(resourceAsStream, 4096);
		
		return slurp.replace("[jqueryUrl]", this.jqueryUrl);
	}

	private static String slurp(final InputStream is, final int bufferSize) {
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}

	/**
	 * Notifies any selection changed listeners that the viewer's selection has
	 * changed. Only listeners registered at the time this method is called are
	 * notified.
	 * 
	 * @param event
	 *            a selection changed event
	 * 
	 * @see ISelectionChangedListener#selectionChanged
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	/*
	 * (non-Javadoc) Method declared on ISelectionProvider.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/*
	 * (non-Javadoc) Method declared on ISelectionProvider.
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		setSelection(selection, false);
	}

	public void setSelection(ISelection selection, boolean reveal) {
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (elements.contains(firstElement)) {
				browser.execute("selectRow('"
						+ StringEscapeUtils
								.escapeJavaScript(((ListElement) firstElement)
										.getId()) + "');");
			}
		}
	}

	@Override
	public ISelection getSelection() {
		Object evaluate = browser.evaluate("return getSelectedElement()");
		if (evaluate != null) {
			String string = evaluate.toString();
			for (ListElement element : elements) {
				if (element.getId().equals(string)) {
					return new StructuredSelection(element);
				}
			}
		}
		return StructuredSelection.EMPTY;
	}

	public boolean setFocus() {
		return browser.setFocus();
	}

	public void setEnabled(boolean enabled) {
		browser.setEnabled(enabled);
	}

	public void setForeground(Color color) {
		browser.setForeground(color);
	}

	public void setLayoutData(Object layoutData) {
		browser.setLayoutData(layoutData);
	}

}
