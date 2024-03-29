/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
 *
 * https://apps.kamax.io/hyperbox
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.hboxc.gui.net;

import io.kamax.hbox.comm.io.NATRuleIO;
import io.kamax.hbox.hypervisor.vbox.net._NATRule;
import io.kamax.hboxc.gui.builder.IconBuilder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NATRulesView {

    private NATRuleTableModel model;
    private JTable table;
    private JScrollPane tablePane;
    private JButton addButton;
    private JButton remButton;
    private JPanel panel;

    public static NATRulesView get() {
        return new NATRulesView();
    }

    public NATRulesView() {
        JComboBox<String> protocolEditor = new JComboBox<>();
        protocolEditor.addItem("TCP");
        protocolEditor.addItem("UDP");

        model = new NATRuleTableModel();
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(model.getColumnIndex(NATRuleTableModel.PROTOCOL)).setCellEditor(new DefaultCellEditor(protocolEditor));
        tablePane = new JScrollPane(table);
        tablePane.setPreferredSize(table.getPreferredSize());
        addButton = new JButton(IconBuilder.AddIcon);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.add(new NATRuleIO("New Rule", null, null, null, null, null));
            }

        });
        remButton = new JButton(IconBuilder.DelIcon);
        remButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row : table.getSelectedRows()) {
                    model.remove(model.getObjectAtRow(table.convertColumnIndexToModel(row)));
                }
            }

        });
        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(tablePane, "grow, push");
        JPanel buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(addButton, "wrap");
        buttonPanel.add(remButton);
        panel.add(buttonPanel, "top");
    }

    public void setRules(List<_NATRule> rules) {
        model.put(rules);
    }

    public JComponent getComponent() {
        return panel;
    }

    public List<_NATRule> getRules() {
        return new ArrayList<_NATRule>(model.list());
    }
}
