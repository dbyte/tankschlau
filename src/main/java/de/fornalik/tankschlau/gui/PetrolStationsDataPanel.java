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

import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The app's main representation of data, using a JTable.
 */
class PetrolStationsDataPanel extends JPanel {
  private static final Localization L10N = Localization.getInstance();

  private final UserPrefs userPrefs;

  private final JPanel dataControlPanel;
  private final JTable dataTable;
  private final JScrollPane dataScrollPane;
  private final JLabel headerLabel;

  PetrolStationsDataPanel(
      FooterPanel footerPanel,
      UserPrefs userPrefs,
      WorkerService<List<PetrolStation>> petrolStationsWorkerService) {

    this.userPrefs = userPrefs;

    PetrolsStationsTableModel dataTableModel = new PetrolsStationsTableModel(userPrefs);
    this.dataTable = new JTable(dataTableModel);

    this.dataScrollPane = new JScrollPane(dataTable);
    this.dataControlPanel = new PetrolStationsControlPanel(
        dataTableModel,
        footerPanel,
        userPrefs,
        petrolStationsWorkerService);

    this.headerLabel = new JLabel();

    initView();
  }

  private void initView() {
    setLayout(new BorderLayout(5, 5));
    setOpaque(true);
    setMinimumSize(new Dimension(0, 150));

    userPrefs.registerChangeListener("petrol.preferredtype", this::onUserPrefsChange);

    configureDataTable();
    configureDataScrollPane();
    configureDataControlPanel();

    add(dataControlPanel, BorderLayout.LINE_START);
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
    setHeaderText(userPrefs.readPreferredPetrolType().getReadableName());
    headerLabel.setForeground(CustomColor.BOX_HEADER_TEXT);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(headerLabel);
    panel.setMaximumSize(new Dimension(getMaximumSize().width, 25));

    return panel;
  }

  private void setHeaderText(String petrolTypeString) {
    headerLabel.setText(L10N.get("msg.CurrentPricesSortedBy", petrolTypeString));
  }

  private void configureDataScrollPane() {
    dataScrollPane.setPreferredSize(new Dimension(0, 400));
    dataScrollPane.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0f, 0f, 0.80f)));
    dataScrollPane.setOpaque(false);
  }

  private void configureDataTable() {
    // dataTable.setPreferredScrollableViewportSize(new Dimension(0, 350));
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
    dataTable.getColumn(PetrolsStationsTableModel.COL_NAME).setMinWidth(200);
  }

  private void configureDataControlPanel() {
    int width = 190;
    dataControlPanel.setPreferredSize(
        new Dimension(width, dataScrollPane.getHeight()));

    dataControlPanel.setMaximumSize(
        new Dimension(width, Short.MAX_VALUE));
  }

  private void onUserPrefsChange(String newValue) {
    setHeaderText(PetrolType.valueOf(newValue).getReadableName());
  }
}
