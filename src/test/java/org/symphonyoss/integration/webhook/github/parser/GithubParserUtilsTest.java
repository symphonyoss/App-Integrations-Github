package org.symphonyoss.integration.webhook.github.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class GithubParserUtilsTest {

  private static final String TEST_STRING = "{ \"test\": \"success\" }";
  private static final String MALFORMATED_URI = "hsop##://9922";

  private InputStream inputStream;

  @Mock
  private Client baseClientTargetBuilder;

  @Mock
  private Response response;

  @Mock
  private WebTarget webTarget;

  @Mock
  private Invocation.Builder builder;

  @InjectMocks
  private GithubParserUtils githubParserUtils;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(GithubParserUtils.class);
    inputStream = new ByteArrayInputStream(TEST_STRING.getBytes(Charset.defaultCharset()));

    doReturn(Response.Status.OK.getStatusCode()).when(response).getStatus();
    doReturn(inputStream).when(response).getEntity();
    doReturn(response).when(builder).get();
    doReturn(builder).when(builder).accept(any(MediaType.class));
    doReturn(builder).when(webTarget).request();
    doReturn(webTarget).when(baseClientTargetBuilder).target(anyString());
  }

  @Test
  public void testGetJsonApi() throws IOException {
    JsonNode jsonNode = githubParserUtils.doGetJsonApi(StringUtils.EMPTY);

    // Due to the IS reading when calling doGetJsonApi
    inputStream.reset();

    assertEquals(JsonUtils.readTree(inputStream), jsonNode);
  }

  @Test(expected = ProcessingException.class)
  public void testFailToAccessUrl() throws IOException {
    doThrow(ProcessingException.class).when(builder).get();
    githubParserUtils.doGetJsonApi(StringUtils.EMPTY);
  }

  @Test
  public void testUnknownHost() throws IOException {
    UnknownHostException hostException = new UnknownHostException();
    ProcessingException processingException = new ProcessingException(hostException);
    doThrow(processingException).when(builder).get();
    assertNull(githubParserUtils.doGetJsonApi(StringUtils.EMPTY));
  }

  @Test
  public void testMalformedUrl() throws IOException {
    UnknownHostException hostException = new UnknownHostException();
    ProcessingException processingException = new ProcessingException(hostException);
    doThrow(processingException).when(builder).get();
    assertNull(githubParserUtils.doGetJsonApi(MALFORMATED_URI));
  }

  @Test
  public void testResponseStatusNotOK() throws IOException {
    doReturn(Response.Status.NOT_FOUND.getStatusCode()).when(response).getStatus();
    assertNull(githubParserUtils.doGetJsonApi(StringUtils.EMPTY));
  }

  @Test(expected = IOException.class)
  public void testMalformedResponse() throws IOException {
    doReturn(null).when(response).getEntity();
    githubParserUtils.doGetJsonApi(StringUtils.EMPTY);
  }
}