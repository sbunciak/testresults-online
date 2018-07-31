package eu.testresults.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.testresults.model.Error;
import eu.testresults.model.Failure;
import eu.testresults.model.Skipped;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TestCase
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-30T21:24:04.222Z")

public class TestCase   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("classname")
  private String classname = null;

  @JsonProperty("group")
  private String group = null;

  @JsonProperty("time")
  private BigDecimal time = null;

  @JsonProperty("system_out")
  private String systemOut = null;

  @JsonProperty("system_err")
  private String systemErr = null;

  @JsonProperty("failure")
  private Failure failure = null;

  @JsonProperty("rerunFailure")
  private Failure rerunFailure = null;

  @JsonProperty("skipped")
  private Skipped skipped = null;

  @JsonProperty("error")
  private Error error = null;

  public TestCase name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TestCase classname(String classname) {
    this.classname = classname;
    return this;
  }

  /**
   * Get classname
   * @return classname
  **/
  @ApiModelProperty(value = "")


  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public TestCase group(String group) {
    this.group = group;
    return this;
  }

  /**
   * Get group
   * @return group
  **/
  @ApiModelProperty(value = "")


  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public TestCase time(BigDecimal time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public BigDecimal getTime() {
    return time;
  }

  public void setTime(BigDecimal time) {
    this.time = time;
  }

  public TestCase systemOut(String systemOut) {
    this.systemOut = systemOut;
    return this;
  }

  /**
   * Get systemOut
   * @return systemOut
  **/
  @ApiModelProperty(value = "")


  public String getSystemOut() {
    return systemOut;
  }

  public void setSystemOut(String systemOut) {
    this.systemOut = systemOut;
  }

  public TestCase systemErr(String systemErr) {
    this.systemErr = systemErr;
    return this;
  }

  /**
   * Get systemErr
   * @return systemErr
  **/
  @ApiModelProperty(value = "")


  public String getSystemErr() {
    return systemErr;
  }

  public void setSystemErr(String systemErr) {
    this.systemErr = systemErr;
  }

  public TestCase failure(Failure failure) {
    this.failure = failure;
    return this;
  }

  /**
   * Get failure
   * @return failure
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Failure getFailure() {
    return failure;
  }

  public void setFailure(Failure failure) {
    this.failure = failure;
  }

  public TestCase rerunFailure(Failure rerunFailure) {
    this.rerunFailure = rerunFailure;
    return this;
  }

  /**
   * Get rerunFailure
   * @return rerunFailure
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Failure getRerunFailure() {
    return rerunFailure;
  }

  public void setRerunFailure(Failure rerunFailure) {
    this.rerunFailure = rerunFailure;
  }

  public TestCase skipped(Skipped skipped) {
    this.skipped = skipped;
    return this;
  }

  /**
   * Get skipped
   * @return skipped
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Skipped getSkipped() {
    return skipped;
  }

  public void setSkipped(Skipped skipped) {
    this.skipped = skipped;
  }

  public TestCase error(Error error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TestCase testCase = (TestCase) o;
    return Objects.equals(this.name, testCase.name) &&
        Objects.equals(this.classname, testCase.classname) &&
        Objects.equals(this.group, testCase.group) &&
        Objects.equals(this.time, testCase.time) &&
        Objects.equals(this.systemOut, testCase.systemOut) &&
        Objects.equals(this.systemErr, testCase.systemErr) &&
        Objects.equals(this.failure, testCase.failure) &&
        Objects.equals(this.rerunFailure, testCase.rerunFailure) &&
        Objects.equals(this.skipped, testCase.skipped) &&
        Objects.equals(this.error, testCase.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, classname, group, time, systemOut, systemErr, failure, rerunFailure, skipped, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TestCase {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    classname: ").append(toIndentedString(classname)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    systemOut: ").append(toIndentedString(systemOut)).append("\n");
    sb.append("    systemErr: ").append(toIndentedString(systemErr)).append("\n");
    sb.append("    failure: ").append(toIndentedString(failure)).append("\n");
    sb.append("    rerunFailure: ").append(toIndentedString(rerunFailure)).append("\n");
    sb.append("    skipped: ").append(toIndentedString(skipped)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

