package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.station.*;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class MainWindow extends JFrame {
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

  public void updateList(Request request, PetrolType sortedFor) {
    model.addElement(TankSchlau.L10N.get("PriceRequestRunning"));

    // Run a new dispatch queue thread for the web service request/response.
    EventQueue.invokeLater(() -> {

      try {
        List<PetrolStation> petrolStations = PetrolStations.createFromWebService(
            TankSchlau.globalHttpClient,
            request,
            TankSchlau.globalJsonAdapter);

        model.remove(0);
        System.out.println("Response ready.");

        petrolStations = PetrolStations.sortByPriceAndDistanceForPetrolType(
            petrolStations,
            sortedFor);

        model.addElement(
            "********** "
                + TankSchlau.L10N.get("CurrentPricesSortedBy", sortedFor.name())
                + " **********");

        model.addElement(" ");

        petrolStations.forEach(this::populateListModel);
      }

      catch (Exception e) {
        model.add(
            1,
            TankSchlau.L10N.get("ErrorWhileRequestingPrices", e.getClass().getTypeName()));

        model.add(2, e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private void populateListModel(PetrolStation station) {
    String stationName = station.address.getName();
    String open = station.isOpen ? TankSchlau.L10N.get("NowOpen") : TankSchlau.L10N.get(
        "NowClosed");
    double distanceKm = station.address.getGeo().flatMap(Geo::getDistance).orElse(0.0);

    Set<Petrol> petrolsUnsorted = station.getPetrols();
    List<Petrol> petrols = Petrols.getSortedByPetrolTypeAndPrice(petrolsUnsorted);

    model.addElement(stationName + " - " + open);
    petrols.forEach((petrol) -> model.addElement(petrol.type.name() + "\t\t" + petrol.price));
    model.addElement(TankSchlau.L10N.get("KmAway", distanceKm));
    model.addElement("\t");
  }
}
