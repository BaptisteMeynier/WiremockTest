package org.test.wiremock.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ShopClient {
	
	public Response getUsers() {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:8089/rest/api/users");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		return invocationBuilder.get();
	}

}
