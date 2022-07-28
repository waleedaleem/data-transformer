package com.walid.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

/**
 * The Main class of the JSON data-transformer application
 *
 * @author wmoustaf
 */
public class Application {

    public static final String INPUT_FILE = "data.json";
    public static final String OUTPUT_FILE = "data-transformed.json";
    public static final String CUSTOMERS = "customers";
    public static final String CUSTOMER = "customer";
    public static final String ORDERS = "orders";
    public static final String ORDER = "order";
    public static final String ID = "id";
    public static final String ITEM = "item";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String REVENUE = "revenue";

    public static void main(String[] args) {
        String currentDirectory = System.getProperty("user.dir");
        File inputFile = new File(currentDirectory, INPUT_FILE);
        File outputFile = new File(currentDirectory, OUTPUT_FILE);

        Map<String, Boolean> outputConfig = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(outputConfig);

        try (
            InputStream inputStream = new FileInputStream(inputFile);
            JsonReader reader = Json.createReader(inputStream);
            OutputStream outputStream = new FileOutputStream(outputFile);
            JsonWriter writer = writerFactory.createWriter(outputStream)) {
            writer.write(transform(reader.readArray()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JsonObject transform(JsonArray input) {
        Set<JsonObject> customers = new HashSet<>();
        JsonArrayBuilder orders = Json.createArrayBuilder();
        input.getValuesAs(JsonObject.class)
            .forEach(transaction -> {
                customers.add(extractCustomer(transaction));
                orders.add(extractOrder(transaction));
            });

        return Json.createObjectBuilder()
            .add(CUSTOMERS, Json.createArrayBuilder(customers))
            .add(ORDERS, orders)
            .build();
    }

    private static JsonObject extractCustomer(JsonObject order) {
        return Json.createObjectBuilder(order.getJsonObject(CUSTOMER)).build();
    }

    private static JsonObject extractOrder(JsonObject order) {
        return Json.createObjectBuilder(order)
            .add(CUSTOMER, order.getJsonObject(CUSTOMER).getString(ID))
            .add(ORDER, transformItems(order.getJsonObject(ORDER)))
            .build();
    }

    private static JsonArray transformItems(JsonObject items) {
        JsonArrayBuilder outputItems = Json.createArrayBuilder();
        items.forEach(
            (key, value) -> outputItems.add(transformItem(key, value))
        );
        return outputItems.build();
    }

    private static JsonObject transformItem(String key, JsonValue value) {
        JsonObject item = value.asJsonObject();
        int quantity = item.getInt(QUANTITY);
        int price = item.getInt(PRICE);
        return Json.createObjectBuilder()
            .add(ITEM, key)
            .add(QUANTITY, quantity)
            .add(PRICE, price)
            .add(REVENUE, quantity * price)
            .build();
    }
}
