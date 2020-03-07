package dev.thiago.inicio;

import dev.thiago.server.MainVerticle;
import io.vertx.core.Vertx;

public class Run {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
		System.out.println(">>> Verticle iniciado e rodando na porta 8080");

	}

}
