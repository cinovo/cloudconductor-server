package de.cinovo.cloudconductor.server.util;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class GenericModelApiConverter {
	private static final Logger logger = LoggerFactory.getLogger(GenericModelApiConverter.class);

	private GenericModelApiConverter() {
		//pervent initialization
	}

	/**
	 * @param origin           the class origin
	 * @param destinationClass the destination class
	 * @param <Destination>    the destination
	 * @param <Origin>         the origin
	 * @return the instanciated destination
	 */
	public static <Destination, Origin> Destination convert(Origin origin, Class<Destination> destinationClass) {
		Destination result = GenericModelApiConverter.createNewInstance(destinationClass);
		return GenericModelApiConverter.copy(origin, result);
	}

	private static <Destination, Origin> Destination copy(Origin origin, Destination result) {
		HashMap<Field, Field> map = GenericModelApiConverter.resolveFieldMapFromOrigin(origin, result);
		for(Entry<Field, Field> entry : map.entrySet()) {
			entry.getKey().setAccessible(true);
			if(entry.getValue() != null) {
				GenericModelApiConverter.copyValue(origin, result, entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private static <Destination, Origin> void copyValue(Origin origin, Destination result, Field originField, Field destinationField) {
		try {
			Object value = GenericModelApiConverter.extractValue(origin, originField);
			Object correctValue = GenericModelApiConverter.getCorrectValue(value, originField, destinationField);
			destinationField.set(result, correctValue);
		} catch(IllegalAccessException | IllegalArgumentException e) {
			GenericModelApiConverter.logger.error("Failed to copy value", e);
		}

	}

	private static Object getCorrectValue(Object originalValue, Field originField, Field destinationField) {
		try {
			if(originalValue == null) {
				return null;
			}

			Class<?> destinationFieldClass = destinationField.getType();

			if(originalValue instanceof Collection && Collection.class.isAssignableFrom(destinationFieldClass)) {
				Collection<Object> newValue;
				if(List.class.isAssignableFrom(destinationFieldClass)) {
					newValue = new ArrayList<>();
				} else {
					newValue = new HashSet<>();
				}
				for(Object element : (Collection) originalValue) {
					newValue.add(getCorrectValue(element, originField, destinationField));
				}
				return newValue;
			}

			if(originalValue instanceof Map && Map.class.isAssignableFrom(destinationFieldClass)) {
				HashMap<Object, Object> newValue = new HashMap<>();
				for(Map.Entry<Object, Object> entry : ((Map<Object, Object>) originalValue).entrySet()) {
					newValue.put(entry.getKey(), getCorrectValue(entry.getValue(), originField, destinationField));
				}
				return newValue;
			}

			if(originalValue instanceof BigDecimal && String.class.isAssignableFrom(destinationFieldClass)) {
				return String.valueOf(originalValue);
			}
			if(originalValue instanceof String && BigDecimal.class.isAssignableFrom(destinationFieldClass)) {
				return new BigDecimal((String) originalValue);
			}

			if(originalValue instanceof Date && DateTime.class.isAssignableFrom(destinationFieldClass)) {
				return new DateTime(originalValue);
			}
			if(originalValue instanceof DateTime && Date.class.isAssignableFrom(destinationFieldClass)) {
				return ((DateTime) originalValue).toDate();
			}

			if(String.class.isAssignableFrom(destinationFieldClass) && originalValue instanceof INamed) {
				return ((INamed) originalValue).getName();
			}

			if(destinationFieldClass.isAssignableFrom(originField.getType())) {
				return originalValue;
			}
		} catch(SecurityException e) {
			GenericModelApiConverter.logger.error("Failed to copy value", e);
		}
		return null;
	}

	private static Object extractValue(Object object, Field field) throws IllegalAccessException {
		try {
			Method e = GenericModelApiConverter.getGetter(object.getClass(), field.getName());
			if(e != null) {
				e.setAccessible(true);
				return e.invoke(object);
			}
		} catch(InvocationTargetException | NoSuchMethodException e) {
			field.setAccessible(true);
			return field.get(object);
		}
		return null;
	}


	private static Method getGetter(Class<?> clazz, String fieldname) throws NoSuchMethodException {
		try {
			return clazz.getMethod("get" + GenericModelApiConverter.capitalize(fieldname));
		} catch(SecurityException e) {
			GenericModelApiConverter.logger.error("Failed to create getter", e);
			return null;
		}
	}

	private static String capitalize(String name) {
		return name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1) : name;
	}

	private static <Destination, Origin> HashMap<Field, Field> resolveFieldMapFromOrigin(Origin origin, Destination destination) {
		HashMap<String, Field> originFields = GenericModelApiConverter.createFieldMap(origin);
		HashMap<String, Field> destinationFields = GenericModelApiConverter.createFieldMap(destination);

		HashMap<Field, Field> result = new HashMap<>();

		for(Field field : originFields.values()) {
			if(destinationFields.containsKey(field.getName())) {
				result.put(field, destinationFields.get(field.getName()));
			}
		}

		return result;
	}

	private static <Destination> Destination createNewInstance(Class<Destination> destinationClass) {
		Destination result = null;
		try {
			result = destinationClass.newInstance();
		} catch(IllegalAccessException | InstantiationException e) {
			GenericModelApiConverter.logger.error("Failed to create new instance", e);
		}
		return result;
	}

	private static HashMap<String, Field> createFieldMap(Object element) {
		HashMap<String, Field> result = new HashMap<>();
		for(Class obj = element.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
			Field[] fields = obj.getDeclaredFields();
			for(Field field : fields) {
				if(!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					result.put(field.getName(), field);
				}
			}
		}
		return result;
	}
}
