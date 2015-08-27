package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import com.services.dao.ObservedEmotionsDAO;
import com.services.entities.ObservedEmotions;

@RestController
@RequestMapping("/get/emotions")
public class GetObservedEmos extends BaseController {

	@Autowired
	ApplicationContext context;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<ObservedEmotions> getObservedEmos(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId) {

		if(loadConfig())
		{

		

		ObservedEmotionsDAO obsEmosDAO = context
				.getBean(ObservedEmotionsDAO.class);

		List<ObservedEmotions> response = obsEmosDAO.getObsEmosByUserSimId(
				userId, simId);
		

		return response;
		}
		
		return null;

	}
}
