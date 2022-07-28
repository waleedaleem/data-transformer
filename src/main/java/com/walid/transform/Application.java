package com.walid.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.json.Json;
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

    static {
        String logConfig = Application.class.getClassLoader().getResource("logging.properties").getPath();
        System.setProperty("java.util.logging.config.file", logConfig);
    }

    private static final Logger logger = Logger.getLogger(Application.class.getName());

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
        JsonWriterFactory writerFactory = Json.createWriterFactory(outputConfig);

        logger.info(() -> String.format("Reading input from ./%s and writing output to ./%s", INPUT_FILE, OUTPUT_FILE));

        try (
            InputStream inputStream = new FileInputStream(inputFile);
            JsonReader reader = Json.createReader(inputStream);
            OutputStream outputStream = new FileOutputStream(outputFile);
            JsonWriter writer = writerFactory.createWriter(outputStream)) {
            writer.write(Transformer.transform(reader.readArray()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "File handling error.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Generic processing error.");
        }
    }
}
