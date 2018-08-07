package com.javastaff.sse.test.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javastaff.sse.test.util.CustomSseEmitter;

@RequestMapping(
		value = "/test-api",
		produces = { MediaType.APPLICATION_JSON_VALUE  })
@RestController
public class TestSSEResource {
	
	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TestSSEResource.class);
	
	/**
	 * Instaura una connessione per ricevere messaggi in modalità SSE
	 */
	@RequestMapping(value = "/sse-emitter", method = RequestMethod.GET,produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public CustomSseEmitter getAlertEmitter() {
		LOG.info("Richiesta per sse-emitter");
		CustomSseEmitter sseEmitter=new CustomSseEmitter();
		new Thread(sseEmitter).start();
		try {
			sseEmitter.send(CustomSseEmitter.event().data("test"));
			sseEmitter.send(CustomSseEmitter.event().data("test2"));
		} catch (IOException e) {
			LOG.error(e.toString());
		}
		return sseEmitter;
	}
}
