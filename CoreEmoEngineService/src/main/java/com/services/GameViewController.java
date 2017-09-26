package com.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.services.dao.SimulationDAO;
import com.services.entities.Simulation;

@Controller
@RequestMapping("/game")
public class GameViewController {
	
	private static final String HOME_PAGE = "EqGameMain";
	
	@Autowired
	ApplicationContext context;
	
	@RequestMapping(value = "/view/home", method = RequestMethod.GET)
	public String openHomePage(@RequestParam(value = "userId") String userId, ModelMap model) {
        SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		List<Simulation> response = simDAO.getSimulationsByUser(userId);
		List<String> games = new ArrayList<String>();
		for (Simulation sim : response) {
			games.add(sim.getSimName());
		}
		model.addAttribute("games", games);
		return HOME_PAGE;
	}

}
