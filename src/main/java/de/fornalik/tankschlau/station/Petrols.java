package de.fornalik.tankschlau.station;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for collections of Petrol
 */
public class Petrols {

  private Petrols() {
  }

  /**
   * Use for sorting a {@link Petrol} Set, based on the order of values defined in Enum
   * {@link PetrolType}. The array will get sorted based on the order in which the enum values
   * are defined.
   *
   * @param petrols List of {@link Petrol}s. Empty List if given petrols are null.
   */
  public static List<Petrol> getAsSortedListByPetrolType(Set<Petrol> petrols) {
    if (petrols == null) return new ArrayList<>();
    ArrayList<Petrol> petrolsList = new ArrayList<>(petrols);
    petrolsList.sort(Comparator.comparingInt(petrol -> petrol.type.ordinal()));
    return petrolsList;
  }

  /**
   * Finds a {@link Petrol} object for a {@link PetrolType} in a list of petrols.
   *
   * @param petrols    Set of {@link Petrol} to search in
   * @param petrolType Type of petrol we search for, assuming it's unique within the collection.
   * @return Optional {@link Petrol} object or Optional empty if not found.
   * @throws PetrolsDuplicateException If we found {@link PetrolType} duplicates in the list.
   */
  public static Optional<Petrol> getPetrol(Set<Petrol> petrols, PetrolType petrolType) {
    List<Petrol> foundPetrols = petrols.stream()
        .filter(petrol -> petrol.type.equals(petrolType))
        .collect(Collectors.toCollection(ArrayList::new));

    int countResults = foundPetrols.size();

    // As petrols is a Set here, this should never happen. Being defensive, we handle it anyway.
    if (countResults >= 2)
      throw new PetrolsDuplicateException(
          String.format("Critical error. Found %d duplicates for petrol type %s",
                        countResults,
                        petrolType));

    return countResults > 0 ? Optional.of(foundPetrols.get(0)) : Optional.empty();
  }

  /**
   * Thrown when we've found duplicates of {@link PetrolType} in a {@link Petrol} collection.
   *
   * @implNote This is an unchecked Exception.
   */
  public static class PetrolsDuplicateException extends RuntimeException {
    public PetrolsDuplicateException(String message) {
      super(message);
    }
  }
}
