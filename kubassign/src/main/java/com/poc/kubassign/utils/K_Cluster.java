package com.poc.kubassign.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class K_Cluster extends ReadDataset {

	public File getCluster(String outputFilePath, int max_iterations, int k) throws NumberFormatException, IOException {
		ReadDataset r1 = new ReadDataset();
		r1.features.clear();
		r1.read(outputFilePath);
		int distance = 1;
		// Hashmap to store centroids with index
		Map<Integer, double[]> centroids = new HashMap<>();
		// calculating initial centroids
		double[] x1 = new double[numberOfFeatures];
		int r = 0;

		for (int i = 0; i < k; i++) {
			x1 = r1.features.get(r++);
			centroids.put(i, x1);

		}
		// Hashmap for finding cluster indexes
		Map<double[], Integer> clusters = new HashMap<>();
		clusters = kmeans(r1.features, distance, centroids, k);

		double db[] = new double[numberOfFeatures];
		// reassigning to new clusters
		for (int i = 0; i < max_iterations; i++) {
			for (int j = 0; j < k; j++) {
				List<double[]> list = new ArrayList<>();
				for (double[] key : clusters.keySet()) {
					if (clusters.get(key) == j) {
						list.add(key);
					}
				}
				db = centroidCalculator(list);
				centroids.put(j, db);

			}
			clusters.clear();
			clusters = kmeans(r1.features, distance, centroids, k);
		}
		// final cluster print
		HashMap<Integer, Integer> clusterApp = new HashMap<>();
		System.out.println("\nFinal Clustering of Data");
		System.out.println("Apps" + " \t" + "Cluster");

		final String groupOutput = "C:\\Users\\nandyr\\Downloads\\AppGroup.txt";
		File file = new File(groupOutput);
		BufferedWriter bf = null;
		int lineCount = 0;
		try {
			// create new BufferedWriter for the output file
			bf = new BufferedWriter(new FileWriter(file));

			for (double[] key : clusters.keySet()) {
				for (int i = 0; i < key.length; i++) {
					int ascii = (int) key[i];
					System.out.print((char) (ascii) + "\t");
					bf.write(((char) (ascii) + "," + clusters.get(key) + "\n"));

				}
				System.out.print(clusters.get(key) + "\n");

			}
			bf.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				// always close the writer
				bf.close();
			} catch (Exception e) {
			}

			// Calculate WCSS
			double wcss = 0;

			for (int i = 0; i < k; i++) {
				double sse = 0;
				for (double[] key : clusters.keySet()) {
					if (clusters.get(key) == i) {
						sse += Math.pow(Distance.eucledianDistance(key, centroids.get(i)), 2);
					}
				}
				wcss += sse;
			}
		}
		return file;
	}

	public K_Cluster() {
	}

	// method to calculate centroids
	public static double[] centroidCalculator(List<double[]> a) {

		int count = 0;
		// double x[] = new double[ReadDataset.numberOfFeatures];
		double sum = 0.0;
		double[] centroids = new double[ReadDataset.numberOfFeatures];
		for (int i = 0; i < ReadDataset.numberOfFeatures; i++) {
			sum = 0.0;
			count = 0;
			for (double[] x : a) {
				count++;
				sum = sum + x[i];
			}
			centroids[i] = sum / count;
		}
		return centroids;

	}

	// method for putting features to clusters and reassignment of clusters.
	public static Map<double[], Integer> kmeans(List<double[]> features, int distance, Map<Integer, double[]> centroids,
			int k) {
		Map<double[], Integer> clusters = new HashMap<>();
		int k1 = 0;
		double dist = 0.0;
		for (double[] x : features) {
			double minimum = 999999.0;
			for (int j = 0; j < k; j++) {
				if (distance == 1) {
					dist = Distance.eucledianDistance(centroids.get(j), x);
				} else if (distance == 2) {
					dist = Distance.manhattanDistance(centroids.get(j), x);
				}
				if (dist < minimum) {
					minimum = dist;
					k1 = j;
				}

			}
			clusters.put(x, k1);
		}

		return clusters;

	}

}