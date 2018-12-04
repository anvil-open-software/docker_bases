/*
 * Copyright 2018 Dematic, Corp.
 * Licensed under the MIT Open Source License: https://opensource.org/licenses/MIT
 */

package com.dematic.labs.docker_bases.wildfly_8_2_0_final;

import org.apache.http.HttpHeaders;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.CoreMatchers;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.jacoco.core.tools.ExecFileLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.Socket;

import static com.dematic.labs.toolkit.helpers.test_util.DockerHelper.getPort;
import static com.dematic.labs.toolkit.helpers.test_util.SecretHelper.getSecret;
import static java.lang.Integer.parseInt;
import static java.net.InetAddress.getLocalHost;
import static junit.framework.TestCase.assertTrue;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;

public class WildflyPortIT {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void instanceWithoutAdminConsoleShouldHaveWelcomeServlet() throws IOException {
        assertEquals(200, getWelcomePage("servlet1.port"));
    }

    @Test
    public void instanceWithoutAdminConsoleShouldFailGettingAdminConsole() throws IOException {
        expectedException.expect(NoHttpResponseException.class);
        expectedException.expectMessage(CoreMatchers.endsWith("failed to respond"));
        getServerState("admin1.port");
    }

    @Test
    public void instanceWithAdminConsoleShouldHaveWelcomeServlet() throws IOException {
        assertEquals(200, getWelcomePage("servlet2.port"));
    }

    @Test
    public void instanceWithAdminConsoleShouldHaveAdminConsole() throws IOException {
        assertEquals(200, getServerState("admin2.port"));
    }

    @Test
    public void instanceWithJacocoShouldHaveWelcomeServlet() throws IOException {
        assertEquals(200, getWelcomePage("servlet3.port"));
    }

    @Test
    public void instanceWithJacocoShouldContactTheJacocoAgent() throws IOException {
        assertTrue(canContactRemoteJacocoAgent("jacoco3.port"));
    }

    @Test
    public void instanceWithoutJacocoShouldFailToContactTheJacocoAgent() throws IOException {
        expectedException.expect(IOException.class);
        canContactRemoteJacocoAgent("jacoco2.port");
    }

    @Test
    public void instanceWithWaitForShouldHaveWelcomeServlet() throws IOException {
        assertEquals(200, getWelcomePage("servlet4.port"));
    }

    private int getWelcomePage(@Nonnull final String propertyName) throws IOException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpUriRequest request = RequestBuilder
                .get("http://localhost:" + getPort(propertyName))
                .setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON.getMimeType())
                .build();
            try (final CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        }
    }

    /**
     * The wildfly admin console is a gwt application; we reduce the test to asserting access to the
     * <a href="https://docs.jboss.org/author/display/WFLY8/The+HTTP+management+API">underlying rest api</a>.
     */
    private int getServerState(@Nonnull final String portName) throws IOException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpUriRequest request = RequestBuilder
                .post("http://localhost:" + getPort(portName) + "/management")
                .setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON.getMimeType())
                .setEntity(
                    new StringEntity(
                        "{\"operation\":\"read-attribute\",\"name\":\"server-state\",\"json.pretty\":1}",
                        APPLICATION_JSON))
                .build();

            final HttpClientContext context = HttpClientContext.create();
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider
                .setCredentials(
                    new AuthScope("localhost", parseInt(getPort(portName)), "ManagementRealm"),
                    new UsernamePasswordCredentials(
                        getSecret("withAdmin/WILDFLY_ADMIN_USER"),
                        getSecret("withAdmin/WILDFLY_ADMIN_PASSWORD")
                    ));
            context.setCredentialsProvider(credentialsProvider);

            try (final CloseableHttpResponse response = client.execute(request, context)) {
                return response.getStatusLine().getStatusCode();
            }
        }
    }

    private boolean canContactRemoteJacocoAgent(@Nonnull final String jacocoPortName) throws IOException {
        // todo: update to jacoco 0.7.10 and use:
        /*
        new org.jacoco.core.tools.ExecDumpClient().dump(getLocalHost(), parseInt(getPort(jacocoPortName)));
        return true;
        */
        try (final Socket socket = new Socket(getLocalHost(), parseInt(getPort(jacocoPortName)))) {
            final RemoteControlWriter remoteWriter = new RemoteControlWriter(socket.getOutputStream());
            remoteWriter.visitDumpCommand(true, false);

            final RemoteControlReader remoteReader = new RemoteControlReader(socket.getInputStream());
            final ExecFileLoader loader = new ExecFileLoader();
            remoteReader.setSessionInfoVisitor(loader.getSessionInfoStore());
            remoteReader.setExecutionDataVisitor(loader.getExecutionDataStore());
            if (!remoteReader.read()) {
                throw new IOException("Socket closed unexpectedly.");
            }
            return true;
        }
    }
}
