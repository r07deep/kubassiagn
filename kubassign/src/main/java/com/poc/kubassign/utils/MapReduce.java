package com.poc.kubassign.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class MapReduce {
	private LinkedHashMap intermediate;
	private JSONObject finalResult = new JSONObject();
	private int resultCount;

	MapReduce() {
		resultCount = 0;
		finalResult = new JSONObject();
		intermediate = new LinkedHashMap();
	}

	JSONObject execute(JSONObject inputdata) {
		JSONArray itemList = (JSONArray) inputdata.get("items");
		for (int i = 0; i < itemList.size(); i++) {
			JSONObject record = (JSONObject) itemList.get(i);
			mapper(record);
		}

		Iterator it = intermediate.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			reducer((String) pair.getKey(), (ArrayList) pair.getValue());
			it.remove();
		}
		return finalResult;

	}

	private void emit(LinkedHashMap obj) {
		finalResult.put(resultCount++, obj);
	}

	private <T> void reducer(T key, ArrayList value) {
		LinkedHashMap obj = new LinkedHashMap();
		obj.put("key", key);
		obj.put("value", value.toString().replace("[", "\"").replace("]", "\"").replace(", ", ","));
		emit(obj);

	}

	private void mapper(JSONObject record) {
		JSONObject spec = (JSONObject) record.get("spec");
		JSONObject metadata = (JSONObject) record.get("metadata");
		String node = (String) spec.get("nodeName");
		String app = (String) metadata.get("name");
		emitIntermediate(node, app); 

	}

	private <T1, T2> void emitIntermediate(T1 key, T2 value) {
		if (!intermediate.containsKey(key))
			intermediate.put(key, new ArrayList());

		ArrayList temp = (ArrayList) intermediate.get(key);
		temp.add(value);
		intermediate.put(key, temp);
	}
		
}
