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
import de.fornalik.tankschlau.util.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

public class TankSchlau {
  // Configuration
  public static final Localization L10N = new Localization(Locale.GERMAN);
  public static final UserPrefs USER_PREFS = new UserPrefs();
  public static final ApiKeyStore API_KEY_STORE = new UserPrefsApiKeyStore(USER_PREFS);
  public static final HttpClient HTTP_CLIENT = new OkHttpClient();
  public static final TypeAdapter<List<PetrolStation>> PETROL_STATIONS_JSON_ADAPTER =
      new PetrolStationsJsonAdapter();

  // Start application
  public static void main(String[] args) throws MalformedURLException {
    // Write some user geo data to user prefs.
    USER_PREFS.writeGeo(new Geo(48.0348466, 11.9068076, 10.0));

    // Default Geo if no prefs exist.
    final Geo[] geo = {new Geo(52.408306, 10.77200, 5.0)};

    USER_PREFS.readGeo().ifPresent(g -> geo[0] = g);

    MainWindow mainWindow = new MainWindow();
    Request request = TankerkoenigRequest.create(geo[0]);

    mainWindow.initGui();
    mainWindow.updateList(request, PetrolType.DIESEL);
  }
}