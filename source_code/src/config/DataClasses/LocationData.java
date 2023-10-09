package config.DataClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import config.Constants;

public class LocationData {
    public Map<String, List<Integer>> coordByLocation;

    public LocationData(){
        coordByLocation = new HashMap<>();
        try {
            // normalizeDataByAddingIdentifiersToEachRow();
            Scanner sc = new Scanner(new File("C:\\4th_Year_RHIT_Classes\\CSSE490\\HW1\\fa23-hw1-genetic-algorithm-fa23_hw1_leek4\\source_code\\src\\config\\LocationData.csv"));
            sc.useDelimiter(",");
            //setting comma as delimiter pattern
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(","); // Split the line by commas
                String name = data[0].trim();
                int x = Integer.parseInt(data[1].trim());
                int y = Integer.parseInt(data[2].trim());
                List<Integer> coords = new ArrayList<>();
                coords.add(x);
                coords.add(y);
                coordByLocation.put(name, coords);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(coordByLocation.toString());
    }

    private void normalizeDataByAddingIdentifiersToEachRow() {
        String csvFileName = "fa23-hw1-genetic-algorithm-fa23_hw1_leek4\\source_code\\src\\config\\LocationData.csv"; // Specify your existing CSV file name

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName, true))) {

            // Append the new column to each row
            int popSize = 100;
            for (int i = 1; i < popSize + 1; i++) {
                String toWrite = ", " + i;
                if (toWrite != null) {
                    writer.write(toWrite); // Separate values with a comma
                }
            }
            writer.newLine(); // Move to the next line
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
