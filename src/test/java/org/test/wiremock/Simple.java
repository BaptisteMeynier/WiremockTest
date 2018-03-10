package org.test.wiremock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.test.wiremock.client.Client1;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;


@RunWith(JUnit4.class)
public class Simple {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080

	private Client client = ClientBuilder.newClient();

	private WebTarget webTarget = client.target("http://localhost:8089/rest/api");

	@Test
	public void exampleTest1() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}

	@Test
	public void exampleTest2() {
		stubFor(get(urlMatching(".*users")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}


	@Test
	public void exampleTest3() {
		stubFor(get(urlEqualTo("/rest/api/users")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}

	@Test//(expected=VerificationException.class)
	public void exampleTest4() {
		stubFor(get(urlMatching("/rest/api/users/.*")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.path("12")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
	}
	
	
	@Test
	public void exampleTest5() {
		stubFor(get(anyUrl()).willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON).withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
		verify(getRequestedFor(urlMatching("/rest/api/users"))
	            .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON)));
	}

	 /*
	@Test
	public void exampleTest3() {
		stubFor(get(anyUrl())
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON)
						.withBody("<response>Some content</response>")));


		WebTarget webTarget = client.target("http://localhost:8089/rest/api/users");
		Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

		assertEquals(200,response.getStatus());

		verify(getRequestedFor(urlMatching("/rest/api/users"))
				.withRequestBody(matching(".*<message>1234</message>.*"))
				.withHeader("Content-Type", notMatching("application/json")));
	}*/

}
