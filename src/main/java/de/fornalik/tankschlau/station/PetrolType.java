package de.fornalik.tankschlau.station;

public enum PetrolType {
  DIESEL("diesel"),
  E5("e5"),
  E10("e10");

  private final String jsonKey;

  PetrolType(String jsonKey) {
    this.jsonKey = jsonKey;
  }

  public String getJsonKey() {
    return this.jsonKey;
  }
}
