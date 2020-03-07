package dev.thiago.server;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import dev.thiago.entities.Produto;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
	
	private Map<Integer, Produto> produtos = new TreeMap<>();
	private static final AtomicInteger COUNTER = new AtomicInteger();

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		mockProdutos();

		Router router = Router.router(vertx);
		
		router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Projeto desenvolvido com Vertx e Java para cadastro de produtos."
            		+ "O id gerado automaticamente também é o número de série pois ele é único o que impede o cadastro de produtos repetidos.</h1>");
        });
		
		router.route("/api/produtos*").handler(BodyHandler.create());
		router.post("/api/produtos").handler(this::addProduto);
		router.get("/api/produtos").handler(this::getAll);
		router.get("/api/produtos/:id").handler(this::getById);
		router.put("/api/produtos/:id").handler(this::update);
		router.delete("/api/produtos/:id").handler(this::delete);

		vertx.createHttpServer().requestHandler(event -> router.accept(event))
		.listen(8080, http -> {
			if (http.succeeded()) {
				startPromise.complete();
				System.out.println("HTTP server started on port 8080");
			} else {
				startPromise.fail(http.cause());
			}
		});
	}

	private void addProduto(RoutingContext routingContext) {
		final Produto produto = Json.decodeValue(routingContext.getBodyAsString(), Produto.class);

		if (produto.getNome() == "" || produto.getBarra() == "") {
			routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
			.end(Json.encodePrettily("Nenhum campo pode ficar vazio."));	
			
		} else {
			produto.setId(COUNTER.getAndIncrement());
			produtos.put(produto.getId(), produto);
			
			routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
			.end(Json.encodePrettily(produto));
		}
	}

	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(produtos.values()));
	}

	private void getById(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Produto produto = produtos.get(idAsInteger);
			if (produto == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(produto));
			}
		}
	}

	private void update(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Produto produto = produtos.get(idAsInteger);
			if (produto == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				produto.setNome(json.getString("nome"));
				produto.setBarra(json.getString("barra"));
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(produto));
			}
		}
	}

	private void delete(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			produtos.remove(idAsInteger);
		}
		routingContext.response().setStatusCode(204).end();
	}

	private void mockProdutos() {
		Produto produto = new Produto();		
		produto.setId(COUNTER.getAndIncrement());
		produto.setNome("Produto de Exemplo");
		produto.setBarra("198237");
		produtos.put(produto.getId(), produto);
		
	}
}
