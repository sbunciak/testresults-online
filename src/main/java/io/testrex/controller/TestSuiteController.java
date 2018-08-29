package io.testrex.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.testrex.model.TestSuite;

@RestController
@RequestMapping("/testsuites")
public class TestSuiteController {

  Logger LOG = LoggerFactory.getLogger(TestSuiteController.class);

  @GetMapping(path = "/{id}")
  public TestSuite findById() {
    // TODO:
    TestSuite ts = new TestSuite();
    ts.setName("test-name");
    return ts;
  }

  @PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
  ResponseEntity<?> create(@RequestBody TestSuite testSuite) {
    LOG.info("Created TestSuite: ", testSuite);

    // TODO:
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(1).toUri();
    return ResponseEntity.created(location).body(testSuite);
  }

}
