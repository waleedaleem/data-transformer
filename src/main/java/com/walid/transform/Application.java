package com.walid.transform;

import static jakarta.json.Json.createReader;
import static jakarta.json.Json.createWriterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;

/**
 * The Main class of the JSON data-transformer application
 *
 * @author wmoustaf
 */
public class Application {

    private static Logger logger;

    private static final String INPUT_FILE = "data.json";
    private static final String OUTPUT_FILE = "data-transformed.json";
    private static final String currentDirectory = System.getProperty("user.dir");
    private static final File inputFile = new File(currentDirectory, INPUT_FILE);
    private static final File outputFile = new File(currentDirectory, OUTPUT_FILE);

    /**
     * The entry point of application.
     *
     * @param args the input arguments (nothing expected)
     */
    public static void main(String[] args) {

        Map<String, Boolean> outputConfig = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = createWriterFactory(outputConfig);

        initialiseLogging();
        logger.info(() -> String.format("Reading input from ./%s and writing output to ./%s", INPUT_FILE, OUTPUT_FILE));

        try (
            InputStream inputStream = new FileInputStream(inputFile);
            JsonReader reader = createReader(inputStream);
            OutputStream outputStream = new FileOutputStream(outputFile);
            JsonWriter writer = writerFactory.createWriter(outputStream)) {

            writer.write(Transformer.transform(reader.readArray()));
            logger.info(() -> "All done!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "File handling error.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Generic processing error.");
        }
    }

    private static void initialiseLogging() {
        try {
            LogManager.getLogManager()
                .readConfiguration(Application.class.getClassLoader().getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger = Logger.getLogger(Application.class.getName());
    }
}
