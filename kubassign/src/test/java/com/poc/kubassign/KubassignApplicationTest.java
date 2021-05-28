
package com.poc.kubassign;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.poc.kubassign.controller.PodController;
import com.poc.kubassign.utils.ProcessPodsInfo;

@RunWith(SpringRunner.class)

@WebMvcTest(value = PodController.class)
public class KubassignApplicationTest {
	static Logger log = Logger.getLogger(KubassignApplicationTest.class.getName());

	@Autowired
	private MockMvc mockMvc;
	ProcessPodsInfo podInfo = new ProcessPodsInfo();
	RequestBuilder requestBuilder = null;
	MvcResult result = null;
	String response = null;
	Object fileObj = null;
	JSONParser parser = new JSONParser();

	@Before
	public void before() throws Exception {
		System.out.println("Setting it up!");
		requestBuilder = MockMvcRequestBuilders.get("/api/node/groups").accept(MediaType.APPLICATION_JSON);
		result = mockMvc.perform(requestBuilder).andReturn();
		response = result.getResponse().getContentAsString();
	}

	@Test
	public void retrieveGrpDetailsTest() throws Exception {
		String expected = "{\"G1\":{\"app\":[a, b, c],\"nodes\":[n1, n5]},\"G2\":{\"app\":[d, e, f, g],\"nodes\":[n2, n3, n4, n6]}}";
		JSONAssert.assertEquals(expected, response, true);
	}

	@Test
	public void retrieveGrpDetailsWithWrongExpectedDataTest() throws Exception {
		String faultyExpectedOutput = "{\"G1\":{\"app\":[a,b,c],\"nodes\":[n1, n5]},\"G2\":{\"app\":[d, e, f, g]}}";
		JSONAssert.assertEquals(faultyExpectedOutput, response, false);

	}


}
