package com.poc.kubassign.utils;

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

		K_Cluster kCluster = new K_Cluster();
		try {
			clusterNAppFile = kCluster.getCluster(outputFilePath, lineCount, 2);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Now read the appNode File and appClusterFile..
		/*   
		Map<String,Set<String>> finalNodes1=new HashMap<String, Set<String>>();
		Map<String,Set<String>> finalApps1=new HashMap<String, Set<String>>();
		Set<String> finalNode1=new HashSet<String>();
		Set<String> appNode1=new HashSet<String>();
		
		Map<String, JSONObject> group1 = new HashMap<String, JSONObject>();
		Map<String, JSONObject> group2 = new HashMap<String, JSONObject>();
		JSONObject grpObject1 = new JSONObject();
		JSONObject grpObject2 = new JSONObject();
		JSONObject finalOutput = new JSONObject();
		
		Map<String,Set<String>> finalNodes2=new HashMap<String, Set<String>>();
		Map<String,Set<String>> finalApps2=new HashMap<String, Set<String>>();
		Set<String> finalNode2=new HashSet<String>();
		Set<String> appNode2=new HashSet<String>();
		
		BufferedReader reader1;
		BufferedReader reader2;
		ArrayList<String> rows = new ArrayList<String>();
		try {
			reader1 = new BufferedReader(new FileReader(file));
			reader2 = new BufferedReader(new FileReader(clusterNAppFile));

			String inputFileString = reader1.readLine();
			String outputFileString = reader2.readLine();
			while ((inputFileString != null) && (outputFileString != null)) {
				ArrayList<String> ipTempList = new ArrayList<String>(Arrays.asList(inputFileString.split(",")));
				ArrayList<String> opTempList = new ArrayList<String>(Arrays.asList(outputFileString.split(",")));
				
				
				for (int j = 0; j <ipTempList.size();j++) {
					String appStr=null;
					String string = ipTempList.get(j);
					System.out.println(string);
					if(string.matches("[0-9]+")) {
						char appChar = (char)Integer.parseInt(string);
						appStr=Character.toString(appChar);
					}
					String appOPStr = opTempList.get(j);
					if(appStr!=null && !(appOPStr.matches("[0-9]+"))) {
					if(appStr.equals(appOPStr)) {
						appNode1.add(appStr);
						finalNode1.add(ipTempList.get(j+1));
						grpObject1.put("nodes",finalNode1);
						grpObject1.put("app",appNode1);
						group1.put("G"+opTempList.get(j+1), grpObject1);
					}
					else {
						appNode2.add(appStr);
						finalNode2.add(ipTempList.get(j+1));
						grpObject2.put("nodes",finalNode2);
						grpObject2.put("app",appNode2);
						group2.put("G"+opTempList.get(j+1), grpObject1);
					}
					}
				}
				
			}

			try {
				reader1.close();
				reader2.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finalOutput.putAll(group1);
		finalOutput.putAll(group2);
		System.out.println(finalOutput);*/

		return finalOutput;
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