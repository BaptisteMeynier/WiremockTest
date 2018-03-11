package org.test.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

import java.util.stream.IntStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.test.wiremock.client.ShopClient;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(JUnit4.class)
public class StubTest {

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

	@Test
	public void should_mock_url_by_regex() {
		stubFor(get(urlMatching(".*users")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}


	@Test
	public void should_mock_with_exact_url() {
		stubFor(get(urlEqualTo("/rest/api/users")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();

		assertEquals(200,response.getStatus());
	}

	@Test
	public void should_mock_url_with_regex_for_path_parameter() {
		stubFor(get(urlMatching("/rest/api/users/.*")).willReturn(aResponse().withStatus(200)));

		Response response = webTarget
				.path("users")
				.path("12")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
	}

	@Test
	public void should_mock_slowest_response_with_fixed_delay() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200).withFixedDelay(2000)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
	}

	@Test
	public void should_mock_slowest_response_with_interval_delay() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200).withUniformRandomDelay(0, 2000)));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
	}



	@Test
	public void should_mock_response_body() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200).withBody("value")));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
		assertEquals("value",response.readEntity(String.class));
	}

	@Test
	public void should_mock_header() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200).withHeader("country", "France")));

		Response response = webTarget
				.path("users")
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertEquals(200,response.getStatus());
		assertEquals("France",response.getHeaderString("country"));
	}

	@Test
	public void should_mock_multiple_request() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

		IntStream.range(0, 5).forEach(i -> {
			Response response =webTarget.path("users").request().get();
			assertEquals(200,response.getStatus());
		});  
	}
	
	@Test
	public void should_mock_each_request() {
		stubFor(get(urlMatching("/rest/api/users")).willReturn(aResponse().withStatus(200)));
		
		Response response =webTarget.path("users").request().get();
		assertEquals(200,response.getStatus());
		
		response =webTarget.path("users").request().get();
		assertEquals(200,response.getStatus());
		
		response =webTarget.path("users").request().get();
		assertEquals(200,response.getStatus());
	}

	
	@Test
	public void should_mock_through_object_with_matching() {
		stubFor(get(urlMatching("/rest/api/users")).willReturn(aResponse().withStatus(200)));
		
		Response response = new ShopClient().getUsers();
		
		assertEquals(200,response.getStatus());
	}
	
	@Test
	public void should_mock_through_objectwith_any_url() {
		stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));
		
		Response response = new ShopClient().getUsers();
		
		assertEquals(200,response.getStatus());
	}
	
}
