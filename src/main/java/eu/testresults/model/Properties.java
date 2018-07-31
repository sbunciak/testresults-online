package eu.testresults.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Properties
 */
@Validated
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-30T21:24:04.222Z")

public class Properties {
  @JsonProperty("properties")
  @Valid
  private List<Property> properties = null;

  public Properties properties(List<Property> properties) {
    this.properties = properties;
    return this;
  }

  public Properties addPropertiesItem(Property propertiesItem) {
    if (this.properties == null) {
      this.properties = new ArrayList<Property>();
    }
    this.properties.add(propertiesItem);
    return this;
  }

  /**
   * Get properties
   * 
   * @return properties
   **/
  @ApiModelProperty(value = "")
  @Valid
  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Properties properties = (Properties) o;
    return Objects.equals(this.properties, properties.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(properties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Properties {\n");

    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
