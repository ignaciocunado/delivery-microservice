package nl.tudelft.sem.template.example.entities;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Delivery
 */
@Entity
public class Delivery {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private UUID deliveryID;

  private UUID orderID;

  private UUID customerID;

  private UUID courierID;

  private UUID restaurantID;

  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime pickupTimeEstimate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime deliveryTimeEstimate;

  private Double customerRating;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime pickedUpTime;

  private String liveLocation;

  private String userException;

  private Integer delay;

  /**
   * Empty constructor for serialization.
   */
  public Delivery()
  {

  }

  /**
   * Constructor that provides parameters relevant to initialization.
   */
  public Delivery(UUID orderID, UUID customerID, UUID courierID, UUID restaurantID, String status,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime pickupTimeEstimate)
  {
    this.orderID = orderID;
    this.customerID = customerID;
    this.courierID = courierID;
    this.restaurantID = restaurantID;
    this.status = status;
    this.pickupTimeEstimate = pickupTimeEstimate;
  }

  /**
   * Constructor for all parameters, excluding delivery ID. Just in case it's useful.
   */
  public Delivery(UUID orderID, UUID customerID, UUID courierID, UUID restaurantID, String status,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime pickupTimeEstimate,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime deliveryTimeEstimate,
                  Double customerRating,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime pickedUpTime, String liveLocation,
                  String userException, Integer delay)
  {
    this.orderID = orderID;
    this.customerID = customerID;
    this.courierID = courierID;
    this.restaurantID = restaurantID;
    this.status = status;
    this.pickupTimeEstimate = pickupTimeEstimate;
    this.deliveryTimeEstimate = deliveryTimeEstimate;
    this.customerRating = customerRating;
    this.pickedUpTime = pickedUpTime;
    this.liveLocation = liveLocation;
    this.userException = userException;
    this.delay = delay;
  }

  /**
   * Get deliveryID
   * @return deliveryID
  */
  @Valid 
  @Schema(name = "deliveryID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deliveryID")
  public UUID getDeliveryID() {
    return deliveryID;
  }

  public void setDeliveryID(UUID deliveryID) {
    this.deliveryID = deliveryID;
  }

  /**
   * Get orderID
   * @return orderID
  */
  @Valid 
  @Schema(name = "orderID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderID")
  public UUID getOrderID() {
    return orderID;
  }

  public void setOrderID(UUID orderID) {
    this.orderID = orderID;
  }

  /**
   * Get customerID
   * @return customerID
  */
  @Valid 
  @Schema(name = "customerID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerID")
  public UUID getCustomerID() {
    return customerID;
  }

  public void setCustomerID(UUID customerID) {
    this.customerID = customerID;
  }

  /**
   * Get courierID
   * @return courierID
  */
  @Valid 
  @Schema(name = "courierID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courierID")
  public UUID getCourierID() {
    return courierID;
  }

  public void setCourierID(UUID courierID) {
    this.courierID = courierID;
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
   * Get status
   * @return status
  */
  
  @Schema(name = "status", example = "pending", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Get pickupTimeEstimate
   * @return pickupTimeEstimate
  */
  @Valid 
  @Schema(name = "pickupTimeEstimate", example = "2022-01-10T15:23:44Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pickupTimeEstimate")
  public OffsetDateTime getPickupTimeEstimate() {
    return pickupTimeEstimate;
  }

  public void setPickupTimeEstimate(OffsetDateTime pickupTimeEstimate) {
    this.pickupTimeEstimate = pickupTimeEstimate;
  }

  /**
   * Get deliveryTimeEstimate
   * @return deliveryTimeEstimate
  */
  @Valid 
  @Schema(name = "deliveryTimeEstimate", example = "2022-01-10T15:23:44Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deliveryTimeEstimate")
  public OffsetDateTime getDeliveryTimeEstimate() {
    return deliveryTimeEstimate;
  }

  public void setDeliveryTimeEstimate(OffsetDateTime deliveryTimeEstimate) {
    this.deliveryTimeEstimate = deliveryTimeEstimate;
  }

  /**
   * Get customerRating
   * @return customerRating
  */
  
  @Schema(name = "customerRating", example = "0.5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerRating")
  public Double getCustomerRating() {
    return customerRating;
  }

  public void setCustomerRating(Double customerRating) {
    this.customerRating = customerRating;
  }

  /**
   * Get pickedUpTime
   * @return pickedUpTime
  */
  @Valid 
  @Schema(name = "pickedUpTime", example = "2022-01-10T15:23:44Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pickedUpTime")
  public OffsetDateTime getPickedUpTime() {
    return pickedUpTime;
  }

  public void setPickedUpTime(OffsetDateTime pickedUpTime) {
    this.pickedUpTime = pickedUpTime;
  }

  /**
   * Get liveLocation
   * @return liveLocation
  */
  
  @Schema(name = "liveLocation", example = "52.00667,4.35556", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("liveLocation")
  public String getLiveLocation() {
    return liveLocation;
  }

  public void setLiveLocation(String liveLocation) {
    this.liveLocation = liveLocation;
  }

  /**
   * Get userException
   * @return userException
  */
  
  @Schema(name = "userException", example = "Rider has fallen", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userException")
  public String getUserException() {
    return userException;
  }

  public void setUserException(String userException) {
    this.userException = userException;
  }

  /**
   * Get delay
   * @return delay
  */
  
  @Schema(name = "delay", example = "15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("delay")
  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Delivery delivery = (Delivery) o;
    return Objects.equals(this.deliveryID, delivery.deliveryID) &&
        Objects.equals(this.orderID, delivery.orderID) &&
        Objects.equals(this.customerID, delivery.customerID) &&
        Objects.equals(this.courierID, delivery.courierID) &&
        Objects.equals(this.restaurantID, delivery.restaurantID) &&
        Objects.equals(this.status, delivery.status) &&
        Objects.equals(this.pickupTimeEstimate, delivery.pickupTimeEstimate) &&
        Objects.equals(this.deliveryTimeEstimate, delivery.deliveryTimeEstimate) &&
        Objects.equals(this.customerRating, delivery.customerRating) &&
        Objects.equals(this.pickedUpTime, delivery.pickedUpTime) &&
        Objects.equals(this.liveLocation, delivery.liveLocation) &&
        Objects.equals(this.userException, delivery.userException) &&
        Objects.equals(this.delay, delivery.delay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deliveryID, orderID, customerID, courierID, restaurantID, status, pickupTimeEstimate, deliveryTimeEstimate, customerRating, pickedUpTime, liveLocation, userException, delay);
  }

  @Override
  public String toString() {
      return "class Delivery {\n" +
            "    deliveryID: " + toIndentedString(deliveryID) + "\n" +
            "    orderID: " + toIndentedString(orderID) + "\n" +
            "    customerID: " + toIndentedString(customerID) + "\n" +
            "    courierID: " + toIndentedString(courierID) + "\n" +
            "    restaurantID: " + toIndentedString(restaurantID) + "\n" +
            "    status: " + toIndentedString(status) + "\n" +
            "    pickupTimeEstimate: " + toIndentedString(pickupTimeEstimate) + "\n" +
            "    deliveryTimeEstimate: " + toIndentedString(deliveryTimeEstimate) + "\n" +
            "    customerRating: " + toIndentedString(customerRating) + "\n" +
            "    pickedUpTime: " + toIndentedString(pickedUpTime) + "\n" +
            "    liveLocation: " + toIndentedString(liveLocation) + "\n" +
            "    userException: " + toIndentedString(userException) + "\n" +
            "    delay: " + toIndentedString(delay) + "\n" +
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

