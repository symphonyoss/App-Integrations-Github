package org.symphonyoss.integration.webhook.github.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.webhook.github.CommonGithubTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Unit test for {@link GithubParserUtils}
 * Created by rsanchez on 09/02/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubParserUtilsTest extends CommonGithubTest {

  private static final String TEST_URL = "https://api.github.com/users/testuser";

  private static final String HOST = "api.github.com";

  @Mock
  private Client baseClientTargetBuilder;

  @Spy
  private List<String> unknownHosts = new ArrayList<>();

  @InjectMocks
  private GithubParserUtils utils = new GithubParserUtils();

  @Mock
  private Invocation.Builder builder;

  @Before
  public void init() throws IOException {
    WebTarget target = mock(WebTarget.class);

    doReturn(target).when(baseClientTargetBuilder).target(TEST_URL);
    doReturn(builder).when(target).request();
    doReturn(builder).when(builder).accept(MediaType.APPLICATION_JSON_TYPE);
  }

  @Test
  public void testUnknownHostException() throws IOException {
    assertEquals(0, unknownHosts.size());

    ProcessingException exception = new ProcessingException(new UnknownHostException(HOST));
    doThrow(exception).when(builder).get();

    JsonNode node = utils.doGetJsonApi(TEST_URL);
    assertNull(node);
    assertEquals(1, unknownHosts.size());
    assertEquals(HOST, unknownHosts.get(0));

    node = utils.doGetJsonApi(TEST_URL);
    assertNull(node);
    assertEquals(1, unknownHosts.size());
    assertEquals(HOST, unknownHosts.get(0));
  }

  @Test(expected = ProcessingException.class)
  public void testProcessingException() throws IOException {
    ProcessingException exception = new ProcessingException(new RuntimeException());
    doThrow(exception).when(builder).get();

    utils.doGetJsonApi(TEST_URL);
    assertEquals(0, unknownHosts.size());
  }

  @Test
  public void testApiError() throws IOException {
    doReturn(Response.serverError().build()).when(builder).get();

    JsonNode node = utils.doGetJsonApi(TEST_URL);
    assertNull(node);
  }

  @Test(expected = IOException.class)
  public void testIOException() throws IOException {
    doReturn(Response.ok().build()).when(builder).get();
    utils.doGetJsonApi(TEST_URL);
  }

  @Test
  public void testOK() throws IOException {
    String jsonFile = "payload_github_public_info_baxterthehacker.json";
    mockRequest(jsonFile);

    JsonNode result = utils.doGetJsonApi(TEST_URL);

    JsonNode expected = getJsonFile(jsonFile);
    assertEquals(expected, result);
  }

  private void mockRequest(String jsonFile) {
    InputStream resourceAsStream = classLoader.getResourceAsStream(jsonFile);

    Response response = Response.ok(resourceAsStream).build();
    doReturn(response).when(builder).get();
  }
}
