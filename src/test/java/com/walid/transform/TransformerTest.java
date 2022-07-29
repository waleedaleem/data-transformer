package com.walid.transform;

import static com.walid.transform.Transformer.CUSTOMER;
import static com.walid.transform.Transformer.CUSTOMERS;
import static com.walid.transform.Transformer.ID;
import static com.walid.transform.Transformer.ITEM;
import static com.walid.transform.Transformer.ORDER;
import static com.walid.transform.Transformer.ORDERS;
import static com.walid.transform.Transformer.PRICE;
import static com.walid.transform.Transformer.QUANTITY;
import static com.walid.transform.Transformer.REVENUE;
import static com.walid.transform.Transformer.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

class TransformerTest {

    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String TEST_ID1 = "hat";
    public static final String TEST_ID2 = "cake";
    public static final String VENDOR = "vendor";
    public static final String DATE = "date";
    private static JsonObject testItem1;
    private static JsonObject testItem2;
    private static JsonObject testItems;
    private static JsonObject testCustomer1;
    private static JsonObject testTransaction1;

    private static JsonArray expectedItems;
    private static JsonObject expectedOrder;

    @BeforeAll
    static void setUp() {
        testItem1 = Json.createObjectBuilder()
            .add(QUANTITY, 14)
            .add(PRICE, 8)
            .build();

        testItem2 = Json.createObjectBuilder()
            .add(QUANTITY, 9)
            .add(PRICE, 3)
            .build();

        testItems = Json.createObjectBuilder()
            .add(TEST_ID1, testItem1)
            .add(TEST_ID2, testItem2)
            .build();

        testCustomer1 = Json.createObjectBuilder()
            .add(ID, "8baa6dea-cc70-4748-9b27-b174e70e4b66")
            .add(NAME, "Lezlie Stuther")
            .add(ADDRESS, "19045 Lawn Court")
            .build();

        testTransaction1 = Json.createObjectBuilder()
            .add(ID, 1)
            .add(VENDOR, "acme")
            .add(DATE, "03/03/2017")
            .add(CUSTOMER, testCustomer1)
            .add(ORDER, testItems)
            .build();

        expectedItems = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add(ITEM, TEST_ID1)
                .add(QUANTITY, 14)
                .add(PRICE, 8)
                .add(REVENUE, 112)
                .build())
            .add(Json.createObjectBuilder()
                .add(ITEM, TEST_ID2)
                .add(QUANTITY, 9)
                .add(PRICE, 3)
                .add(REVENUE, 27)
                .build())
            .build();

        expectedOrder = Json.createObjectBuilder()
            .add(ID, 1)
            .add(VENDOR, "acme")
            .add(DATE, "03/03/2017")
            .add(CUSTOMER, "8baa6dea-cc70-4748-9b27-b174e70e4b66")
            .add(ORDER, expectedItems)
            .build();
    }

    @Test
    void testTransform() {
        JsonObject expectedOutput = Json.createObjectBuilder()
            .add(CUSTOMERS, Json.createArrayBuilder().add(testCustomer1).build())
            .add(ORDERS, Json.createArrayBuilder().add(expectedOrder).build())
            .build();

        assertEquals(expectedOutput, transform(Json.createArrayBuilder()
            .add(testTransaction1).build()));
    }

    @Test
    void testExtractOrder() {
        assertEquals(expectedOrder, Transformer.extractOrder(testTransaction1));
    }

    @Test
    void testTransformItems() {
        assertEquals(expectedItems, Transformer.transformItems(testItems));
    }

    @Test
    void testTransformItem() {
        final JsonObject expected = Json.createObjectBuilder(testItem1)
            .add(ITEM, TEST_ID1)
            .add(REVENUE, 112)
            .build();

        JsonObject transformedItem = Transformer.transformItem(TEST_ID1, testItem1);

        assertEquals(expected, transformedItem);
    }
}