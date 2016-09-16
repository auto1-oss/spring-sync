/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sync.diffsync.web;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.sync.Patch;
import org.springframework.sync.json.JsonPatchPatchConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link HttpMessageConverter} that converts "application/json-patch+json" payloads to/from {@link Patch} objects.
 * @author Craig Walls
 */
public class JsonPatchHttpMessageConverter extends AbstractHttpMessageConverter<Patch> {

	private static final MediaType JSON_PATCH = new MediaType("application", "json-patch+json");

	private ObjectMapper objectMapper;

	private JsonPatchPatchConverter jsonPatchMaker;

	public JsonPatchHttpMessageConverter(ObjectMapper objectMapper) {
		setSupportedMediaTypes(Collections.singletonList(JSON_PATCH));
		this.jsonPatchMaker = new JsonPatchPatchConverter(objectMapper);
		this.objectMapper = objectMapper;
	}

	public JsonPatchHttpMessageConverter() {
		this(new ObjectMapper());
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return Patch.class.isAssignableFrom(clazz);
	}

	@Override
	protected Patch readInternal(Class<? extends Patch> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return jsonPatchMaker.convert(objectMapper.readTree(inputMessage.getBody()));
	}

	@Override
	protected void writeInternal(Patch patch, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		outputMessage.getHeaders().setContentType(JSON_PATCH);
		objectMapper.writer().writeValue(outputMessage.getBody(), jsonPatchMaker.convert(patch));
	}

}
