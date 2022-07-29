package com.walid.transform;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;

import java.util.HashSet;
import java.util.Set;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

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
        JsonArrayBuilder orders = createArrayBuilder();
        input.getValuesAs(JsonObject.class)
            .forEach(transaction -> {
                customers.add(transaction.getJsonObject(CUSTOMER));
                orders.add(extractOrder(transaction));
            });

        return createObjectBuilder()
            .add(CUSTOMERS, createArrayBuilder(customers))
            .add(ORDERS, orders)
            .build();
    }

    /**
     * Transforms the JSON transaction object to an order object capturing the customer ID attribute
     *
     * @param transaction input transaction object
     * @return transformed order object
     */
    static JsonObject extractOrder(JsonObject transaction) {
        return createObjectBuilder(transaction)
            .add(CUSTOMER, transaction.getJsonObject(CUSTOMER).getString(ID))
            .add(ORDER, transformItems(transaction.getJsonObject(ORDER)))
            .build();
    }

    static JsonArray transformItems(JsonObject items) {
        JsonArrayBuilder outputItems = createArrayBuilder();
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
        return createObjectBuilder()
            .add(ITEM, itemID)
            .add(QUANTITY, quantity)
            .add(PRICE, price)
            .add(REVENUE, quantity * price)
            .build();
    }
}
