/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The app's main representation of data, using a JTable.
 */
@Component
class PetrolStationsView extends JPanel implements TableModelListener {

  private final PetrolStationsActionView actionView;
  private final JTable dataTable;
  private final JScrollPane dataScrollPane;
  private final JLabel headerLabel;
  private final JLabel lastUpdateLabel;

  private final UserPrefs userPrefs;
  private final Localization l10n;

  @Autowired
  PetrolStationsView(
      PetrolStationsActionView actionView,
      PetrolsStationsTableModel petrolsStationsTableModel,
      UserPrefs userPrefs,
      Localization l10n) {

    this.userPrefs = userPrefs;
    this.l10n = l10n;
    this.actionView = actionView;

    petrolsStationsTableModel.addTableModelListener(this);
    this.dataTable = new JTable(petrolsStationsTableModel);

    this.dataScrollPane = new JScrollPane(dataTable);
    this.headerLabel = new JLabel();
    this.lastUpdateLabel = new JLabel();
  }

  @PostConstruct
  private void initView() {
    setLayout(new BorderLayout(5, 5));
    setOpaque(true);
    setMinimumSize(new Dimension(0, 150));

    userPrefs.registerChangeListener("petrol.preferredtype", this::onUserPrefsChange);

    configureDataTable();
    configureDataScrollPane();
    configureDataControlPanel();

    add(actionView, BorderLayout.LINE_START);
    add(createMainPanel(), BorderLayout.CENTER);
  }

  private JPanel createMainPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(createDataHeaderPanel());
    panel.add(dataScrollPane);

    return panel;
  }

  private JPanel createDataHeaderPanel() {
    // Init label texts
    setHeaderText(userPrefs.readPreferredPetrolType().getReadableName());
    setLastUpdateText(LocalDateTime.MIN);

    headerLabel.setForeground(CustomColor.BOX_HEADER_TEXT);
    lastUpdateLabel.setForeground(CustomColor.BOX_HEADER_TEXT);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(headerLabel);
    panel.add(lastUpdateLabel);
    panel.setMaximumSize(new Dimension(getMaximumSize().width, 25));

    return panel;
  }

  private void setHeaderText(String petrolTypeString) {
    headerLabel.setText(l10n.get("msg.CurrentPricesSortedBy", petrolTypeString));
  }

  private void setLastUpdateText(LocalDateTime lastUpdateAt) {
    // Set display text of last petrol stations update time.
    if (lastUpdateAt == LocalDateTime.MIN) {
      lastUpdateLabel.setText("");
      return;
    }

    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm:ss", l10n.getRegion());

    String lastUpdateStr = formatter.format(lastUpdateAt);
    lastUpdateLabel.setText(l10n.get("msg.LastUpdateAt", lastUpdateStr));
  }

  private void configureDataScrollPane() {
    dataScrollPane.setPreferredSize(new Dimension(0, 400));
    dataScrollPane.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0f, 0f, 0.80f)));
    dataScrollPane.setOpaque(false);
  }

  private void configureDataTable() {
    dataTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.setForeground(CustomColor.TABLE_TEXT);
    dataTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
    dataTable.getTableHeader().setForeground(Color.getHSBColor(0f, 0f, 0.40f));
    dataTable.getTableHeader().setFont(dataTable.getFont().deriveFont(12f));
    dataTable.setFillsViewportHeight(true);
    dataTable.setBorder(BorderFactory.createEmptyBorder());
    dataTable.setShowGrid(false);
    dataTable.setShowVerticalLines(false);
    dataTable.setShowHorizontalLines(true);
    dataTable.setGridColor(Color.getHSBColor(0f, 0f, 0.93f));
    dataTable.setRowHeight(69);
    dataTable.getColumn(l10n.get("tableHeader.Name")).setMinWidth(200);
  }

  private void configureDataControlPanel() {
    int width = 190;
    actionView.setPreferredSize(
        new Dimension(width, dataScrollPane.getHeight()));

    actionView.setMaximumSize(
        new Dimension(width, Short.MAX_VALUE));
  }

  private void onUserPrefsChange(String newValue) {
    setHeaderText(PetrolType.valueOf(newValue).getReadableName());
  }

  // Set last update time display text according to the data event.
  @Override
  public void tableChanged(TableModelEvent e) {
    if (e.getType() == TableModelEvent.INSERT) {
      setLastUpdateText(LocalDateTime.now());
    }
    else if (e.getType() == TableModelEvent.DELETE && dataTable.getModel().getRowCount() == 0) {
      setLastUpdateText(LocalDateTime.MIN);
      repaint();
    }
  }
}
