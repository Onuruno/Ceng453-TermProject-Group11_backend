package com.group11.server;

import com.group11.server.model.Game;
import com.group11.server.model.Player;
import com.group11.server.repository.GameRepository;
import com.group11.server.repository.PlayerRepository;
import com.group11.server.service.GameService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.awt.print.Pageable;
import java.util.List;

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


	@Test
	public void addRecordSuccessfulTest() throws Exception {
		List<Game> beforeList = gameRepository.findAll();
		gameService.addGame(1L, 1); // For test reasons, add this one to the database manually.
		List<Game> afterList = gameRepository.findAll();
		assertEquals(beforeList.size(), afterList.size() - 1);
		Game game = gameRepository.findGameByUsernameAndScore(1 , playerRepository.getById(1L).getUsername());
		gameRepository.delete(game);
	}

	@Test
	public void addRecordNegativeScoreTest() throws Exception {
		mockMvc.perform( post("/game")
						.param("playerID", "1")
						.param("score", "-1"))
						.andExpect(MockMvcResultMatchers.status().isBadRequest())
						.andExpect(MockMvcResultMatchers.content().string("Score cannot be negative"));

	}

	@Test
	public void addRecordInvalidUser() throws Exception {
		mockMvc.perform( post("/game")
				.param("playerID", "-1")
				.param("score", "1"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("Player is not found."));
	}

	@Test
	public void getWeeklyRecordsTest() throws Exception {
		mockMvc.perform( post("/leaderboard_weekly")
						.param("pageLimit", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void getMonthlyRecordsTest() throws Exception {
		mockMvc.perform( post("/leaderboard_monthly")
						.param("pageLimit", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void playerSuccessfulLoginTest() throws Exception {
		mockMvc.perform( post("/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"admin\"}") )
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void playerFailedLoginWrongUserNameTest() throws Exception {
		mockMvc.perform( post("/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"hehehe\",\"password\":\"admin\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Incorrect username or password"));
	}

	@Test
	public void playerFailedLoginWrongPasswordTest() throws Exception {
		mockMvc.perform( post("/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"hehehe\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Incorrect username or password"));
	}

	@Test
	public void playerFailedLoginEmptyUsernameTest() throws Exception {
		mockMvc.perform( post("/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"\",\"password\":\"admin\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Username cannot be empty."));
	}

	@Test
	public void playerFailedLoginEmptyPasswordTest() throws Exception {
		mockMvc.perform( post("/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Password cannot be empty."));
	}

	@Test
	public void getPlayerIdSuccessful() throws Exception {
		mockMvc.perform( post("/getPlayerID")
						.param("username", "admin"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void getPlayerIdFailed() throws Exception {
		mockMvc.perform( post("/getPlayerID")
						.param("username", "notadmin"))
				.andExpect(MockMvcResultMatchers.content().string("Player is not found."));
	}

	@Test
	public void getAllPlayers() throws Exception {
		mockMvc.perform( post("/players")
				)
				.andExpect(MockMvcResultMatchers.status().isOk());
	}


	@Test
	public void getPlayerSuccessful() throws Exception {
		mockMvc.perform( post("/player")
						.param("id", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void getPlayerFailed() throws Exception {
		mockMvc.perform( post("/player")
						.param("id", "-1"))
				.andExpect(MockMvcResultMatchers.content().string("Player is not found"));
	}
//Register
@Test
public void playerSuccessfulRegisterTest() throws Exception {
	mockMvc.perform( post("/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"admin\",\"password\":\"admin\"" +
							",\"email\":\"admin@email.com\"}") )
			.andExpect(MockMvcResultMatchers.status().isOk());
}

	@Test
	public void playerFailedRegisterWrongUserNameTest() throws Exception {
		mockMvc.perform( post("/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"admin\"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Incorrect username or password"));
	}

	@Test
	public void playerFailedRegisterAlreadyExistTest() throws Exception {
		mockMvc.perform( post("/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"admin\"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Username is already taken."));
	}

	@Test
	public void playerFailedRegisterEmptyUsernameTest() throws Exception {
		mockMvc.perform( post("/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"\",\"password\":\"admin\"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Username cannot be empty."));
	}

	@Test
	public void playerFailedRegisterEmptyPasswordTest() throws Exception {
		mockMvc.perform( post("/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"\"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Password cannot be empty."));
	}

	@Test
	public void updatePlayerSuccessfulTest() throws Exception {
		mockMvc.perform( put("/player")
						.param("id", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\" newadmin \"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void updatePlayerFailedTest() throws Exception {
		mockMvc.perform( put("/player")
						.param("id", "-1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\" newadmin \"" +
								",\"email\":\"admin@email.com\"}") )
				.andExpect(MockMvcResultMatchers.content().string("Player is not found, please check the input fields and id."));
	}

}
