package eu.testresults.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.testresults.model.TestSuite;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-30T21:24:04.222Z")

@Controller
public class TestsuitesApiController implements TestsuitesApi {

    private static final Logger log = LoggerFactory.getLogger(TestsuitesApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TestsuitesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<TestSuite>> testsuitesGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<TestSuite>>(objectMapper.readValue("[ {  \"name\" : \"Regression Tests\",  \"skipped\" : 0,  \"errors\" : 0,  \"failures\" : 2,  \"tests\" : 23,  \"time\" : 5438.68,  \"project_id\" : \"507f191e810c19729de860ea\",  \"labels\" : [ \"ER1\", \"regression\", \"automated\" ],  \"properties_array\" : [ {    \"properties\" : [ {      \"name\" : \"OS\",      \"value\" : \"Linux\"    }, {      \"name\" : \"java.vm.vendor\",      \"value\" : \"Oracle Corporation\"    } ]  } ],  \"testcases\" : [ {    \"name\" : \"RegressionTestCase\",    \"time\" : 438.84,    \"classname\" : \"com.example.regression.Test\"  } ]} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<TestSuite>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<TestSuite>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> testsuitesIdDelete(@ApiParam(value = "ID of the TestSuite to be deleted",required=true) @PathVariable("id") Long id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<TestSuite> testsuitesIdGet(@ApiParam(value = "ID of the TestSuite to find",required=true) @PathVariable("id") Long id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TestSuite>(objectMapper.readValue("{  \"name\" : \"Regression Tests\",  \"skipped\" : 0,  \"errors\" : 0,  \"failures\" : 2,  \"tests\" : 23,  \"time\" : 5438.68,  \"project_id\" : \"507f191e810c19729de860ea\",  \"labels\" : [ \"ER1\", \"regression\", \"automated\" ],  \"properties_array\" : [ {    \"properties\" : [ {      \"name\" : \"OS\",      \"value\" : \"Linux\"    }, {      \"name\" : \"java.vm.vendor\",      \"value\" : \"Oracle Corporation\"    } ]  } ],  \"testcases\" : [ {    \"name\" : \"RegressionTestCase\",    \"time\" : 438.84,    \"classname\" : \"com.example.regression.Test\"  } ]}", TestSuite.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TestSuite>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TestSuite>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<TestSuite> testsuitesIdPut(@ApiParam(value = "ID of the TestSuite to update",required=true) @PathVariable("id") Long id,@ApiParam(value = "" ,required=true )  @Valid @RequestBody TestSuite body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TestSuite>(objectMapper.readValue("{  \"name\" : \"Regression Tests\",  \"skipped\" : 0,  \"errors\" : 0,  \"failures\" : 2,  \"tests\" : 23,  \"time\" : 5438.68,  \"project_id\" : \"507f191e810c19729de860ea\",  \"labels\" : [ \"ER1\", \"regression\", \"automated\" ],  \"properties_array\" : [ {    \"properties\" : [ {      \"name\" : \"OS\",      \"value\" : \"Linux\"    }, {      \"name\" : \"java.vm.vendor\",      \"value\" : \"Oracle Corporation\"    } ]  } ],  \"testcases\" : [ {    \"name\" : \"RegressionTestCase\",    \"time\" : 438.84,    \"classname\" : \"com.example.regression.Test\"  } ]}", TestSuite.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TestSuite>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TestSuite>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<TestSuite> testsuitesPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody TestSuite body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TestSuite>(objectMapper.readValue("{  \"name\" : \"Regression Tests\",  \"skipped\" : 0,  \"errors\" : 0,  \"failures\" : 2,  \"tests\" : 23,  \"time\" : 5438.68,  \"project_id\" : \"507f191e810c19729de860ea\",  \"labels\" : [ \"ER1\", \"regression\", \"automated\" ],  \"properties_array\" : [ {    \"properties\" : [ {      \"name\" : \"OS\",      \"value\" : \"Linux\"    }, {      \"name\" : \"java.vm.vendor\",      \"value\" : \"Oracle Corporation\"    } ]  } ],  \"testcases\" : [ {    \"name\" : \"RegressionTestCase\",    \"time\" : 438.84,    \"classname\" : \"com.example.regression.Test\"  } ]}", TestSuite.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TestSuite>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TestSuite>(HttpStatus.NOT_IMPLEMENTED);
    }

}
