/*
 * Copyright (C) 2015 Denis Forveille titou10.titou10@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.titou10.jtb.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.titou10.jtb.variable.VariablesUtils;
import org.titou10.jtb.variable.gen.Variable;
import org.titou10.jtb.variable.gen.VariableKind;

/**
 * 
 * Manage the variables
 * 
 * @author Denis Forveille
 *
 */
public class VariablesManageDialog extends Dialog {

   private static final Logger log = LoggerFactory.getLogger(VariablesManageDialog.class);

   private Text                newName;
   private Table               variableTable;

   private List<Variable>      variables;
   private VariableKind        variableKindSelected;

   public VariablesManageDialog(Shell parentShell, List<Variable> variables) {
      super(parentShell);

      setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.PRIMARY_MODAL);

      this.variables = variables;
   }

   @Override
   protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setSize(900, 600);
      newShell.setText("Manage Variables");
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, "Save", true);
      createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite container = (Composite) super.createDialogArea(parent);
      container.setLayout(new GridLayout(1, false));

      Composite addComposite = new Composite(container, SWT.NONE);
      addComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      GridLayout glAdd = new GridLayout(3, false);
      glAdd.marginWidth = 0;
      addComposite.setLayout(glAdd);

      Label lblNewLabel = new Label(addComposite, SWT.NONE);
      lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
      lblNewLabel.setAlignment(SWT.CENTER);
      lblNewLabel.setBounds(0, 0, 49, 13);
      lblNewLabel.setText("Name");

      Label lbl1 = new Label(addComposite, SWT.NONE);
      lbl1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
      lbl1.setAlignment(SWT.CENTER);
      lbl1.setBounds(0, 0, 49, 13);
      lbl1.setText("Kind");
      new Label(addComposite, SWT.NONE);

      newName = new Text(addComposite, SWT.BORDER);
      newName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
      newName.setBounds(0, 0, 76, 19);

      final Combo newKindCombo = new Combo(addComposite, SWT.READ_ONLY);

      Button btnAddVariable = new Button(addComposite, SWT.NONE);
      btnAddVariable.setText("Add...");

      // Table with variables

      Composite compositeList = new Composite(container, SWT.NONE);
      compositeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      TableColumnLayout tcListComposite = new TableColumnLayout();
      compositeList.setLayout(tcListComposite);

      final TableViewer variableTableViewer = new TableViewer(compositeList, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
      variableTable = variableTableViewer.getTable();
      variableTable.setHeaderVisible(true);
      variableTable.setLinesVisible(true);

      TableViewerColumn systemViewerColumn = new TableViewerColumn(variableTableViewer, SWT.NONE);
      TableColumn systemColumn = systemViewerColumn.getColumn();
      systemColumn.setAlignment(SWT.CENTER);
      tcListComposite.setColumnData(systemColumn, new ColumnPixelData(16, true, true));
      systemViewerColumn.setLabelProvider(new ColumnLabelProvider() {
         @Override
         public String getText(Object element) {
            Variable v = (Variable) element;
            return v.isSystem() ? "*" : "";
         }
      });

      TableViewerColumn nameViewerColumn = new TableViewerColumn(variableTableViewer, SWT.NONE);
      TableColumn nameColumn = nameViewerColumn.getColumn();
      tcListComposite.setColumnData(nameColumn, new ColumnWeightData(4, 100, true));
      nameColumn.setText("Name");
      nameViewerColumn.setLabelProvider(new ColumnLabelProvider() {
         @Override
         public String getText(Object element) {
            Variable v = (Variable) element;
            return v.getName();
         }
      });

      TableViewerColumn kindViewerColumn = new TableViewerColumn(variableTableViewer, SWT.NONE);
      TableColumn kindColumn = kindViewerColumn.getColumn();
      tcListComposite.setColumnData(kindColumn, new ColumnWeightData(1, 100, true));
      kindColumn.setText("Kind");
      kindViewerColumn.setLabelProvider(new ColumnLabelProvider() {
         @Override
         public String getText(Object element) {
            Variable v = (Variable) element;
            return v.getKind().name();
         }
      });

      TableViewerColumn definitionViewerColumn = new TableViewerColumn(variableTableViewer, SWT.NONE);
      TableColumn definitionColumn = definitionViewerColumn.getColumn();
      tcListComposite.setColumnData(definitionColumn, new ColumnWeightData(12, 100, true));
      definitionColumn.setText("Definition");
      definitionViewerColumn.setLabelProvider(new ColumnLabelProvider() {
         @Override
         public String getText(Object element) {
            Variable v = (Variable) element;
            return VariablesUtils.buildDescription(v);
         }
      });

      variableTableViewer.setContentProvider(ArrayContentProvider.getInstance());

      // ----------
      // Set values
      // ----------
      String[] vkNames = new String[VariableKind.values().length];
      int i = 0;
      for (VariableKind kind : VariableKind.values()) {
         vkNames[i++] = kind.name();
      }
      newKindCombo.setItems(vkNames);
      int sel = 3; // STRING
      newKindCombo.select(sel);
      variableKindSelected = VariableKind.values()[sel];

      variableTableViewer.setInput(variables);

      // ----------
      // Behavior
      // ----------

      btnAddVariable.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            log.debug("Add selected");
            String n = newName.getText().trim();
            if (n.isEmpty()) {
               MessageDialog.openInformation(getShell(), "Missing Name", "Please enter first a variable name");
               return;
            }

            // Check for duplicates
            for (Variable v : variables) {
               if (v.getName().equals(n)) {
                  MessageDialog.openError(getShell(), "Duplicate Name", "A variable with this name already exist");
                  return;
               }

            }

            switch (variableKindSelected) {
               case DATE:
                  VariablesDateDialog d1 = new VariablesDateDialog(getShell());
                  if (d1.open() != Window.OK) {
                     return;
                  }
                  log.debug("pattern : {} min: {} max: {}", d1.getPattern(), d1.getMin(), d1.getMax());
                  Variable v = VariablesUtils.buildDateVariable(false,
                                                                n,
                                                                d1.getKind(),
                                                                d1.getPattern(),
                                                                d1.getMin(),
                                                                d1.getMax(),
                                                                d1.getOffset(),
                                                                d1.getOffsetTU());
                  variables.add(v);
                  variableTableViewer.refresh();
                  break;

               case INT:
                  VariablesIntDialog d2 = new VariablesIntDialog(getShell());
                  if (d2.open() != Window.OK) {
                     return;
                  }
                  variables.add(VariablesUtils.buildIntVariable(false, n, d2.getMin(), d2.getMax()));
                  variableTableViewer.refresh();
                  break;

               case LIST:
                  VariablesListDialog d3 = new VariablesListDialog(getShell());
                  if (d3.open() != Window.OK) {
                     return;
                  }
                  variables.add(VariablesUtils.buildListVariable(false, n, d3.getValues()));
                  variableTableViewer.refresh();
                  break;

               case STRING:
                  VariablesStringDialog d4 = new VariablesStringDialog(getShell());
                  if (d4.open() != Window.OK) {
                     return;
                  }
                  variables.add(VariablesUtils.buildStringVariable(false, n, d4.getKind(), d4.getLength(), d4.getCharacters()));
                  variableTableViewer.refresh();
                  break;
            }
         }
      });

      variableTable.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            if (e.keyCode == SWT.DEL) {
               IStructuredSelection selection = (IStructuredSelection) variableTableViewer.getSelection();
               if (selection.isEmpty()) {
                  return;
               }
               for (Object sel : selection.toList()) {
                  Variable v = (Variable) sel;
                  if (v.isSystem()) {
                     continue;
                  }
                  log.debug("Remove {} from the list", v);
                  variables.remove(v);
               }
               variableTableViewer.refresh();
            }
         }
      });

      // Save the selected property Kind
      newKindCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent arg0) {
            String sel = newKindCombo.getItem(newKindCombo.getSelectionIndex());
            variableKindSelected = VariableKind.valueOf(sel);
         }
      });

      return container;
   }
}
