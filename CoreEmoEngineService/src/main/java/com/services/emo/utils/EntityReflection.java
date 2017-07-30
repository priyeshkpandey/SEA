package com.services.emo.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Column;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.services.dao.AgentNetworkDAO;
import com.services.dao.AgentTabDAO;
import com.services.dao.EmotionalAttitudesDAO;
import com.services.dao.EventTabDAO;
import com.services.dao.InteractionAttitudesDAO;
import com.services.dao.ObjectTabDAO;
import com.services.dao.ObservedEmotionsDAO;
import com.services.dao.PreviousStateDAO;
import com.services.entities.AgentNetwork;
import com.services.entities.AgentTab;
import com.services.entities.EmotionalAttitudes;
import com.services.entities.EventTab;
import com.services.entities.InteractionAttitudes;
import com.services.entities.ObjectTab;

public class EntityReflection {

	public <T> String getFieldNameByColumnName(String columnName, T entity) {
		if (entity instanceof Serializable) {
			for (Field field : entity.getClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && column.name().equals(columnName)) {
					return field.getName();
				}
			}
		}

		return null;
	}

	public <T> String getColumnNameByFieldName(String fieldName, T entity) {
		if (entity instanceof Serializable) {
			for (Field field : entity.getClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && field.getName().equals(fieldName)) {
					return column.name();
				}
			}
		}

		return null;
	}

	public <T, V> void invokeSetterMethodByColumnName(String columnName, T entity, V value) {
		if (entity instanceof Serializable) {

			try {
				BeanInfo info = Introspector.getBeanInfo(entity.getClass());
				PropertyDescriptor[] props = info.getPropertyDescriptors();

				for (PropertyDescriptor pd : props) {
					if (pd.getName().equals(
							getFieldNameByColumnName(columnName, entity))) {
						
						Method setterMethod = pd.getWriteMethod();
						setterMethod.invoke(entity, value);

					}
				}

			} catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		
	}
	
	
	@SuppressWarnings("unchecked")
	public <T, V> V invokeGetterMethodByColumnName(String columnName, T entity) {
		if (entity instanceof Serializable) {

			try {
				BeanInfo info = Introspector.getBeanInfo(entity.getClass());
				PropertyDescriptor[] props = info.getPropertyDescriptors();

				for (PropertyDescriptor pd : props) {
					if (pd.getName().equals(
							getFieldNameByColumnName(columnName, entity))) {
						
						Method getterMethod = pd.getReadMethod();	
						return (V) getterMethod.invoke(entity);
						

					}
				}

			} catch (IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}
	
	
	public<T, V> void invokeJpaSave(T dao, V entity)
	{
		if(dao instanceof JpaRepository<?, ?>)
		{
			
			try {
				
				if (dao instanceof AgentNetworkDAO) {
					AgentNetworkDAO agentNetworkDAO = (AgentNetworkDAO)dao;
					AgentNetwork network = (AgentNetwork)entity;
					agentNetworkDAO.save(network);
				} else if (dao instanceof AgentTabDAO) {
					AgentTabDAO agentDAO = (AgentTabDAO)dao;
					AgentTab agent = (AgentTab)entity;
					agentDAO.save(agent);
				} else if (dao instanceof EmotionalAttitudesDAO) {
					EmotionalAttitudesDAO emoAttsDAO = (EmotionalAttitudesDAO)dao;
					EmotionalAttitudes emoAtts = (EmotionalAttitudes)entity;
					emoAttsDAO.save(emoAtts);
				} else if (dao instanceof EventTabDAO) {
					EventTabDAO eventDAO = (EventTabDAO)dao;
					EventTab event = (EventTab)entity;
					eventDAO.save(event);
				} else if (dao instanceof InteractionAttitudesDAO) {
					InteractionAttitudesDAO interactAttsDAO = (InteractionAttitudesDAO)dao;
					InteractionAttitudes interactAtts = (InteractionAttitudes)entity;
					interactAttsDAO.save(interactAtts);
				} else if (dao instanceof ObjectTabDAO) {
					ObjectTabDAO objectDAO = (ObjectTabDAO)dao;
					ObjectTab object = (ObjectTab)entity;
					objectDAO.save(object);
				} 
				
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println("Not an instance of JpaRepository.");
		}
	}
	
	public<T, V> void invokeJpaSaveAndFlush(T dao, V entity)
	{
		if(dao instanceof JpaRepository<?, ?>)
		{
			
			try {
				
				if (dao instanceof AgentNetworkDAO) {
					AgentNetworkDAO agentNetworkDAO = (AgentNetworkDAO)dao;
					AgentNetwork network = (AgentNetwork)entity;
					agentNetworkDAO.saveAndFlush(network);
				} else if (dao instanceof AgentTabDAO) {
					AgentTabDAO agentDAO = (AgentTabDAO)dao;
					AgentTab agent = (AgentTab)entity;
					agentDAO.saveAndFlush(agent);
				} else if (dao instanceof EmotionalAttitudesDAO) {
					EmotionalAttitudesDAO emoAttsDAO = (EmotionalAttitudesDAO)dao;
					EmotionalAttitudes emoAtts = (EmotionalAttitudes)entity;
					emoAttsDAO.saveAndFlush(emoAtts);
				} else if (dao instanceof EventTabDAO) {
					EventTabDAO eventDAO = (EventTabDAO)dao;
					EventTab event = (EventTab)entity;
					eventDAO.saveAndFlush(event);
				} else if (dao instanceof InteractionAttitudesDAO) {
					InteractionAttitudesDAO interactAttsDAO = (InteractionAttitudesDAO)dao;
					InteractionAttitudes interactAtts = (InteractionAttitudes)entity;
					interactAttsDAO.saveAndFlush(interactAtts);
				} else if (dao instanceof ObjectTabDAO) {
					ObjectTabDAO objectDAO = (ObjectTabDAO)dao;
					ObjectTab object = (ObjectTab)entity;
					objectDAO.saveAndFlush(object);
				} 
				
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println("Not an instance of JpaRepository.");
		}
	}
	
	
	public <T, V> List<V> getEntityBySpec(T dao, V entity, Specification<V> spec)
	{
		List results = null;
			try {
				if (dao instanceof AgentNetworkDAO) {
					AgentNetworkDAO agentNetworkDAO = (AgentNetworkDAO)dao;
					Specification<AgentNetwork> networkSpec = (Specification<AgentNetwork>)spec;
					results = agentNetworkDAO.findAll(networkSpec);
				} else if (dao instanceof AgentTabDAO) {
					AgentTabDAO agentDAO = (AgentTabDAO)dao;
					Specification<AgentTab> agentSpec = (Specification<AgentTab>)spec;
					results = agentDAO.findAll(agentSpec);
				} else if (dao instanceof EmotionalAttitudesDAO) {
					EmotionalAttitudesDAO emoAttsDAO = (EmotionalAttitudesDAO)dao;
					Specification<EmotionalAttitudes> emoAttsSpec = (Specification<EmotionalAttitudes>)spec;
					results = emoAttsDAO.findAll(emoAttsSpec);
				} else if (dao instanceof EventTabDAO) {
					EventTabDAO eventDAO = (EventTabDAO)dao;
					Specification<EventTab> eventSpec = (Specification<EventTab>)spec;
					results = eventDAO.findAll(eventSpec);
				} else if (dao instanceof InteractionAttitudesDAO) {
					InteractionAttitudesDAO interactAttsDAO = (InteractionAttitudesDAO)dao;
					Specification<InteractionAttitudes> interactAttsSpec = (Specification<InteractionAttitudes>)spec;
					results = interactAttsDAO.findAll(interactAttsSpec);
				} else if (dao instanceof ObjectTabDAO) {
					ObjectTabDAO objectDAO = (ObjectTabDAO)dao;
					Specification<ObjectTab> objectSpec = (Specification<ObjectTab>)spec;
					results = objectDAO.findAll(objectSpec);
				} 
				
			}  catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return results;
		
	}
	

	

}
