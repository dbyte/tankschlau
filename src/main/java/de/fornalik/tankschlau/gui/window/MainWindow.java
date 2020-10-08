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

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.menu.MainMenuBar;
import de.fornalik.tankschlau.station.*;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;
import de.fornalik.tankschlau.webserviceapi.common.MessageClient;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MainWindow extends JFrame {
  private final Localization l10n;
  private final UserPrefs userPrefs;
  private final PetrolStations petrolStationService;
  private final GeocodingClient geoCodingClient;
  private final MessageClient messageClient;
  private final MessageContent messageContent;

  private DefaultListModel<String> model;

  public MainWindow(
      Localization l10n,
      UserPrefs userPrefs,
      PetrolStations petrolStationService,
      GeocodingClient geoCodingClient,
      MessageClient messageClient,
      MessageContent messageContent) {

    super(de.fornalik.tankschlau.TankSchlau.class.getSimpleName());
    this.l10n = l10n;
    this.userPrefs = userPrefs;
    this.petrolStationService = petrolStationService;
    this.geoCodingClient = geoCodingClient;
    this.messageClient = messageClient;
    this.messageContent = messageContent;

    initGui();
    updateList(PetrolType.DIESEL);
  }

  private Address getUserPrefAddress() {
    Address address = userPrefs
        .readAddress()
        .orElseThrow(() -> new IllegalStateException("No preferences found for user address."));

    if (!address.getGeo().isPresent()) {
      Optional<Geo> geoFromWebservice = getUserGeoFromWebservice(address);

      if (!geoFromWebservice.isPresent()) {
        // Swap that msg with a Logger.
        String msg = "Log.Error: Requesting webservice for Geo data based on user's address did "
            + "not return required lat/lng.";
        System.out.println(msg);

        return address;
      }

      address.setGeo(geoFromWebservice.get());
      userPrefs.writeAddress(address);
    }

    return address;
  }

  private Optional<Geo> getUserGeoFromWebservice(Address userAddress) {
    System.out.println("Log.Info: Requesting geocoding webservice...");
    Optional<Geo> geo = Optional.empty();

    try {
      geo = geoCodingClient.getGeo(userAddress);
      geo.ifPresent(newGeo -> newGeo.setDistance(8.0)); // app default: 8.0 km search radius
      System.out.println("Log.Info: Success geocoding webservice. New geo data: " + geo);
    }

    catch (IOException e) {
      e.printStackTrace();
      // Swap that msg with a Logger.
      System.out.println("Log.Error: Requesting webservice for Geo data based on user's "
                             + "address did not return required lat/lng.");
    }

    return geo;
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

    this.setJMenuBar(new MainMenuBar(this, l10n));

    this.setVisible(true);
  }

  public void updateList(PetrolType sortedFor) {
    // processTestAddress(); // Ex: Writing some user geo data to user prefs

    Geo userGeo = getUserPrefAddress()
        .getGeo()
        .orElseThrow(() -> new IllegalStateException(
            "Can't request petrol station update, because got no lat/lng data for user address"));

    model.addElement(l10n.get("msg.PriceRequestRunning"));

    // Run a new dispatch queue thread for the web service request/response.
    EventQueue.invokeLater(() -> {

      try {
        List<PetrolStation> petrolStationsList = petrolStationService.getAllInNeighbourhood(
            userGeo);

        model.remove(0);
        System.out.println(
            "Response ready, status: " + petrolStationService.getTransactionInfo().getStatus());

        petrolStationsList = PetrolStations.sortByPriceAndDistanceForPetrolType(
            petrolStationsList,
            sortedFor);

        model.addElement(
            "********** "
                + l10n.get("msg.CurrentPricesSortedBy", sortedFor.name())
                + " **********");

        model.addElement(" ");

        petrolStationsList.forEach(this::populateListModel);

        if (petrolStationsList.size() == 0)
          return;

        // Send a push message
        PetrolStation cheapest = petrolStationsList.get(0);

        messageContent.getClass().getConstructor().newInstance();
        messageContent.setMessage(
            "Niedrigster Preis: "
                + createPetrolString(cheapest, sortedFor) + "\n\n"
                + createStationHeader(cheapest) + "\n"
                + (cheapest.address.getStreet() + " " + cheapest.address.getHouseNumber()).trim());

        messageClient.sendMessage(messageContent);
      }

      catch (IOException e) {
        model.add(
            1,
            l10n.get("msg.ErrorServerConnection", e.getClass().getTypeName()));

        model.add(2, e.getMessage());
        e.printStackTrace();
      }

      catch (Exception e) {
        model.add(
            1,
            l10n.get("msg.ErrorWhileRequestingPrices", e.getClass().getTypeName()));

        model.add(2, e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private void populateListModel(PetrolStation station) {
    double distanceKm = station.address.getGeo().flatMap(Geo::getDistance).orElse(0.0);

    Set<Petrol> petrolsUnsorted = station.getPetrols();
    List<Petrol> petrols = Petrols.getSortedByPetrolTypeAndPrice(petrolsUnsorted);

    model.addElement(createStationHeader(station));
    petrols.forEach((petrol) -> model.addElement(createPetrolString(station, petrol.type)));
    model.addElement(l10n.get("msg.KmAway", distanceKm));
    model.addElement("\t");
  }

  private String createStationHeader(PetrolStation station) {
    String stationName = station.address.getName();
    String open = station.isOpen ? l10n.get("msg.NowOpen") : l10n.get(
        "msg.NowClosed");
    return stationName + " - " + open;
  }

  private String createPetrolString(PetrolStation station, PetrolType type) {
    return station
        .findPetrol(type)
        .map(p -> p.type.name() + " " + p.price)
        .orElse(type.name() + " data not found for station " + station.address.getName());
  }
}
