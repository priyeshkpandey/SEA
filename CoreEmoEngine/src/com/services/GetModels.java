package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.services.dao.ResourceModelDAO;
import com.services.entities.ResourceModel;

@RestController
@RequestMapping("/get/models")
public class GetModels extends BaseController {

	@Autowired
	ApplicationContext context;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<ResourceModel> getModels(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId) {
		if(loadConfig())
		{	

		

		ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);

		List<ResourceModel> response = modelDAO.getModelsBySimUserId(userId,
				simId);
		
		
		return response;
		}
		return null;
	}

}
