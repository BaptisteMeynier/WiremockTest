package org.test.wiremock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.test.wiremock.client.ShopClient;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;


@RunWith(JUnit4.class)
public class SimpleTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080

	private Client client = ClientBuilder.newClient();

	private WebTarget webTarget = client.target("http://localhost:8089/rest/api");

	@Test
	public void should_mock_anyurl() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}
}
