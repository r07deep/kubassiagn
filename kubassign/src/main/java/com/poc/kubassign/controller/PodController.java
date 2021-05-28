package com.poc.kubassign.controller;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.kubassign.utils.ProcessPodsInfo;

@RestController
@RequestMapping()

public class PodController {
	static Logger log = Logger.getLogger(PodController.class.getName());
	//@Autowired
	//private RestTemplate restTemplate;
	
	@GetMapping("/api/node/groups")
	public JSONObject getPodConfig() {
		log.info("--INSIDE THE GET CALL OF CONTROLLER--");
		ProcessPodsInfo pi = new ProcessPodsInfo();
		JSONObject finalOutput = pi.getGroupInfo();
		log.info("--OUTPUT RETURNED TO USER--");
		return finalOutput;

	}

	/*
	 * public Object getRawData() throws UnsupportedEncodingException { //String
	 * encodedURL = URLDecoder.decode("http://localhost:8080/vi/api/pods", "UTF-8");
	 * 
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setAccept(Arrays.asList(MediaType.ALL));
	 * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	 * 
	 * HttpEntity<String> request = new HttpEntity<>(headers);
	 * 
	 * //UriComponents uriComponents =
	 * UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(
	 * 8080) //.path("/vi/api/pods").build();
	 * //System.out.println(uriComponents.toUri()); ResponseEntity<String> response1
	 * = restTemplate.exchange(encodedURL, HttpMethod.GET, request, String.class);
	 * // Object //
	 * fileObj1=restTemplate.getForObject("http://localhost:8080/vi/api/pods",Object
	 * .class); // JSONObject file11 = (JSONObject) fileObj1;
	 * System.out.println("Testing"); // System.out.println(file11);
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

}
