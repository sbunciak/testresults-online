package io.testrex.controller;

import io.testrex.model.TestSuite;
import io.testrex.repository.TestSuiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/testsuites")
public class TestSuiteController {
  private final TestSuiteRepository testSuiteRepository;

  public TestSuiteController(TestSuiteRepository testSuiteRepository) {
    this.testSuiteRepository = testSuiteRepository;
  }

  Logger LOG = LoggerFactory.getLogger(TestSuiteController.class);

  @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite>findById(@PathVariable Long id) {
   return testSuiteRepository.findById(id).map(testSuite1 -> ResponseEntity.ok(testSuite1)).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(path = "/", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public List<ResponseEntity<TestSuite>> findAll() {
    List<ResponseEntity<TestSuite>> resources = testSuiteRepository.findAll().stream()
            .map(testSuite -> ResponseEntity.ok(testSuite)).collect(Collectors.toList());
    return resources;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite> create(@RequestBody TestSuite testSuite) {
    LOG.info("Creating TestSuite: ", testSuite.toString());
    TestSuite ts = testSuiteRepository.save(testSuite);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(ts.getId()).toUri();
    return ResponseEntity.created(location).body(ts);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    LOG.info("Deleting TestSuite with id: " + id);
    testSuiteRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite> put(@PathVariable Long id, @RequestBody TestSuite newTestSuite) {
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    TestSuite requested = testSuiteRepository.findById(id).get();
    if (requested != null) {
      newTestSuite.setId(id);
      LOG.info("Replacing TestCase on id: " + id);
      return ResponseEntity.created(location).body(testSuiteRepository.save(newTestSuite));
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
