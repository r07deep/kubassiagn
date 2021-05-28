package com.poc.kubassign.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	/*
	 * 1. function which calls the api/vi/pods or gets the output form the file
	 * system to fetch the raw data and return the group output to controller.
	 */
	public JSONObject getGroupInfo() {
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
		//calling execute method of MapReduce Class to get the node and app distribution list
		JSONObject result = mapred.execute(jsonInputFile);
		LinkedHashMap<String, Set<String>> nodeNAppDistribtionList = getNodeAppDistrubutionData(result);
		System.out.println("Intermidaite data-> Node distribution with AppGroups");
		System.out.println(nodeNAppDistribtionList);
		
		
		Set<String> app1 = new HashSet<>();
		Set<String> node1 = new HashSet<>();
		Set<String> app2 = new HashSet<>();
		Set<String> node2 = new HashSet<>();
		Map<String, JSONObject> group1 = new HashMap<String, JSONObject>();
		Map<String, JSONObject> group2 = new HashMap<String, JSONObject>();
		
		JSONObject matchingGrp = new JSONObject();
		JSONObject nonMatchingGrp = new JSONObject();
		JSONObject finalOutput = new JSONObject();
		
		/*using the distribution list creating the groups*/
		for (Map.Entry<String, Set<String>> temp : nodeNAppDistribtionList.entrySet()) {
			String tempKey = temp.getKey();
			String tempVal = temp.getValue().toString();
			for (Map.Entry<String, Set<String>> sec : nodeNAppDistribtionList.entrySet()) {
				String key = sec.getKey();
				String val = sec.getValue().toString();
                //checking for the matching apps across nodes
				if (tempVal.contains(val) && !(tempKey.equals(key))) {
					ArrayList<String> tempList = new ArrayList<String>(
					Arrays.asList(val.replaceAll("\\[", "").replaceAll("\\]", "").split(",")));
					for (int j = 0; j < tempList.size(); j++) {
						app1.add(tempList.get(j).trim());
					}
					node1.add(tempKey);
					node1.add(key);
				}
			}
		}
			matchingGrp.put("nodes", node1);
			matchingGrp.put("app", app1);
			group1.put("G1", matchingGrp);

		// now remove the matching data nodes from nodeNAppDistribtionList
		for (String key : node1) {
			nodeNAppDistribtionList.remove(key);
		}
        //now continue with rest of the non matching nodes
		for (Map.Entry<String, Set<String>> map : nodeNAppDistribtionList.entrySet()) {
			node2.add(map.getKey());
			ArrayList<String> myList = new ArrayList<String>(
			Arrays.asList(map.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",")));
			for (int j = 0; j < myList.size(); j++) {
				app2.add(myList.get(j).trim());
			}
		}
		nonMatchingGrp.put("nodes", node2);
		nonMatchingGrp.put("app", app2);
		group2.put("G2", nonMatchingGrp);
		finalOutput.putAll(group1);
		finalOutput.putAll(group2);
		System.out.println(finalOutput);
		return finalOutput;
}
    /*function which returns the node and app distribution list*/
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