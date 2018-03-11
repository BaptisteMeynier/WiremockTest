package org.test.wiremock;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.util.stream.IntStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Verify will check that the request is correctly formated
 * @author baptiste
 *
 */
@RunWith(JUnit4.class)
public class VerifyTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080

	private Client client = ClientBuilder.newClient();

	private WebTarget webTarget = client.target("http://localhost:8089/rest/api");

	@Test
	public void should_verify_exact_url() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
		verify(getRequestedFor(urlMatching("/rest/api/users")));
	}


	@Test
	public void should_verify_header_parameter_is_matching() {
		stubFor(get(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request()
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());

		verify(getRequestedFor(
				urlMatching("/rest/api/users"))
				.withHeader("Content-Type", matching(MediaType.APPLICATION_JSON)));
	}

	@Test
	public void should_verify_header_parameter_not_match() {
		stubFor(get(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request()
				.header("country", "France")
				.get();
		assertEquals(200,response.getStatus());

		verify(getRequestedFor(
				urlMatching("/rest/api/users"))
				.withHeader("country", notMatching("Germany")));
	}

	@Test
	public void should_verify_missing_header_parameter() {
		stubFor(get(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request()
				.get();
		assertEquals(200,response.getStatus());

		verify(getRequestedFor(
				urlMatching("/rest/api/users"))
				.withoutHeader("country"));
	}

	@Test
	public void should_verify_request_body_value() {
		stubFor(post(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request()
				.post(Entity.entity("<message>value</message>", MediaType.TEXT_XML));

		assertEquals(200,response.getStatus());
		verify(postRequestedFor(
				urlMatching("/rest/api/users"))
				.withRequestBody(matching(".*<message>value</message>.*")));
	}



	@Test
	public void should_verify_query_param() {
		stubFor(get(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.queryParam("country", "France")
				.request()
				.get();

		assertEquals(200,response.getStatus());
		verify(getRequestedFor(anyUrl()).withQueryParam("country", matching("France")));
	}	

	@Test
	public void should_verify_path_param_with_regex() {
		stubFor(get(anyUrl())
				.willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.path("1245")
				.request()
				.get();

		assertEquals(200,response.getStatus());
		verify(getRequestedFor(urlMatching("/rest/api/users/[a-z0-9]+")));
	}


	@Ignore
	@Test(expected=VerificationException.class)
	public void should_verify_that_all_request_were_mocked() {
		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));

		webTarget.path("users").queryParam("country", "France").request().get();
		verify(getRequestedFor(urlEqualTo("/rest/api/users")).withQueryParam("country", matching("France")));

		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));

		webTarget.path("users").queryParam("country", "France").request().get();
		verify(getRequestedFor(urlEqualTo("/rest/api/users")).withQueryParam("country", matching("France")));

	}

	@Ignore
	@Test(expected=VerificationException.class)
	public void should_not_verify_that_all_request_were_mocked() {
		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));

		webTarget.path("users").queryParam("country", "France").request().get();
		verify(getRequestedFor(urlEqualTo("/rest/api/users")).withQueryParam("country", matching("France")));

		webTarget.path("users").queryParam("country", "France").request().get();
		verify(getRequestedFor(urlEqualTo("/rest/api/users")).withQueryParam("country", matching("France")));

	}

	/*
	 verify(lessThan(5), postRequestedFor(urlEqualTo("/many")));
	 verify(lessThanOrExactly(5), postRequestedFor(urlEqualTo("/many")));
	 verify(exactly(5), postRequestedFor(urlEqualTo("/many")));
	 verify(moreThanOrExactly(5), postRequestedFor(urlEqualTo("/many")));
	 verify(moreThan(5), postRequestedFor(urlEqualTo("/many")));
	 */

	@Test
	public void should_verify_that_less_5_request_were_played() {
		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));
		IntStream.range(0, 4).forEach(i -> {webTarget.path("users").request().get();});  

		verify(lessThan(5), getRequestedFor(urlEqualTo("/rest/api/users")));
	}

	@Test(expected=VerificationException.class)
	public void given_40_request_should_verify_that_less_5_request_were_played() {
		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));
		IntStream.range(0, 40).forEach(i -> {webTarget.path("users").request().get();});  

		verify(lessThan(5), getRequestedFor(urlEqualTo("/rest/api/users")));
	}

	@Test
	public void should_verify_that_exactly_5_request_were_played() {
		stubFor(get(urlEqualTo("/rest/api/users"))
				.willReturn(aResponse().withStatus(200)));
		IntStream.range(0, 5).forEach(i -> {webTarget.path("users").request().get();});  

		verify(exactly(5), getRequestedFor(urlEqualTo("/rest/api/users")));
	}

}
