package com.item.core.controller;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.item.core.model.Item;
import com.item.core.model.Product;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@EnableCircuitBreaker
public class ItemsController {
	
	@GetMapping("/items")
	@HystrixCommand(fallbackMethod = "getAllItemsFallback")
	public Item[] getAllItems(){
		final String uri = "http://localhost:8080/products";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Product[]> response = restTemplate.getForEntity(uri, Product[].class);
		Product[] products = response.getBody();
		
		Item[] items = new Item[products.length];
		
		for (int i = 0; i < products.length; i++) {
			Item item = new Item();
			Product product = products[i];
			
			item.setId(product.getId());
			item.setProduct(product);
			item.setQuantity(1);
			
			items[i] = item;
		}
		
		return items;
	}
	
	public Item[] getAllItemsFallback(){
		return new Item[0];
	}
	
	@GetMapping("/item/{id}/quantity/{quantity}")
	@HystrixCommand(fallbackMethod = "getItemByIdFallback")
	public Item getItemById(@PathVariable final Long id, @PathVariable final Integer quantity) {
		final String uri = "http://localhost:8080/product/" + id;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Product> response = restTemplate.getForEntity(uri, Product.class);
		Product product = response.getBody();
		
		Item item = new Item();
		
		item.setId(product.getId());
		item.setProduct(product);
		item.setQuantity(1);
		
		return item;
	}
	
	public Item getItemByIdFallback(@PathVariable final Long id, @PathVariable final Integer quantity) {
		return new Item();
	}

}
