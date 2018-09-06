package io.testrex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.testrex.model.Project;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@ActiveProfiles("itest")
public class ProjectRestControllerTests {
    private final HttpHeaders headers = new HttpHeaders();
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private Project getTestProject(String name) {
        Project project = new Project();
        project.setName(name);
        project.setDesc("Test project.");
        return project;
    }

    @Test
    public void createProjectFromJSONReturns200() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Project requestProject = getTestProject("PostJSONTest");
        objectMapper = new ObjectMapper();
        ResponseEntity<Project> response = createProject(requestProject);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(requestProject.toString(), is(response.getBody().toString()));


    }

    private ResponseEntity<Project> createProject(Project project) {

        try {
            if (objectMapper == null) {
                headers.setContentType(MediaType.APPLICATION_JSON);
                objectMapper = new ObjectMapper();
            }
            HttpEntity<String> entity = entity = new HttpEntity<>(objectMapper.writeValueAsString(project), headers);
            ResponseEntity<Project> response = testRestTemplate.postForEntity(getUrl(), entity, Project.class);
            return response;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.noContent().build();
        }
    }


    @Test
    public void createProjectFromXMLReturns200() {
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

        Project requestProject = getTestProject("PostXMLTest");

        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        objectMapper = new XmlMapper(xmlModule);
        ResponseEntity<Project> response = createProject(requestProject);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(requestProject.toString(), is(response.getBody().toString()));

    }

    @Test
    public void getProjectReturnsJSON() {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Project requestProject = getTestProject("GetJSonTest");
        ResponseEntity<Project> responseEntity = createProject(requestProject);
        ResponseEntity<String> response = testRestTemplate.getForEntity(getUrl() + responseEntity.getBody().getProjectId(), String.class, headers);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        boolean valid = true;
        try {
            objectMapper = new ObjectMapper();
            objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            valid = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(valid);

    }

    @Test
    public void getProjectReturnsXML() {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        Project requestProject = getTestProject("GetXMLTest");
        ResponseEntity<Project> responseEntity = createProject(requestProject);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = testRestTemplate.exchange(getUrl() + responseEntity.getBody().getProjectId(), HttpMethod.GET, entity, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        objectMapper = new XmlMapper(xmlModule);
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
    public void putProjectReturns200() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();

        Project requestProject = getTestProject("DeleteTest");
        ResponseEntity<Project> responseEntity = createProject(requestProject);
        Project putProject = getTestProject("PutProject");
        testRestTemplate.put(getUrl() + responseEntity.getBody().getProjectId(), putProject, String.class);
        ResponseEntity<Project> response = testRestTemplate.getForEntity(getUrl() + responseEntity.getBody().getProjectId(), Project.class);

        assertThat(putProject.toString(), is(response.getBody().toString()));


    }


    @Test
    public void deleteProjectReturns200() {
        Project requestProject = getTestProject("DeleteTest");
        ResponseEntity<Project> responseEntity = createProject(requestProject);
        testRestTemplate.delete(getUrl() + responseEntity.getBody().getProjectId(), String.class);
        ResponseEntity<String> response = testRestTemplate.getForEntity(getUrl() + responseEntity.getBody().getProjectId(), String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private String getUrl() {
        return "http://localhost:" + this.port + "/projects/";
    }
}
