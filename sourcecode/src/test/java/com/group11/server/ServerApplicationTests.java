package com.group11.server;

import com.group11.server.repository.GameRepository;
import com.group11.server.repository.PlayerRepository;
import com.group11.server.service.GameService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@RunWith(SpringRunner.class)
class ServerApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	GameRepository gameRepository;

	@Autowired
	PlayerRepository playerRepository;

	@Autowired
	GameService gameService;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

}
