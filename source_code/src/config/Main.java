package config;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import config.Constants;
import config.PropParser;
import config.DataClasses.LocationData;


/**
 * Designed to be run with a properties file specified
 * > java -jar Main filename.properties
 * 
 * This is just a toy program setup to show you how to freeze your code and run different experiments
 * once you have finished all the details.
 * 
 * @author Jason Yoder
 *
 */
public class Main {
	
	public static void main(String[] args) {

		// String filename = args[0];    // to avoid needing to change source code at all
		// To use this:
		// Right Click on Main.java->Run As->Run Configurations
		// Select Arguments->Variables->String Prompt(i.e. ${string_prompt}) -> Run
		// Then the program will ask you for the path to the configuration you want to use
		LocationData ld = new LocationData();
		Simulation simulation = new Simulation(ld);
		List<Map<String, Double>> pop = simulation.initializePopulation();
		// System.out.println(pop.toString());
		// while(pop.peek() != null){
		// 	System.out.println(pop.poll().toString());
		// }
		System.out.println(pop.toString());

		int numberOfGenerations = 100000;
		// Truncation || Ranked || 'Other': Roulette Wheel 
		String selectionMethod = "Truncation";
		int elitists = 1;
		int changeAlgorithmAtGen = 800000;
		String csvFileName = "fa23-hw1-genetic-algorithm-fa23_hw1_leek4\\example\\simple_plot_demo\\run.csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName))) {
            // Write the header row (optional)
            writer.write("generation,max,average,min");
		} catch (IOException e) {
            e.printStackTrace();
        }

		for (int i = 0; i < numberOfGenerations; i++) {
			if (i == changeAlgorithmAtGen) {
				simulation.changeComparator();
			}
			pop = simulation.runSimulation(pop, selectionMethod, elitists);
			System.out.println("Best of this gen: " + pop.get(0).toString());
			Double avg = 0.0;
			for(int j = 0; j < pop.size(); j++) {
				avg += pop.get(j).values().iterator().next();
			}

			avg /= Constants.POP_SIZE;
			writeToFile(i, pop.get(0).values().iterator().next(), pop.get(Constants.POP_SIZE - 1).values().iterator().next(), avg);
		}
		// System.out.println("--------------------");
		// System.out.println(pop.toString());
		System.out.println(pop.size());
	}

	private static void writeToFile(int gen, Double best, Double worst, Double avg) {
		String csvFileName = "fa23-hw1-genetic-algorithm-fa23_hw1_leek4\\example\\simple_plot_demo\\run.csv";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName, true))) {
			writer.newLine();
			String nextRow = gen + "," + best + "," + avg + "," + worst;
			writer.write(nextRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
