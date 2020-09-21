package de.fornalik.tankschlau;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.station.*;
import de.fornalik.tankschlau.webserviceapi.TankerkoenigRequest;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TankSchlau {
  DefaultListModel<String> model;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      TankSchlau app = new TankSchlau();
      app.initGui();
      app.updateList();
    });
  }

  private void initGui() {
    model = new DefaultListModel<>();

    JFrame frame = new JFrame(TankSchlau.class.getSimpleName());
    JList<String> list = new JList<>(model);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(list);
    scrollPane.setBorder(null);

    frame.add(scrollPane);
    frame.setSize(650, 900);
    frame.setLocationRelativeTo(null);  // *** this will center your app ***
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    frame.setVisible(true);
  }

  private void updateList() {
    model.addElement("Preise werden abgefragt, bitte warten...");

    // Run a new dispatch queue thread for the web service request/response.
    EventQueue.invokeLater(() -> {
      PetrolType sortedFor = PetrolType.DIESEL;

      try {
        List<PetrolStation> petrolStations = updatePetrolStations();
        model.remove(0);

        System.out.println("Response ready.");

        petrolStations = PetrolStations.sortByPriceAndDistanceForPetrolType(
            petrolStations,
            sortedFor);

        model.addElement(String.format(
            "***** Aktuelle Preise, sortiert für %s nach Preis und Entfernung *****",
            sortedFor.name()));

        model.addElement(" ");

        petrolStations.forEach(station -> populateListModel(station, model));
      }

      catch (Exception e) {
        model.add(1, "Fehler bei der Preisabfrage: " + e.getClass().getTypeName());
        model.add(2, e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private List<PetrolStation> updatePetrolStations() throws IOException {
    // given
    Geo userLocation = new Geo(52.408306, 10.77200, 5.0);
    PetrolStationsJsonAdapter gsonAdapter = new PetrolStationsJsonAdapter();
    Request request = TankerkoenigRequest.create(userLocation);

    HttpClient httpClient = new OkHttpClient();

    // when
    System.out.println("Waiting for server response...");
    Response actualResponse = httpClient.newCall(request);

    // then
    return PetrolStations.createFromJson(
        actualResponse
            .getBody()
            .orElse(new ArrayList<>())
            .toString(), gsonAdapter);
  }

  private void populateListModel(PetrolStation station, DefaultListModel<String> model) {
    String stationName = station.address.getName();
    String open = station.isOpen ? "jetzt geöffnet" : "geschlossen";
    double distanceKm = station.address.getGeo().flatMap(Geo::getDistance).orElse(0.0);

    Set<Petrol> petrolsUnsorted = station.getPetrols();
    List<Petrol> petrols = Petrols.getSortedByPetrolTypeAndPrice(petrolsUnsorted);

    model.addElement(stationName + " - " + open);
    petrols.forEach((petrol) -> model.addElement(petrol.type.name() + "\t\t" + petrol.price));
    model.addElement(distanceKm + " km entfernt");
    model.addElement("\t");
  }
}
