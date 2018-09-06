package io.testrex.repository;

import io.testrex.model.TestSuite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestSuiteRepository extends CrudRepository<TestSuite, Long> {
    List<TestSuite> findTestSuitesByProjectProjectId(Long projectId);
    TestSuite findTestSuiteByIdAndProjectProjectId(Long id, Long projectId);
}
