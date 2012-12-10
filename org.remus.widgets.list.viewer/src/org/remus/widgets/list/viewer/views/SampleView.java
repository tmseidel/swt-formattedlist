package org.remus.widgets.list.viewer.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.remus.widgets.list.ListElement;
import org.remus.widgets.list.RichtextList;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.remus.widgets.list.viewer.views.SampleView";
	private Text txtSelection;
	private RichtextList richtextList;

	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));

		composite.setLayout(new FillLayout());
		richtextList = new RichtextList(composite, SWT.NONE,
				"http://code.jquery.com/jquery-latest.js");

		Button btnRemoveSelectedRow = new Button(parent, SWT.NONE);
		btnRemoveSelectedRow.setText("Remove Selected Row");
		btnRemoveSelectedRow.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Object firstElement = ((IStructuredSelection) richtextList
						.getSelection()).getFirstElement();
				richtextList.removeElement((ListElement) firstElement);

			}
		});

		Button btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.setText("Change header");
		btnNewButton.addListener(SWT.Selection, new Listener() {
			
			private boolean alternate;
			
			@Override
			public void handleEvent(Event event) {
				richtextList.setTitle(alternate ? "New Title set" : "Other title set");
				alternate = !alternate;
				
			}
		});

		Button btnAddNewRow = new Button(parent, SWT.NONE);
		btnAddNewRow.setText("Add new Row");
		btnAddNewRow.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ListElement listElement = new ListElement();
				listElement
						.setContent("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et \n"
								+ "");
				listElement.setHeader("Hello World");
				listElement.setId(String.valueOf(System.currentTimeMillis()));
				listElement.setTimeLabel("now");
				listElement.setTimeTooltip("now");
				richtextList.addElement(listElement, -1, true);

			}
		});
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		txtSelection = new Text(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false,
				3, 1);
		layoutData.heightHint = 150;
		layoutData.minimumHeight = 150;
		txtSelection.setLayoutData(layoutData);

		richtextList
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						txtSelection.append(((IStructuredSelection) event
								.getSelection()).getFirstElement() + "\n");
					}
				});
		
		ListElement listElement = new ListElement();
		listElement
				.setContent("This is a simple list-entry. You can use every kind of formatting, e.g. <strong>bold</strong> or <i>italic</i>.");
		listElement.setHeader("Initial entry - this is the heading");
		listElement.setId(String.valueOf(System.currentTimeMillis()));
		listElement.setTimeLabel("2h ago");
		listElement.setTimeTooltip("2h ago");
		richtextList.addElement(listElement, -1, true);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}
}