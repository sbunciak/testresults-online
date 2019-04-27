package io.testrex.controller;

import io.testrex.model.Project;
import io.testrex.model.TestSuite;
import io.testrex.repository.ProjectRepository;
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
@RequestMapping(path = "/projects/{projectId}/testsuites")
public class TestSuiteController {
  private final TestSuiteRepository testSuiteRepository;
  private final ProjectRepository projectRepository;

  public TestSuiteController(TestSuiteRepository testSuiteRepository, ProjectRepository projectRepository) {
    this.testSuiteRepository = testSuiteRepository;
    this.projectRepository = projectRepository;
  }

  Logger LOG = LoggerFactory.getLogger(TestSuiteController.class);

  @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite>findById(@PathVariable Long id, @PathVariable Long projectId) {

      TestSuite testSuite = testSuiteRepository.findTestSuiteByIdAndProjectProjectId(id, projectId);
      if (testSuite == null) return ResponseEntity.notFound().build();
      else return ResponseEntity.ok(testSuite);

     }

  @GetMapping(path = "/", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public List<ResponseEntity<TestSuite>> findAll(@PathVariable Long projectId) {
      List<ResponseEntity<TestSuite>> resources = testSuiteRepository.findTestSuitesByProjectProjectId(projectId)
              .stream().map(testSuite -> ResponseEntity.ok(testSuite)).collect(Collectors.toList());
        return resources;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite> create(@RequestBody TestSuite testSuite, @PathVariable Long projectId) {
    LOG.info("Creating TestSuite: {}", testSuite.toString());
    if (!projectRepository.findById(projectId).isPresent()) {
        return ResponseEntity.notFound().build();
    }
    Project pr = projectRepository.findById(projectId).get();
        testSuite.setProject(pr);
        TestSuite ts = testSuiteRepository.save(testSuite);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(ts.getId()).toUri();
        return ResponseEntity.created(location).body(ts);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id, @PathVariable Long projectId) {

      TestSuite testSuite = testSuiteRepository.findTestSuiteByIdAndProjectProjectId(id, projectId);
      if (testSuite != null) {
      testSuiteRepository.delete(testSuite);
      return ResponseEntity.ok().location(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).build();
      } else {
          return ResponseEntity.notFound().location(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri()).build();
      }
  }

  @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<TestSuite> put(@PathVariable Long id, @RequestBody TestSuite newTestSuite, @PathVariable Long projectId) {
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    TestSuite requested = testSuiteRepository.findTestSuiteByIdAndProjectProjectId(id, projectId);
    if (requested != null) {
      newTestSuite.setId(id);
      Project pr = projectRepository.findById(projectId).get();
      if (pr != null) {
          newTestSuite.setProject(pr);
          LOG.info("Replacing TestCase on id: " + id);
          return ResponseEntity.created(location).body(testSuiteRepository.save(newTestSuite));
      } else {
          return ResponseEntity.notFound().build();
      }
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
