package com.javastaff.sse.test.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Estensione di {@link SseEmitter} 
 * per la gestione della disconnessione dei client
 */
public class CustomSseEmitter extends SseEmitter implements Runnable{
	
	private boolean running=false;
	
	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CustomSseEmitter.class);
	
	public CustomSseEmitter() {
		super(Long.MAX_VALUE);
		this.running=true;
	}
	
	/**
	 * Override del metodo send per gestire la disconnessione
	 */
	@Override
	public void send(Object object) throws IOException {
		try {
			super.send(object);
		} catch(IOException e) {
			gestioneDisconnessione(e);
		}
	}
	
	/**
	 * Override del metodo send con SseEventBuilder 
	 * per gestire la disconnessione
	 */
	@Override
	public void send(SseEventBuilder builder) throws IOException {
		try {
			super.send(builder);
		} catch (IOException e) {
			gestioneDisconnessione(e);
		}
	}
	
	private void gestioneDisconnessione(Exception e) {
		//Rimuove il destinatario dal map
		LOG.info("Client disconnesso");
		this.completeWithError(e);
		this.running=false;
	}
	
	/**
	 * Implementazione del metodo run che permette di gestire l'heartbeat 
	 * inviato al client per verificare che sia sempre connesso
	 */
	@Override
	public void run() {
		while(running) {
			try {
				send(SseEmitter.event().comment("ping"));
				Thread.sleep(30000L);
			} catch (IOException e) {
				gestioneDisconnessione(e);
			} catch (InterruptedException e) {
				gestioneDisconnessione(e);
				Thread.currentThread().interrupt();
				LOG.error(e.toString());
			}
		}
	}
}
