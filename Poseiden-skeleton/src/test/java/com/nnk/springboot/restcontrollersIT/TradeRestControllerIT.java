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
import com.nnk.springboot.domain.Trade;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TradeRestControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Integer tradeId;

	@BeforeEach
	public void setUp() throws JsonProcessingException, Exception {
		Trade trade = new Trade("account", "type");
		String jsonResponse = mockMvc
				.perform(post("/rest/trade").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(trade)))
				.andReturn().getResponse().getContentAsString();

		tradeId = JsonPath.parse(jsonResponse).read("$.tradeId");
	}

	@Test
	@WithMockUser
	void givenATrade_whenPostTrade_thenReturns200AndTrade() throws Exception {
		Trade trade = new Trade("account1", "type1");
		mockMvc.perform(post("/rest/trade").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(trade))).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account1"));
	}

	@WithMockUser
	@Test
	public void givenAnId_whenGetTrade_thenReturnOk() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/rest/trade?tradeId={id}", tradeId).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account"));
	}

	@Test
	@WithMockUser
	void givenATrade_whenPutTrade_thenReturns200AndUpdatedTrade() throws Exception {
		Trade trade = new Trade(tradeId, "account", "type");
		mockMvc.perform(put("/rest/trade").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(trade))).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.account").value("account"));
	}

	@Test
	@WithMockUser
	void givenATrade_whenDeleteTrade_thenReturns200() throws Exception {
		mockMvc.perform(delete("/rest/trade?tradeId={id}", tradeId)).andExpect(status().isOk());
	}
}
