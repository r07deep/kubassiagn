package com.poc.kubassign.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProcessPodsInfo {
	static Logger log = Logger.getLogger(ProcessPodsInfo.class.getName());
	JSONObject finalOutput = new JSONObject();

	/*
	 * 1. function which calls the api/vi/pods or gets the output form the file
	 * system to fetch the raw data and return the group output to controller.
	 */
	public JSONObject getGroupInfo() throws IOException {
		log.info("--INSIDE UTIL CLASS--");

		JSONParser parser = new JSONParser();
		Object fileObj = null;
		try {
			// Reading the main Output from file structure..
			fileObj = parser.parse(new FileReader("C:\\Users\\nandyr\\Music\\v1\\api\\pods.json"));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}

		// jsonFile retrieved
		JSONObject jsonInputFile = (JSONObject) fileObj;

		MapReduce mapred = new MapReduce();
		// calling execute method of MapReduce Class to get the node and app
		// distribution list
		JSONObject result = mapred.execute(jsonInputFile);
		LinkedHashMap<String, Set<String>> nodeNAppDistribtionList = getNodeAppDistrubutionData(result);
		System.out.println("Intermidaite data-> Node distribution with AppGroups");
		System.out.println(nodeNAppDistribtionList);

		LinkedHashMap<String, ArrayList<Integer>> nodeAppAscii = new LinkedHashMap<>();
		for (Map.Entry<String, Set<String>> temp : nodeNAppDistribtionList.entrySet()) {
			ArrayList<Integer> finalList = new ArrayList<Integer>();
			ArrayList<String> tempList = new ArrayList<String>(
					Arrays.asList(temp.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",")));
			for (int j = 0; j < tempList.size(); j++) {
				String str = tempList.get(j).trim();
				finalList.add((int) str.charAt(0));
				nodeAppAscii.put(temp.getKey(), finalList);
			}

		}

		System.out.println(nodeAppAscii);

		final String outputFilePath = "C:\\Users\\nandyr\\Downloads\\Sample1.txt";
		// new file object
		File file = new File(outputFilePath);
		File clusterNAppFile = null;
		BufferedWriter bf = null;
		int lineCount = 0;
		try {
			// create new BufferedWriter for the output file
			bf = new BufferedWriter(new FileWriter(file));

			// iterate map entries
			for (Map.Entry<String, ArrayList<Integer>> entry : nodeAppAscii.entrySet()) {
				ArrayList<Integer> value = entry.getValue();
				for (int j = 0; j < value.size(); j++) {
					Integer intVal = value.get(j);
					// put key and value separated by a colon
					bf.write((intVal + "," + entry.getKey() + "\n"));
					lineCount++;
				}
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
		}
		// Apply K-Means..
		K_Cluster kCluster = new K_Cluster();
		try {
			clusterNAppFile = kCluster.getCluster(outputFilePath, lineCount, 2);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Now read the appNode File and appClusterFile..

		JSONObject finalOutput = new JSONObject();
		Map<String, Map<String, Set<String>>> appNodeCluster1 = new HashMap<String, Map<String, Set<String>>>();
		Map<String, Map<String, Set<String>>> appNodeCluster2 = new HashMap<String, Map<String, Set<String>>>();
		Set<String> apps1 = new HashSet<>();
		Set<String> apps2 = new HashSet<>();
		Set<String> nodes1 = new HashSet<>();
		Set<String> nodes2 = new HashSet<>();
		Map<String, Set<String>> appsObj1 = new HashMap<String, Set<String>>();
		Map<String, Set<String>> appsObj2 = new HashMap<String, Set<String>>();
		Map<String, Set<String>> nodeObj1 = new HashMap<String, Set<String>>();
		Map<String, Set<String>> nodeObj2 = new HashMap<String, Set<String>>();

		addAppsToRespectiveCluster(clusterNAppFile, appNodeCluster1, appNodeCluster2, apps1, apps2, appsObj1, appsObj2);

		formNodeObjects(nodeNAppDistribtionList, appNodeCluster1, nodes1, nodes2, nodeObj1, nodeObj2);

		addNodesToClusters(appNodeCluster1, appNodeCluster2, nodes1, nodes2);

		System.out.println("--APP and CLUSTER--");
		System.out.println(appNodeCluster1);
		System.out.println(appNodeCluster2);
		finalOutput.putAll(appNodeCluster1);
		finalOutput.putAll(appNodeCluster2);
		return finalOutput;
	}

	/* function which adds apps node1 and app node2 to respective Clusters */
	private void addAppsToRespectiveCluster(File clusterNAppFile, Map<String, Map<String, Set<String>>> appNodeCluster1,
			Map<String, Map<String, Set<String>>> appNodeCluster2, Set<String> apps1, Set<String> apps2,
			Map<String, Set<String>> appsObj1, Map<String, Set<String>> appsObj2) throws IOException {
		String inputFileString = null;
		BufferedReader reader1;
		String cluster1 = "0";
		String cluster2 = "1";
		try {
			reader1 = new BufferedReader(new FileReader(clusterNAppFile));
			inputFileString = reader1.readLine();
			while ((inputFileString != null)) {
				String[] split = inputFileString.trim().split(",");
				if (cluster1.equals(split[1])) {
					apps1.add(split[0]);
					appsObj1.put("apps", apps1);
					appNodeCluster1.put("G" + cluster1, appsObj1);
				} else {
					apps2.add(split[0]);
					appsObj2.put("apps", apps2);
					appNodeCluster2.put("G" + cluster2, appsObj2);
				}
				// read next line
				inputFileString = reader1.readLine();

			}
			reader1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/* function which forms the node1 and node2 Object */
	private void formNodeObjects(LinkedHashMap<String, Set<String>> nodeNAppDistribtionList,
			Map<String, Map<String, Set<String>>> appNCluster1, Set<String> nodes1, Set<String> nodes2,
			Map<String, Set<String>> nodeObj1, Map<String, Set<String>> nodeObj2) {
		for (Map.Entry<String, Set<String>> temp : nodeNAppDistribtionList.entrySet()) {
			String key = temp.getKey();
			String value = temp.getValue().toString();
			for (Map.Entry<String, Map<String, Set<String>>> appGrp : appNCluster1.entrySet()) {
				Map<String, Set<String>> appsMap = appGrp.getValue();
				for (Map.Entry<String, Set<String>> appDataSet : appsMap.entrySet()) {
					if (value.equals(appDataSet.getValue().toString())) {
						nodes1.add(key);
						nodeObj1.put("nodes", nodes1);
					} else {
						nodes2.add(key);
						nodeObj2.put("nodes", nodes2);
					}
				}
			}
		}
	}

	/* function which adds nodes to Cluster */
	private void addNodesToClusters(Map<String, Map<String, Set<String>>> appNCluster1,
			Map<String, Map<String, Set<String>>> appNCluster2, Set<String> nodes1, Set<String> nodes2) {
		for (Map.Entry<String, Map<String, Set<String>>> appGrp1 : appNCluster1.entrySet()) {
			if (appGrp1.getKey().equals("G0")) {
				appNCluster1.get("G0").put("nodes", nodes1);
			}
		}
		for (Map.Entry<String, Map<String, Set<String>>> appGrp1 : appNCluster2.entrySet()) {
			if (appGrp1.getKey().equals("G1")) {
				appNCluster2.get("G1").put("nodes", nodes2);
			}
		}
	}

	/* function which returns the node and app distribution list */
	private LinkedHashMap<String, Set<String>> getNodeAppDistrubutionData(JSONObject result) {
		LinkedHashMap<String, Set<String>> nodeNAppDistribtionList = new LinkedHashMap<>();
		for (int i = 0; i < result.size(); i++) {
			LinkedHashMap record = (LinkedHashMap) result.get(i);
			String key = (String) record.get("key");
			String value = (String) record.get("value");
			value = value.replaceAll("\\\"", "");
			ArrayList<String> myList = new ArrayList<String>(Arrays.asList(value.split(",")));
			Set<String> appArray = new HashSet<>();
			for (int j = 0; j < myList.size(); j++) {
				char[] appNameChar = myList.get(j).toCharArray();
				String appName = Character.toString(appNameChar[0]).trim();
				appArray.add(appName);
			}
			// System.out.println ("{\"nodes\":\""+key+"\",\"app\":"+value+"}");
			nodeNAppDistribtionList.put(key, appArray);
		}
		return nodeNAppDistribtionList;
	}

}
