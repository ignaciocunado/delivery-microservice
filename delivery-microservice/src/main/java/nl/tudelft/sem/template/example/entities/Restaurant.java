package nl.tudelft.sem.template.example.entities;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;

import javax.persistence.*;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Restaurant
 */
@Entity
public class Restaurant {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private UUID restaurantID;

  private UUID vendorID;

  @Valid
  @ElementCollection
  private List<@Valid UUID> courierIDs;

  private Double maxDeliveryZone;

  /**
   * Empty constructor for serialization.
   */
  public Restaurant()
  {

  }

  public Restaurant(UUID vendorID, List<@Valid UUID> courierIDs, Double maxDeliveryZone)
  {
    this.vendorID = vendorID;
    this.courierIDs = courierIDs;
    this.maxDeliveryZone = maxDeliveryZone;
  }

  /**
   * Get restaurantID
   * @return restaurantID
  */
  @Valid 
  @Schema(name = "restaurantID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurantID")
  public UUID getRestaurantID() {
    return restaurantID;
  }

  public void setRestaurantID(UUID restaurantID) {
    this.restaurantID = restaurantID;
  }

  /**
   * Get vendorID
   * @return vendorID
  */
  @Valid 
  @Schema(name = "vendorID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vendorID")
  public UUID getVendorID() {
    return vendorID;
  }

  public void setVendorID(UUID vendorID) {
    this.vendorID = vendorID;
  }

  public Restaurant addCourierIDsItem(UUID courierID) {
    if (this.courierIDs == null) {
      this.courierIDs = new ArrayList<>();
    }
    this.courierIDs.add(courierID);
    return this;
  }

  /**
   * Get courierIDs
   * @return courierIDs
  */
  @Valid 
  @Schema(name = "courierIDs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courierIDs")
  public List<@Valid UUID> getCourierIDs() {
    return courierIDs;
  }

  public void setCourierIDs(List<@Valid UUID> courierIDs) {
    this.courierIDs = courierIDs;
  }

  /**
   * Get maxDeliveryZone
   * @return maxDeliveryZone
  */
  
  @Schema(name = "maxDeliveryZone", example = "500", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxDeliveryZone")
  public Double getMaxDeliveryZone() {
    return maxDeliveryZone;
  }

  public void setMaxDeliveryZone(Double maxDeliveryZone) {
    this.maxDeliveryZone = maxDeliveryZone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Restaurant restaurant = (Restaurant) o;
    return Objects.equals(this.restaurantID, restaurant.restaurantID) &&
        Objects.equals(this.vendorID, restaurant.vendorID) &&
        Objects.equals(this.courierIDs, restaurant.courierIDs) &&
        Objects.equals(this.maxDeliveryZone, restaurant.maxDeliveryZone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restaurantID, vendorID, courierIDs, maxDeliveryZone);
  }

  @Override
  public String toString() {
      return "class Restaurant {\n" +
            "    restaurantID: " + toIndentedString(restaurantID) + "\n" +
            "    vendorID: " + toIndentedString(vendorID) + "\n" +
            "    courierIDs: " + toIndentedString(courierIDs) + "\n" +
            "    maxDeliveryZone: " + toIndentedString(maxDeliveryZone) + "\n" +
            "}";
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

