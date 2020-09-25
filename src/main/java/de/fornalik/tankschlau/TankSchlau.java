package de.fornalik.tankschlau;

import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.window.MainWindow;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.webserviceapi.TankerkoenigRequest;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

public class TankSchlau {
  // Configuration
  public static final HttpClient globalHttpClient = new OkHttpClient();
  public static final TypeAdapter<List<PetrolStation>> globalJsonAdapter =
      new PetrolStationsJsonAdapter();
  public static final Localization L10N = new Localization(Locale.GERMAN);

  // Start application
  public static void main(String[] args) throws MalformedURLException {
    MainWindow mainWindow = new MainWindow();
    Request request = TankerkoenigRequest.create(new Geo(52.408306, 10.77200, 5.0));

    mainWindow.initGui();
    mainWindow.updateList(request, PetrolType.DIESEL);
  }
}