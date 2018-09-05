package io.testrex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.testrex.model.Property;
import io.testrex.model.TestCase;
import io.testrex.model.TestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@ActiveProfiles("itest")
public class TestRexApplicationTests {

  private final HttpHeaders headers = new HttpHeaders();
  private final String filePath = "src/test/resources/";

  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgt;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void shouldReturn404WhenSendingRequestToController() {
    System.out.println("http://localhost:" + this.port + "/testsuites/1");
    
    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/testsuites/0", Map.class);

    assertTrue(entity.getStatusCode().is4xxClientError());
  }

  @Test
  public void shouldReturn200WhenSendingRequestToManagementEndpoint() {
    System.out.println("http://localhost:" + this.mgt + "/actuator/info");
    @SuppressWarnings("rawtypes")
    ResponseEntity<Map> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.mgt + "/actuator/info", Map.class);

    assertTrue(entity.getStatusCode().is2xxSuccessful());
  }

  @Test
  public void parseTestSuiteReturnsXML() {
      headers.setContentType(MediaType.APPLICATION_XML);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
      HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-classWithNoTests.NoMethodsTestCase.xml")), headers);
      ResponseEntity<String> response = testRestTemplate.postForEntity(getUrl(), entity, String.class);

      JacksonXmlModule xmlModule = new JacksonXmlModule();
      xmlModule.setDefaultUseWrapper(false);
      ObjectMapper objectMapper = new XmlMapper(xmlModule);
      Exception ex = null;
      try {
          objectMapper.readTree(response.getBody());
      } catch (JsonProcessingException e) {
          ex = e;
      }
      catch (IOException e) {
          e.printStackTrace();
      }
      assertNull(ex);
  }

  @Test
  public void parseTestSuiteReturnsJSON() {
      headers.setContentType(MediaType.APPLICATION_XML);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-classWithNoTests.NoMethodsTestCase.xml")), headers);
      ResponseEntity<String> response = testRestTemplate.postForEntity(getUrl(), entity, String.class);
      boolean valid = true;
      try {
          final ObjectMapper jsonParser = new ObjectMapper();
          jsonParser.readTree(response.getBody());
      } catch (JsonProcessingException e) {
          valid = false;
      } catch (IOException e) {
          e.printStackTrace();
      }
      assertTrue(valid);
  }

  @Test
  public void parseTestSuiteXMLWithoutTestcases() {
    headers.setContentType(MediaType.APPLICATION_XML);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-classWithNoTests.NoMethodsTestCase.xml")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());

      assertThat(response.getBody().getErrors(), is(0));
      assertThat(response.getBody().getSkipped(), is(0));
      assertThat(response.getBody().getTests(), is(0));
      assertThat(response.getBody().getTime().toString(), is("0"));
      assertThat(response.getBody().getFailures(), is(0));
      assertThat(response.getBody().getName(), is("classWithNoTests.NoMethodsTestCase"));
      List<Property> properties = response.getBody().getProperties();
      assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
      assertThat(properties.get(0).getName(), is("java.runtime.name"));
      assertThat(properties.get(56).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
      assertThat(properties.get(56).getName(), is("sun.cpu.isalist"));

    testRestTemplate.delete(getUrl() + response.getBody().getId());
    ResponseEntity<String> deletedResponse = testRestTemplate.getForEntity(getUrl() + response.getBody().getId(), String.class);
    assertTrue(deletedResponse.getStatusCode().is4xxClientError());
  }

  @Test
  public void parseTestSuiteJSONWithoutTestcases() {
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-classWithNoTests.NoMethodsTestCase.json")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertThat(response.getBody().getErrors(), is(0));
    assertThat(response.getBody().getSkipped(), is(0));
    assertThat(response.getBody().getTests(), is(0));
    assertThat(response.getBody().getTime().toString(), is("0"));
    assertThat(response.getBody().getFailures(), is(0));
    assertThat(response.getBody().getName(), is("classWithNoTests.NoMethodsTestCase"));
    List<Property> properties = response.getBody().getProperties();
    assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
    assertThat(properties.get(0).getName(), is("java.runtime.name"));
    assertThat(properties.get(56).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
    assertThat(properties.get(56).getName(), is("sun.cpu.isalist"));

    HttpEntity<String> putEntity = new HttpEntity<String>(readTestFile(Paths.get(filePath + "TEST-com.shape.PointTest.json")), headers);
    testRestTemplate.put(getUrl() + response.getBody().getId(), putEntity);
    ResponseEntity<TestSuite> putResponse = testRestTemplate.getForEntity(getUrl() + response.getBody().getId(), TestSuite.class);
    assertTrue(putResponse.getStatusCode().is2xxSuccessful());
  }

  @Test
  public void parseTestSuiteXMLWithCircleTest() {
    headers.setContentType(MediaType.APPLICATION_XML);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-com.shape.CircleTest.xml")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
      assertThat(response.getBody().getErrors(), is(1));
      assertThat(response.getBody().getTests(), is(8));
      assertThat(response.getBody().getTime().toString(), is("0.201"));
      assertThat(response.getBody().getFailures(), is(1));
      assertThat(response.getBody().getName(), is("com.shape.CircleTest"));
      List<Property> properties = response.getBody().getProperties();
      assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
      assertThat(properties.get(0).getName(), is("java.runtime.name"));
      assertThat(properties.get(54).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
      assertThat(properties.get(54).getName(), is("sun.cpu.isalist"));
      List<TestCase> testCases = response.getBody().getTestCases();
      assertThat(testCases.get(0).getTime().toString(), is("0.01"));
      assertThat(testCases.get(0).getName(), is("testX"));
      assertThat(testCases.get(1).getTime().toString(), is("0"));
      assertThat(testCases.get(1).getName(), is("testY"));
      assertThat(testCases.get(2).getTime().toString(), is("0"));
      assertThat(testCases.get(2).getName(), is("testXY"));
      assertThat(testCases.get(3).getTime().toString(), is("0.01"));
      assertThat(testCases.get(3).getName(), is("testRadius"));
      assertThat(testCases.get(3).getFailure().getMessage(), is("expected:<20> but was:<10>"));
      assertThat(testCases.get(3).getFailure().getContent(), equalToIgnoringWhiteSpace("junit.framework.AssertionFailedError: expected:<20> but was:<10>\n      at junit.framework.Assert.fail(Assert.java:47)\n      at junit.framework.Assert.failNotEquals(Assert.java:282)\n      at junit.framework.Assert.assertEquals(Assert.java:64)\n      at junit.framework.Assert.assertEquals(Assert.java:201)\n      at junit.framework.Assert.assertEquals(Assert.java:207)\n      at com.shape.CircleTest.testRadius(CircleTest.java:34)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n      at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n", "")));
      assertThat(testCases.get(3).getSystemOut(), equalToIgnoringWhiteSpace("[OUT] : Getting the diameter"));
      assertThat(testCases.get(3).getSystemErr(), equalToIgnoringWhiteSpace("[ERR] : Getting the Circumference"));
      assertThat(testCases.get(4).getTime().toString(), is("0.02"));
      assertThat(testCases.get(4).getName(), is("testProperties"));
      assertThat(testCases.get(4).getError().getMessage(), is("/ by zero"));
      assertThat(testCases.get(4).getError().getContent(), equalToIgnoringWhiteSpace("java.lang.ArithmeticException: / by zero\n      at com.shape.CircleTest.testProperties(CircleTest.java:44)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n      at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n", "")));
      assertThat(testCases.get(4).getSystemOut(), equalToIgnoringWhiteSpace("[OUT] : Getting the diameter"));
      assertThat(testCases.get(4).getSystemErr(), equalToIgnoringWhiteSpace("[ERR] : Getting the Circumference"));
      assertThat(testCases.get(5).getTime().toString(), is("0"));
      assertThat(testCases.get(5).getName(), is("testPI"));
      assertThat(testCases.get(6).getTime().toString(), is("0.01"));
      assertThat(testCases.get(6).getName(), is("testCircumference"));
      assertThat(testCases.get(7).getTime().toString(), is("0"));
      assertThat(testCases.get(7).getName(), is("testDiameter"));
  }

  @Test
  public void parseTestSuiteJSONWithCircleTest() {
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-com.shape.CircleTest.json")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertThat(response.getBody().getErrors(), is(1));
    assertThat(response.getBody().getTests(), is(8));
    assertThat(response.getBody().getTime().toString(), is("0.201"));
    assertThat(response.getBody().getFailures(), is(1));
    assertThat(response.getBody().getName(), is("com.shape.CircleTest"));
    List<Property> properties = response.getBody().getProperties();
      assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
      assertThat(properties.get(0).getName(), is("java.runtime.name"));
      assertThat(properties.get(54).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
      assertThat(properties.get(54).getName(), is("sun.cpu.isalist"));
      List<TestCase> testCases = response.getBody().getTestCases();
      assertThat(testCases.get(0).getTime().toString(), is("0.01"));
      assertThat(testCases.get(0).getName(), is("testX"));
      assertThat(testCases.get(1).getTime().toString(), is("0"));
      assertThat(testCases.get(1).getName(), is("testY"));
      assertThat(testCases.get(2).getTime().toString(), is("0"));
      assertThat(testCases.get(2).getName(), is("testXY"));
      assertThat(testCases.get(3).getTime().toString(), is("0.01"));
      assertThat(testCases.get(3).getName(), is("testRadius"));
      assertThat(testCases.get(3).getFailure().getMessage(), is("expected:<20> but was:<10>"));
      assertThat(testCases.get(3).getFailure().getContent(), is("junit.framework.AssertionFailedError: expected:<20> but was:<10>\n      at junit.framework.Assert.fail(Assert.java:47)\n      at junit.framework.Assert.failNotEquals(Assert.java:282)\n      at junit.framework.Assert.assertEquals(Assert.java:64)\n      at junit.framework.Assert.assertEquals(Assert.java:201)\n      at junit.framework.Assert.assertEquals(Assert.java:207)\n      at com.shape.CircleTest.testRadius(CircleTest.java:34)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n      at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)"));
      assertThat(testCases.get(3).getSystemOut(), is("[OUT] : Getting the diameter"));
      assertThat(testCases.get(3).getSystemErr(), is("[ERR] : Getting the Circumference"));
      assertThat(testCases.get(4).getTime().toString(), is("0.02"));
      assertThat(testCases.get(4).getName(), is("testProperties"));
      assertThat(testCases.get(4).getError().getMessage(), is("/ by zero"));
      assertThat(testCases.get(4).getError().getContent(), is("java.lang.ArithmeticException: / by zero\n      at com.shape.CircleTest.testProperties(CircleTest.java:44)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n      at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)"));
      assertThat(testCases.get(4).getSystemOut(), equalToIgnoringWhiteSpace("[OUT] : Getting the diameter"));
      assertThat(testCases.get(4).getSystemErr(), equalToIgnoringWhiteSpace("[ERR] : Getting the Circumference"));
      assertThat(testCases.get(5).getTime().toString(), is("0"));
      assertThat(testCases.get(5).getName(), is("testPI"));
      assertThat(testCases.get(6).getTime().toString(), is("0.01"));
      assertThat(testCases.get(6).getName(), is("testCircumference"));
      assertThat(testCases.get(7).getTime().toString(), is("0"));
      assertThat(testCases.get(7).getName(), is("testDiameter"));


  }

  @Test
  public void parseTestSuiteXMLWithPointTest() {
    headers.setContentType(MediaType.APPLICATION_XML);
    String content = "junit.framework.AssertionFailedError: expected:<0> but was:<1>\n      at junit.framework.Assert.fail(Assert.java:47)\n      at junit.framework.Assert.failNotEquals(Assert.java:282)\n      at junit.framework.Assert.assertEquals(Assert.java:64)\n      at junit.framework.Assert.assertEquals(Assert.java:201)\n      at junit.framework.Assert.assertEquals(Assert.java:207)\n      at com.shape.PointTest.testXY(PointTest.java:28)\n" +
            "      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n " +
            "     at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n " +
            "     at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
            "      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n " +
            "     at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n    " +
            "  at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n" +
            "      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n", "");
    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-com.shape.PointTest.xml")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());

    assertThat(response.getBody().getErrors().toString(), is("0"));
    assertThat(response.getBody().getTests(), is(3
    ));
    assertThat(response.getBody().getName(), is("com.shape.PointTest"));
    assertThat(response.getBody().getTime().toString(), is("0.01"));
    assertThat(response.getBody().getFailures(), is(1));
    List<TestCase> responseTestCases = response.getBody().getTestCases();
    assertThat(responseTestCases.get(0).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(0).getName(), is("testX"));
    assertThat(responseTestCases.get(1).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(1).getName(), is("testY"));
    assertThat(responseTestCases.get(2).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(2).getName(), is("testXY"));
    assertThat(responseTestCases.get(2).getFailure().getMessage(), is("expected:<0> but was:<1>"));
    assertThat(responseTestCases.get(2).getFailure().getContent(), equalToIgnoringWhiteSpace(content));
    assertThat(responseTestCases.get(2).getSystemErr(), is("SYS-ERR"));
    assertThat(responseTestCases.get(2).getSystemOut(), is("SYS-OUT"));
  }

  @Test
  public void parseTestSuiteJSONWithPointTest() {
    headers.setContentType(MediaType.APPLICATION_JSON);
    String content = "junit.framework.AssertionFailedError: expected:<0> but was:<1>\n      at junit.framework.Assert.fail(Assert.java:47)\n      at junit.framework.Assert.failNotEquals(Assert.java:282)\n      at junit.framework.Assert.assertEquals(Assert.java:64)\n      at junit.framework.Assert.assertEquals(Assert.java:201)\n      at junit.framework.Assert.assertEquals(Assert.java:207)\n      at com.shape.PointTest.testXY(PointTest.java:28)\n" +
            "      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n " +
            "     at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:246)\n      at org.codehaus.surefire.battery.JUnitBattery.execute(JUnitBattery.java:220)\n " +
            "     at org.codehaus.surefire.Surefire.executeBattery(Surefire.java:203)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:152)\n      at org.codehaus.surefire.Surefire.run(Surefire.java:76)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
            "      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.surefire.SurefireBooter.run(SurefireBooter.java:104)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:241)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:357)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:479)\n " +
            "     at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:452)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:438)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:273)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:131)\n    " +
            "  at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:186)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:316)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n" +
            "      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)";
    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-com.shape.PointTest.json")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertThat(response.getBody().getName(), is("com.shape.PointTest"));
    assertThat(response.getBody().getErrors().toString(), is("0"));
    assertThat(response.getBody().getTests(), is(3));
    assertThat(response.getBody().getTime().toString(), is("0.01"));
    assertThat(response.getBody().getFailures(), is(1));
    List<TestCase> responseTestCases = response.getBody().getTestCases();
    assertThat(responseTestCases.get(0).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(0).getName(), is("testX"));
    assertThat(responseTestCases.get(1).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(1).getName(), is("testY"));
    assertThat(responseTestCases.get(2).getTime().toString(), is("0"));
    assertThat(responseTestCases.get(2).getName(), is("testXY"));
    assertThat(responseTestCases.get(2).getFailure().getMessage(), is("expected:<0> but was:<1>"));
    assertThat(responseTestCases.get(2).getFailure().getContent(), is(content));
    assertThat(responseTestCases.get(2).getSystemErr(), is("SYS-ERR"));
    assertThat(responseTestCases.get(2).getSystemOut(), is("SYS-OUT"));
  }

  @Test
  public void parseTestSuiteXMLWithoutPackage() {
    headers.setContentType(MediaType.APPLICATION_XML);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-NoPackageTest.xml")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertThat(response.getBody().getErrors(), is(0));
    assertThat(response.getBody().getTests(), is(3));
    assertThat(response.getBody().getTime().toString(), is("0.047"));
    assertThat(response.getBody().getFailures(), is(3));
    assertThat(response.getBody().getName(), is("NoPackageTest"));
    List<Property> properties = response.getBody().getProperties();
      assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
      assertThat(properties.get(0).getName(), is("java.runtime.name"));
      assertThat(properties.get(55).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
      assertThat(properties.get(55).getName(), is("sun.cpu.isalist"));
    List<TestCase> testCases = response.getBody().getTestCases();
    assertThat(testCases.get(0).getTime().toString(), is("0"));
    assertThat(testCases.get(0).getName(), is("testQuote"));
    assertThat(testCases.get(0).getFailure().getMessage(), is("\""));
    assertThat(testCases.get(0).getFailure().getContent(), equalToIgnoringWhiteSpace("junit.framework.AssertionFailedError: \"\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testQuote(NoPackageTest.java:23)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n","")));
    assertThat(testCases.get(1).getTime().toString(), is("0"));
    assertThat(testCases.get(1).getName(), is("testLower"));
    assertThat(testCases.get(1).getFailure().getMessage(), is("<"));
    assertThat(testCases.get(1).getFailure().getContent(), equalToIgnoringWhiteSpace("junit.framework.AssertionFailedError: <\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testLower(NoPackageTest.java:28)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n","")));
    assertThat(testCases.get(2).getTime().toString(), is("0"));
    assertThat(testCases.get(2).getName(), is("testGreater"));
    assertThat(testCases.get(2).getFailure().getMessage(), is(">"));
    assertThat(testCases.get(2).getFailure().getContent(), equalToIgnoringWhiteSpace("junit.framework.AssertionFailedError: >\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testGreater(NoPackageTest.java:33)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)".replaceAll("\n","")));
  }

  @Test
  public void parseTestSuiteJSONWithoutPackage() {
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(readTestFile(Paths.get(filePath + "TEST-NoPackageTest.json")), headers);
    ResponseEntity<TestSuite> response = testRestTemplate.postForEntity(getUrl(), entity, TestSuite.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertThat(response.getBody().getErrors(), is(0));
    assertThat(response.getBody().getTests(), is(3));
    assertThat(response.getBody().getTime().toString(), is("0.047"));
    assertThat(response.getBody().getFailures(), is(3));
    assertThat(response.getBody().getName(), is("NoPackageTest"));
    List<Property> properties = response.getBody().getProperties();
    assertThat(properties.get(0).getValue(), is("Java(TM) 2 Runtime Environment, Standard Edition"));
    assertThat(properties.get(0).getName(), is("java.runtime.name"));
    assertThat(properties.get(55).getValue(), is("pentium_pro+mmx pentium_pro pentium+mmx pentium i486 i386 i86"));
    assertThat(properties.get(55).getName(), is("sun.cpu.isalist"));
    List<TestCase> testCases = response.getBody().getTestCases();
    assertThat(testCases.get(0).getTime().toString(), is("0"));
    assertThat(testCases.get(0).getName(), is("testQuote"));
    assertThat(testCases.get(0).getFailure().getMessage(), is("\""));
    assertThat(testCases.get(0).getFailure().getContent(), is("junit.framework.AssertionFailedError: \"\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testQuote(NoPackageTest.java:23)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)"));
    assertThat(testCases.get(1).getTime().toString(), is("0"));
    assertThat(testCases.get(1).getName(), is("testLower"));
    assertThat(testCases.get(1).getFailure().getMessage(), is("<"));
    assertThat(testCases.get(1).getFailure().getContent(), is("junit.framework.AssertionFailedError: <\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testLower(NoPackageTest.java:28)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)"));
    assertThat(testCases.get(2).getTime().toString(), is("0"));
    assertThat(testCases.get(2).getName(), is("testGreater"));
    assertThat(testCases.get(2).getFailure().getMessage(), is(">"));
    assertThat(testCases.get(2).getFailure().getContent(), is("junit.framework.AssertionFailedError: >\n      at junit.framework.Assert.fail(Assert.java:47)\n      at NoPackageTest.testGreater(NoPackageTest.java:33)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at junit.framework.TestCase.runTest(TestCase.java:154)\n      at junit.framework.TestCase.runBare(TestCase.java:127)\n      at junit.framework.TestResult$1.protect(TestResult.java:106)\n      at junit.framework.TestResult.runProtected(TestResult.java:124)\n      at junit.framework.TestResult.run(TestResult.java:109)\n      at junit.framework.TestCase.run(TestCase.java:118)\n      at junit.framework.TestSuite.runTest(TestSuite.java:208)\n      at junit.framework.TestSuite.run(TestSuite.java:203)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.battery.JUnitBattery.executeJUnit(JUnitBattery.java:242)\n      at org.apache.maven.surefire.battery.JUnitBattery.execute(JUnitBattery.java:216)\n      at org.apache.maven.surefire.Surefire.executeBattery(Surefire.java:215)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:163)\n      at org.apache.maven.surefire.Surefire.run(Surefire.java:87)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.apache.maven.surefire.SurefireBooter.runTestsInProcess(SurefireBooter.java:300)\n      at org.apache.maven.surefire.SurefireBooter.run(SurefireBooter.java:216)\n      at org.apache.maven.test.SurefirePlugin.execute(SurefirePlugin.java:369)\n      at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:415)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:539)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:480)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkProjectLifecycle(DefaultLifecycleExecutor.java:867)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.forkLifecycle(DefaultLifecycleExecutor.java:739)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:510)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeStandaloneGoal(DefaultLifecycleExecutor.java:493)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:463)\n      at\n      org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:311)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:274)\n      at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:140)\n      at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:322)\n      at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:115)\n      at org.apache.maven.cli.MavenCli.main(MavenCli.java:249)\n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n      at java.lang.reflect.Method.invoke(Method.java:585)\n      at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)\n      at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)\n      at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)\n      at org.codehaus.classworlds.Launcher.main(Launcher.java:375)"));
  }

  private String readTestFile(Path filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(filePath)))) {

      String returnString = "";
      String line;
      while ((line = br.readLine()) != null) returnString += line;
      return returnString + "\n";

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getUrl() {
    return "http://localhost:" + this.port + "testsuites/";
  }

}
