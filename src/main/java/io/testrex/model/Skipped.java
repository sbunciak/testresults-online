package io.testrex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Skipped
 */
@Validated
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-30T21:24:04.222Z")
@Embeddable
public class Skipped {
  @JsonProperty("message")
  @Column(name = "message")
  private String message = null;

  // this is a field only in json
  // in xml it represents the textual content of the element
  @JsonProperty("content")
  @Column(name = "content")
  private String content = null;

  public Skipped message(String message) {
    this.message = message;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Skipped content(String content) {
    this.content = content;
    return this;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Skipped skipped = (Skipped) o;
    return Objects.equals(this.message, skipped.message) && Objects.equals(this.content, skipped.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Skipped {\n");

    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
