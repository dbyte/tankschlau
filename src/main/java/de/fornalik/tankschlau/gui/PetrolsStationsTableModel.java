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
import de.fornalik.tankschlau.station.*;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;

import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Handles the domain table model and its data, which is a result of the WorkerService and
 * its owned Worker.
 */
class PetrolsStationsTableModel extends AbstractTableModel implements Serializable {
  private static final Localization L10N = Localization.getInstance();
  static final String COL_NAME = L10N.get("tableHeader.Name");
  static final String COL_PRICES = L10N.get("tableHeader.Price");
  static final String COL_STREET = L10N.get("tableHeader.Place");
  static final String COL_DISTANCE = L10N.get("tableHeader.Distance");
  static final String COL_IS_OPEN = L10N.get("tableHeader.Status");
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
    PetrolStation petrolStation = petrolStations.get(rowIndex);

    switch (columnIndex) {
      case 0:
        return petrolStation.getAddress().getName();

      case 1:
        return petrolsToHtml(petrolStation.getPetrols());

      case 2:
        return petrolStation.getAddress().getStreetAndHouseNumber();

      case 3:
        return petrolStation.getAddress()
            .getGeo()
            .map(Geo::getDistanceAwayString)
            .orElse(L10N.get("msg.Unknown"));

      case 4:
        return isOpenToHtml(petrolStation.isOpen());

      default:
        return "Unregistered column index: " + columnIndex;
    }
  }

  synchronized void removeAllPetrolStations() {
    this.petrolStations.clear();
    fireTableRowsDeleted(0, getRowCount());
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

  // TODO This finally belongs to the View Layer, wrong class here for a display method.
  private String isOpenToHtml(boolean isOpen) {
    StringBuilder out = new StringBuilder("<html>");

    if (isOpen) {
      out.append("<p>")
          .append(L10N.get("msg.NowOpen"))
          .append("</p>");
    }
    else {
      out.append("<p style=\"color:#B71414;\">")
          .append(L10N.get("msg.NowClosed"))
          .append("</p>");
    }

    out.append("</html>");
    return out.toString();
  }

  // TODO This finally belongs to the View Layer, wrong class here for a display method.
  private String petrolsToHtml(Set<Petrol> petrols) {
    if (petrols.size() == 0)
      return "";

    final PetrolType preferredPetrolType = userPrefs.readPreferredPetrolType();
    final List<Petrol> petrolList = Petrols.getSortedByPetrolTypeAndPrice(petrols);

    // Format as html as we need multiple lines and colors for the prices per row.
    StringBuilder out = new StringBuilder("<html>");

    for (Petrol petrol : petrolList) {
      StringTokenizer st = new StringTokenizer(petrol.getTypeAndPrice(), " ");
      String tabbed = String.format("%-12s", st.nextElement()) + st.nextElement();

      // Color non-preferred petrol types and their prices light grey.
      if (petrol.type == preferredPetrolType) {
        tabbed = "<p>" + tabbed + "</p>";
      }
      else {
        tabbed = "<p style=\"color:#c2c2c2;\">" + tabbed + "</p>";
      }

      // Convert padding to html-non-breaking-spaces.
      out.append(tabbed.replace(" ", "&nbsp;"));
    }

    out.append("</html>");

    return out.toString();
  }
}
