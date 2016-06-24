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
				
				Method saveMethod = dao.getClass().getMethod("save", entity.getClass());
				saveMethod.invoke(dao, entity);
				
				
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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
				
				Method saveMethod = dao.getClass().getMethod("saveAndFlush", entity.getClass());
				saveMethod.invoke(dao, entity);
				
				
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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
		else
		{
			System.out.println("Not an instance of JpaRepository.");
		}
	}
	
	
	public <T, V> List<V> getEntityBySpec(T dao, V entity, Specification<V> spec)
	{
		List<V> results = null;
			try {
				Method getMethodBySpec = dao.getClass().getMethod("findAll", spec.getClass());
				results =  (List<V>) getMethodBySpec.invoke(dao, spec);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
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
			
			return results;
		
	}
	

}
