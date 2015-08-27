package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;


import com.services.dao.ResourceModelDAO;
import com.services.entities.ResourceModel;

@RestController
@RequestMapping("/create/models")
public class CreateModelsController extends BaseController {

	@Autowired
	ApplicationContext context;
	
	@RequestMapping(method = RequestMethod.POST)
	public void createModels(@RequestBody List<ResourceModel> modelList) {
		if (loadConfig()) {


			ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);

			for (ResourceModel model : modelList) {
				modelDAO.save(model);
			}

			
		}
	}

}
