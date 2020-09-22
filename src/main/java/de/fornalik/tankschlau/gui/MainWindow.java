package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.station.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

public class MainWindow {
  private DefaultListModel<String> model;

  public void initGui() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    model = new DefaultListModel<>();

    JFrame frame = new JFrame(de.fornalik.tankschlau.TankSchlau.class.getSimpleName());
    JList<String> list = new JList<>(model);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(list);
    scrollPane.setBorder(null);

    frame.add(scrollPane);
    frame.setSize(650, 900);
    frame.setLocationRelativeTo(null);  // will center the window
    frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    frame.setJMenuBar(createMenuBar());

    frame.setVisible(true);
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // File menu
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);

    // cmd + W: Closes window
    JMenuItem eMenuItem = new JMenuItem("Close");
    KeyStroke strokeCmdW = KeyStroke.getKeyStroke(
        KeyEvent.VK_W,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

    eMenuItem.setAccelerator(strokeCmdW);
    eMenuItem.addActionListener((event) -> System.exit(0));
    fileMenu.add(eMenuItem);
    menuBar.add(fileMenu);

    return menuBar;
  }

  public void updateList(Request request, PetrolType sortedFor) {
    model.addElement("Preise werden abgefragt, bitte warten...");

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

        model.addElement(String.format(
            "***** Aktuelle Preise, sortiert für %s nach Preis und Entfernung *****",
            sortedFor.name()));

        model.addElement(" ");

        petrolStations.forEach(this::populateListModel);
      }

      catch (Exception e) {
        model.add(1, "Fehler bei der Preisabfrage: " + e.getClass().getTypeName());
        model.add(2, e.getMessage());
        e.printStackTrace();
      }
    });
  }

  private void populateListModel(PetrolStation station) {
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

