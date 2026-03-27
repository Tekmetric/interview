package com.tekmetric.entity;

import com.tekmetric.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum CarMake {
  TOYOTA("Toyota", ToyotaModel.values()),
  HONDA("Honda", HondaModel.values()),
  BMW("BMW", BmwModel.values()),
  FORD("Ford", FordModel.values()),
  TESLA("Tesla", TeslaModel.values()),
  AUDI("Audi", AudiModel.values());

  private final String displayName;
  private final Set<String> modelNames;

  CarMake(String displayName, ModelEnum[] models) {
    this.displayName = displayName;
    Set<String> set = new HashSet<>();
    for (ModelEnum m : models) {
      set.add(m.getName());
    }
    this.modelNames = Collections.unmodifiableSet(set);
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<String> getModelNames() {
    return modelNames;
  }

  /** Case-insensitive lookup by "Toyota", "BMW", etc. */
  public static CarMake fromDisplayName(String name) {
    return Arrays.stream(values())
        .filter(m -> m.displayName.equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new ValidationException("Unknown make: " + name));
  }

  /** Case-insensitive check if model is allowed for this make. */
  public boolean supportsModel(String model) {
    if (model == null) return false;
    return modelNames.stream().anyMatch(m -> m.equalsIgnoreCase(model));
  }

  /** Interface implemented by nested model enums. */
  public interface ModelEnum {
    String getName();
  }

  // ---------- Nested enums per make ----------

  public enum ToyotaModel implements ModelEnum {
    RAV4("RAV4"),
    CAMRY("Camry");

    private final String name;

    ToyotaModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public enum HondaModel implements ModelEnum {
    CR_V("CR-V"),
    ACCORD("Accord");

    private final String name;

    HondaModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public enum BmwModel implements ModelEnum {
    I7("i7"),
    X5("X5");

    private final String name;

    BmwModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public enum FordModel implements ModelEnum {
    ESCAPE("Escape"),
    F_150("F-150");

    private final String name;

    FordModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public enum TeslaModel implements ModelEnum {
    Y("Y"),
    MODEL_3("3");

    private final String name;

    TeslaModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public enum AudiModel implements ModelEnum {
    Q5("Q5"),
    A7("A7");

    private final String name;

    AudiModel(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
