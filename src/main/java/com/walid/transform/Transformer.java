package com.walid.transform;

import java.util.HashSet;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

/**
 * A JSON transformer which transforms JSON transaction array into a JSON object that consists of customers and orders arrays
 *
 * @author wmoustaf
 */
public class Transformer {

    public static final String CUSTOMERS = "customers";
    public static final String CUSTOMER = "customer";
    public static final String ORDERS = "orders";
    public static final String ORDER = "order";
    public static final String ID = "id";
    public static final String ITEM = "item";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String REVENUE = "revenue";

    private Transformer() {
        // no instances
    }

    /**
     * Splits input transaction array into customers and orders arrays
     *
     * @param input JSON transactions array
     * @return a JSON object that consists of customers and orders arrays
     */
    public static JsonObject transform(JsonArray input) {
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

    static JsonObject extractCustomer(JsonObject order) {
        return Json.createObjectBuilder(order.getJsonObject(CUSTOMER)).build();
    }

    /**
     * Transforms the JSON order object capturing the customer ID attribute
     *
     * @param order input order object
     * @return transformed order object
     */
    static JsonObject extractOrder(JsonObject order) {
        return Json.createObjectBuilder(order)
            .add(CUSTOMER, order.getJsonObject(CUSTOMER).getString(ID))
            .add(ORDER, transformItems(order.getJsonObject(ORDER)))
            .build();
    }

    static JsonArray transformItems(JsonObject items) {
        JsonArrayBuilder outputItems = Json.createArrayBuilder();
        items.forEach(
            (key, value) -> outputItems.add(transformItem(key, value.asJsonObject()))
        );
        return outputItems.build();
    }

    /**
     * Transforms a JSON item enclosing the item ID as a property
     *
     * @param itemID item ID
     * @param item   input item object
     * @return transformed item object
     */
    static JsonObject transformItem(String itemID, JsonObject item) {
        int quantity = item.getInt(QUANTITY);
        int price = item.getInt(PRICE);
        return Json.createObjectBuilder()
            .add(ITEM, itemID)
            .add(QUANTITY, quantity)
            .add(PRICE, price)
            .add(REVENUE, quantity * price)
            .build();
    }
}
