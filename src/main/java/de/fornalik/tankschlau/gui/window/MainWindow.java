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

package de.fornalik.tankschlau.gui.window;

import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.menu.MainMenuBar;
import de.fornalik.tankschlau.station.*;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigPetrolStationsDao;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MainWindow extends JFrame {
  private final PetrolStationsDao petrolStationsDao = new TankerkoenigPetrolStationsDao();
  private DefaultListModel<String> model;

  public MainWindow() {
    super(de.fornalik.tankschlau.TankSchlau.class.getSimpleName());
  }

  public void initGui() {
    if (SystemUtils.IS_OS_MAC_OSX)
      try {
        UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }

    model = new DefaultListModel<>();

    JList<String> list = new JList<>(model);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(list);
    scrollPane.setBorder(null);

    this.add(scrollPane);
    this.setSize(650, 900);
    this.setLocationRelativeTo(null);  // will center the window
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    this.setJMenuBar(new MainMenuBar(this));

    this.setVisible(true);
  }

  public void updateList(Geo geo, PetrolType sortedFor) {
    model.addElement(TankSchlau.L10N.get("msg.PriceRequestRunning"));

    // Run a new dispatch queue thread for the web service request/response.
    EventQueue.invokeLater(() -> {

      try {
        List<PetrolStation> petrolStations = PetrolStations.getAllInNeighbourhood(
            petrolStationsDao,
            geo);

        model.remove(0);
        System.out.println("Response ready, status: " + petrolStationsDao.getTransactionInfo()
                                                                         .getStatus());

        petrolStations = PetrolStations.sortByPriceAndDistanceForPetrolType(
            petrolStations,
            sortedFor);

        model.addElement(
            "********** "
                + TankSchlau.L10N.get("msg.CurrentPricesSortedBy", sortedFor.name())
                + " **********");

        model.addElement(" ");

        petrolStations.forEach(this::populateListModel);
      }

      catch (IOException e) {
        model.add(
            1,
            TankSchlau.L10N.get("msg.ErrorServerConnection", e.getClass().getTypeName()));

        model.add(2, e.getMessage());
        e.printStackTrace();
      }

      catch (Exception e) {
        model.add(
            1,
            TankSchlau.L10N.get("msg.ErrorWhileRequestingPrices", e.getClass().getTypeName()));

        model.add(2, e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private void populateListModel(PetrolStation station) {
    String stationName = station.address.getName();
    String open = station.isOpen ? TankSchlau.L10N.get("msg.NowOpen") : TankSchlau.L10N.get(
        "msg.NowClosed");
    double distanceKm = station.address.getGeo().flatMap(Geo::getDistance).orElse(0.0);

    Set<Petrol> petrolsUnsorted = station.getPetrols();
    List<Petrol> petrols = Petrols.getSortedByPetrolTypeAndPrice(petrolsUnsorted);

    model.addElement(stationName + " - " + open);
    petrols.forEach((petrol) -> model.addElement(petrol.type.name() + "\t\t" + petrol.price));
    model.addElement(TankSchlau.L10N.get("msg.KmAway", distanceKm));
    model.addElement("\t");
  }
}
