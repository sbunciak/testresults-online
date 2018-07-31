package eu.testresults.model;

import java.util.Objects;

import javax.annotation.Generated;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * Error
 */
@Validated
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-30T21:24:04.222Z")
public class Error {
  @JsonProperty("message")
  private String message = null;

  @JsonProperty("content")
  private String content = null;

  @JsonProperty("type")
  private String type = null;

  public Error message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * 
   * @return message
   **/
  @ApiModelProperty(value = "")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Error content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * 
   * @return content
   **/
  @ApiModelProperty(value = "")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Error type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * 
   * @return type
   **/
  @ApiModelProperty(value = "")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(this.message, error.message) && Objects.equals(this.content, error.content) && Objects.equals(this.type, error.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, content, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");

    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
