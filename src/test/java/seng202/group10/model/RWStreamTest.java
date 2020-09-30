package seng202.group10.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RWStreamTest {

    @AfterAll
    public static void tearDown() {
        DatabaseConnection.getInstance().disconnect();
    }

    @Test
    public void testRead() {
        String test_string = "this, is, a, test";
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        ArrayList<String> singleList = new ArrayList<String>(Arrays.asList(test_string.split("[, ]+")));
        list.add(singleList);
        RWStream rwstream = new RWStream("test.csv");
        data = rwstream.read();
        assertEquals(list, data);
    }

    @Test
    public void testWriteSingle() {
        ArrayList<ArrayList<String>> read_info = new ArrayList<ArrayList<String>>();
        String test1 = "This, Is, a, test, of, the, write, single, class";
        ArrayList<String> compareList = new ArrayList<String>(Arrays.asList(test1.split("[, ]+")));
        ArrayList<String> list = new ArrayList<String>();
        list.add(test1);
        RWStream rwstream = new RWStream("test1.csv");
        rwstream.writeSingle(list);
        read_info = rwstream.read();
        assertEquals(compareList, read_info.get(0));
    }

    @Test
    public void testWriteAll() {
        String filePath = "testWriteAll.txt";
        File testFile = new File(filePath);
        try {
            // Setup
            ArrayList<ArrayList<String>> testArray = new ArrayList<>();
            testArray.add(new ArrayList<>(Arrays.asList("1", "2", "3")));
            testArray.add(new ArrayList<>(Arrays.asList("4", "5", "6")));
            String expected = "1, 2, 3\n4, 5, 6\n";

            // Write to file
            RWStream stream = new RWStream("", filePath);
            stream.writeAll(testArray);

            // Check file
            String readString = Files.readString(Path.of(filePath));
            assertEquals(readString, expected);

        } catch (IOException ignore) {
        } finally {
            testFile.delete();
        }




    }

}