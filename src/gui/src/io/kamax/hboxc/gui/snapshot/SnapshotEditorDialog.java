package io.kamax.hboxc.gui.snapshot;

import io.kamax.hboxc.gui.builder.JDialogBuilder;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;


public class SnapshotEditorDialog {

    private JDialog mainDialog;

    private JPanel mainPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel descLabel;
    private JTextArea descArea;

    private JPanel buttonsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    public SnapshotEditorDialog(Action saveAction, Action cancelAction) {
        nameLabel = new JLabel("Name");
        nameField = new JTextField(40);
        descLabel = new JLabel("Description");
        descArea = new JTextArea();
        descArea.setLineWrap(true);
        descArea.setRows(10);
        descArea.setBorder(nameField.getBorder());

        mainPanel = new JPanel(new MigLayout());
        mainPanel.add(nameLabel);
        mainPanel.add(nameField, "growx,pushx,wrap");
        mainPanel.add(descLabel);
        mainPanel.add(descArea, "growx,pushx,wrap");

        saveButton = new JButton(saveAction);
        saveButton.setEnabled(false);
        cancelButton = new JButton(cancelAction);

        buttonsPanel = new JPanel(new MigLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainDialog = JDialogBuilder.get("Edit Snapshot", saveButton);
        mainDialog.getContentPane().setLayout(new MigLayout());
        mainDialog.getContentPane().add(mainPanel, "grow,push,wrap");
        mainDialog.getContentPane().add(buttonsPanel, "center, growx");
        mainDialog.getRootPane().setDefaultButton(saveButton);
    }

    public void setDialogTitle(String title) {
        mainDialog.setTitle(title);
    }

    public void setName(String name) {
        nameField.setText(name);
    }

    public void setDescription(String description) {
        descArea.setText(description);
    }

    public JDialog getDialog() {
        return mainDialog;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public String getName() {
        return nameField.getText();
    }

    public String getDescription() {
        return descArea.getText();
    }

}
