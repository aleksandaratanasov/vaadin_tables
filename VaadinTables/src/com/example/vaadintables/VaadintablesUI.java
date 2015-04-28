package com.example.vaadintables;

import java.util.Iterator;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("vaadintables")
public class VaadintablesUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = VaadintablesUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Table table = new Table();
		table.setSelectable(true);
		table.addStyleName("components-inside");
		table.setColumnCollapsingAllowed(true);
		table.addColumnResizeListener(new Table.ColumnResizeListener(){
			@Override
		    public void columnResize(ColumnResizeEvent event) {
		        // Get the new width of the resized column
		        int width = event.getCurrentWidth();
		        
		        // Get the property ID of the resized column
		        String column = (String) event.getPropertyId();

		        // Do something with the information
		        table.setColumnFooter(column, String.valueOf(width) + "px");
		    }
		});
		        
		// Must be immediate to send the resize events immediately
		table.setImmediate(true);

		/* Define the names and data types of columns.
		 * The "default value" parameter is meaningless here. */
		table.addContainerProperty("ID",            Label.class,     null);
		table.addContainerProperty("Username", 		Label.class, 	 null);
		table.addContainerProperty("Delete", 		CheckBox.class,  null);
		table.addContainerProperty("Comments",      TextField.class, null);
		table.addContainerProperty("Details",       Button.class,    null);
		
		table.addListener(new ItemClickListener() {

		    @Override
		    public void itemClick(ItemClickEvent event) {

		        Property itemProperty = event.getItem().getItemProperty("Username");
		        Notification.show(itemProperty.getValue().toString()); // TODO: Do something with this value.

		    }
		});

		/* Add a few items in the table. */
		for (int i=0; i<100; i++) {
		    // Create the fields for the current table row
		    Label userIdField = new Label(Integer.toString(i), ContentMode.HTML);
		    Label usernameField = new Label("user" + Integer.toString(i), ContentMode.HTML);
		    CheckBox markForDeletionField = new CheckBox("delete");
		    markForDeletionField.setImmediate(true);
		    
		    // Multiline text field. This required modifying the 
		    // height of the table row.
		    TextField commentsField = new TextField();
		    //commentsField.setColumns(3);
		    
		    // The Table item identifier for the row.
		    Integer itemId = new Integer(i);
		    
		    // Create a button and handle its click. A Button does not
		    // know the item it is contained in, so we have to store the
		    // item ID as user-defined data.
		    Button detailsField = new Button("show details");
		    detailsField.setData(itemId);
		    detailsField.addClickListener(new Button.ClickListener() {
		        public void buttonClick(ClickEvent event) {
		            // Get the item identifier from the user-defined data.
		            Integer iid = (Integer)event.getButton().getData();
		            Notification.show("Link " +
		                              iid.intValue() + " clicked.");
		        } 
		    });
		    detailsField.addStyleName("link");
		    
		    // Create the table row.
		    table.addItem(new Object[] {userIdField, usernameField, markForDeletionField,
		                                commentsField, detailsField},
		                  itemId);
		}

		// Show just three rows because they are so high.
		table.setPageLength(3);
//		
//		Button deleteSelectedButton = new Button("Delete", new Button.ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		CheckBox selectAllCheckBox = new CheckBox("Select all", false);
		selectAllCheckBox.setImmediate(true);
		selectAllCheckBox.addBlurListener(new BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				if(!selectAllCheckBox.getValue()) {
					for (Object row : table.getItemIds()) {
						// Get the Property representing a cell
	                    Property propertyDelete = table.getContainerProperty(row, "Delete");
	                    
	                    // Get the value of the Property 
	                    Object valueDelete = propertyDelete.getValue();
	                    CheckBox chb = (CheckBox)valueDelete;
	                    chb.setValue(true);
					}
				}
				else {
					for (Object row : table.getItemIds()) {
						// Get the Property representing a cell
	                    Property propertyDelete = table.getContainerProperty(row, "Delete");
	                    
	                    // Get the value of the Property 
	                    Object valueDelete = propertyDelete.getValue();
	                    CheckBox chb = (CheckBox)valueDelete;
	                    chb.setValue(false);
					}
				}
			}

		});

		layout.addComponent(table);
		
		Button testButton = new Button("Test table iteration", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String message = "";
				for (Object row : table.getItemIds()) {
					// Get the Property representing a cell
                    Property propertyUsername = table.getContainerProperty(row, "Username");
                    Property propertyUserId = table.getContainerProperty(row, "ID");
                    Property propertyDelete = table.getContainerProperty(row, "Delete");
                    
                    // Get the value of the Property 
                    Object valueUsername = propertyUsername.getValue();
                    Object valueUserId = propertyUserId.getValue();
                    Object valueDelete = propertyDelete.getValue();
                    
                    if (((Label) valueUsername).getValue() != null && ((Label) valueUserId).getValue() != null) {
                        message += "[" + ((Label) valueUserId).getValue() + " : "
                        		+ ((Label) valueUsername).getValue() + " | "
                        		+ (((CheckBox)valueDelete).getValue() ? "delete" : "remain") + "] ";
                    }
				}
				
				Label out = new Label(message);
				layout.addComponent(out);
			}
		});
		
		layout.addComponent(selectAllCheckBox);
		layout.addComponent(testButton);
		setContent(layout);
	}

}