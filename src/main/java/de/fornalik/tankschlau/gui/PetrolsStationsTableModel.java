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
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Component
class PetrolsStationsTableModel extends AbstractTableModel implements Serializable {

  private static final int COL_NAME_INDEX = 0;
  private static final int COL_PRICES_INDEX = 1;
  private static final int COL_STREET_INDEX = 2;
  private static final int COL_DISTANCE_INDEX = 3;
  private static final int COL_IS_OPEN_INDEX = 4;
  private static final String[] COLUMN_NAMES = new String[5];

  private final transient List<PetrolStation> petrolStations;
  private final UserPrefs userPrefs;
  private final Localization l10n;

  @Autowired
  PetrolsStationsTableModel(UserPrefs userPrefs, Localization l10n) {
    super();
    this.userPrefs = userPrefs;
    this.userPrefs.registerChangeListener("petrol.preferredtype", this::sortPetrolStations);
    this.l10n = l10n;
    this.petrolStations = new ArrayList<>();
  }

  @PostConstruct
  private void initColumnNames() {
    COLUMN_NAMES[0] = l10n.get("tableHeader.Name");
    COLUMN_NAMES[1] = l10n.get("tableHeader.Price");
    COLUMN_NAMES[2] = l10n.get("tableHeader.Place");
    COLUMN_NAMES[3] = l10n.get("tableHeader.Distance");
    COLUMN_NAMES[4] = l10n.get("tableHeader.Status");
  }

  @Override
  public String getColumnName(int forIndex) {
    return COLUMN_NAMES[forIndex];
  }

  @Override
  public int getRowCount() {
    return petrolStations.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    PetrolStation petrolStation = petrolStations.get(rowIndex);

    switch (columnIndex) {
      case COL_NAME_INDEX:
        return petrolStation.getAddress().getName();

      case COL_PRICES_INDEX:
        return petrolsToHtml(petrolStation.getPetrols());

      case COL_STREET_INDEX:
        return petrolStation.getAddress().getStreetAndHouseNumber();

      case COL_DISTANCE_INDEX:
        return petrolStation.getAddress()
            .getGeo()
            .map(Geo::getDistanceAwayString)
            .orElse(l10n.get("msg.Unknown"));

      case COL_IS_OPEN_INDEX:
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

    if (petrolStations.isEmpty()) return;
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
          .append(l10n.get("msg.NowOpen"))
          .append("</p>");
    }
    else {
      out.append("<p style=\"color:#B71414;\">")
          .append(l10n.get("msg.NowClosed"))
          .append("</p>");
    }

    out.append("</html>");
    return out.toString();
  }

  // TODO This finally belongs to the View Layer, wrong class here for a display method.
  private String petrolsToHtml(Set<Petrol> petrols) {
    if (petrols.isEmpty()) return "";

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
