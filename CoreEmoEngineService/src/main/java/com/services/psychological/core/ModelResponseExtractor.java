package com.services.psychological.core;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelResponseExtractor implements ResponseExtractor<ModelValue> {

	@Override
	public ModelValue extractData(ClientHttpResponse modelResponse)
			throws IOException {
		
		return new ObjectMapper().readValue(
				modelResponse.getBody(), ModelValue.class);
	}

}
