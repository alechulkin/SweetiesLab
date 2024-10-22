package org.sweetieslab.model.order;

import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.model.order.validator.exception.NotSetAddressValidatorException;

public class Address {

  private String building;
  private String room;
  private static AddressValidator validator;

  private Address(String building, String room) {
    this.building = building;
    this.room = room;
    if (validator != null) {
      validator.validate(this);
    } else {
      throw new NotSetAddressValidatorException("Address validator is not set");
    }
  }

  public static void setValidator(AddressValidator validator) {
    Address.validator = validator;
  }

  public String getBuilding() {
    return building;
  }

  public String getRoom() {
    return room;
  }

  public static class Builder {

    private String building;
    private String room;

    public Builder building(String building) {
      this.building = building;
      return this;
    }

    public Builder room(String room) {
      this.room = room;
      return this;
    }

    public Address build() {
      Address address = new Address(building, room);
      return address;
    }
  }
}
