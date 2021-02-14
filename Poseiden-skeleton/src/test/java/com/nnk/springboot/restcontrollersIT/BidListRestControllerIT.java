package com.nnk.springboot.restcontrollersIT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.nnk.springboot.domain.BidList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BidListRestControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Integer bidListId;

	@BeforeEach
	public void setUp() throws JsonProcessingException, Exception {
		BidList bidList = new BidList("account", "type", 2.2);
		String jsonResponse = mockMvc
				.perform(post("/rest/bidList").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bidList)))
				.andReturn().getResponse().getContentAsString();

		bidListId = JsonPath.parse(jsonResponse).read("$.bidListId");
	}

	@Test
	@WithMockUser
	void givenABidList_whenPostBidList_thenReturns200AndBidList() throws Exception {
		BidList bidList = new BidList("account1", "type1", 1.1);
		mockMvc.perform(post("/rest/bidList").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bidList))).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account1"));
	}

	@WithMockUser
	@Test
	public void givenAnId_whenGetBidList_thenReturnOkAndBidList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/rest/bidList?bidListId={id}", bidListId)
				.accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account"));
	}

	@Test
	@WithMockUser
	void givenABidList_whenPutBidList_thenReturns200AndUpdatedBidList() throws Exception {
		BidList bidList = new BidList(bidListId, "account2", "type2", 3.3);
		mockMvc.perform(put("/rest/bidList").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bidList))).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account2"));
	}

	@Test
	@WithMockUser
	void givenABidList_whenDeleteBidList_thenReturns200() throws Exception {
		mockMvc.perform(delete("/rest/bidList?bidListId={id}", bidListId)).andExpect(status().isOk());
	}
}
