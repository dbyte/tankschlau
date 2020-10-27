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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;

import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Handles the domain table model and its data, which is a result of the WorkerService and
 * its owned Worker.
 */
class PetrolsStationsTableModel extends AbstractTableModel implements Serializable {
  public static final String COL_NAME = "Name";
  public static final String COL_PRICES = "Preis";
  public static final String COL_STREET = "Ort";
  public static final String COL_DISTANCE = "Entfernung";
  public static final String COL_IS_OPEN = "Status";
  private static final Localization L10N = Localization.getInstance();
  private static final Logger LOGGER = Logger.getLogger(PetrolsStationsTableModel.class.getName());
  private static final String[] columns = new String[5];

  private final List<PetrolStation> petrolStations;
  private final UserPrefs userPrefs;

  PetrolsStationsTableModel(UserPrefs userPrefs) {
    super();
    this.userPrefs = userPrefs;
    this.userPrefs.registerChangeListener("petrol.preferredtype", this::sortPetrolStations);
    this.petrolStations = new ArrayList<>();
    this.initColumnIdentifiers();
  }

  private void initColumnIdentifiers() {
    columns[0] = COL_NAME;
    columns[1] = COL_PRICES;
    columns[2] = COL_STREET;
    columns[3] = COL_DISTANCE;
    columns[4] = COL_IS_OPEN;
  }

  @Override
  public String getColumnName(int forIndex) {
    return columns[forIndex];
  }

  @Override
  public int getRowCount() {
    return petrolStations.size();
  }

  @Override
  public int getColumnCount() {
    return columns.length;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    PetrolStation record = petrolStations.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return record.getAddress().getName();

      case 1:
        return petrolsToHtml(record.getPetrols());

      case 2:
        return record.getAddress().getStreet();

      case 3:
        return record.getAddress()
            .getGeo()
            .map(Geo::getDistanceAwayString)
            .orElse(L10N.get("msg.Unknown"));

      case 4:
        return record.isOpen() ? L10N.get("msg.NowOpen") : L10N.get("msg.NowClosed");

      default:
        return "Unregistered column index: " + columnIndex;
    }
  }

  synchronized void removeAllPetrolStations() {
    this.petrolStations.clear();
    fireTableDataChanged();
  }

  synchronized void addPetrolStations(List<PetrolStation> petrolStations) {
    this.removeAllPetrolStations();

    if (petrolStations.size() == 0) return;
    int rowCountBeforeInsert = getRowCount();

    this.petrolStations.addAll(petrolStations);
    fireTableRowsInserted(rowCountBeforeInsert, petrolStations.size());
    this.sortPetrolStations();
  }

  private void sortPetrolStations() {
    PetrolStations
        .sortByPriceAndDistanceForPetrolType(petrolStations, userPrefs.readPreferredPetrolType());
    fireTableDataChanged();
  }

  private String petrolsToHtml(Set<Petrol> petrols) {
    if (petrols.size() == 0)
      return "";

    final List<Petrol> petrolList = Petrols.getSortedByPetrolTypeAndPrice(petrols);
    final String lineBreak = "<br>";

    StringBuilder out = new StringBuilder("<html>");

    for (Petrol petrol : petrolList) {
      StringTokenizer st = new StringTokenizer(petrol.getTypeAndPrice(), " ");
      String tabbed = String.format("%-12s", st.nextElement()) + st.nextElement();

      out.append(tabbed.replace(" ", "&nbsp;")).append("<br>");
    }

    // Remove last <br>
    out.replace(out.lastIndexOf(lineBreak), out.lastIndexOf(lineBreak) + lineBreak.length(), "");

    out.append("</html>");

    return out.toString();
  }
}
