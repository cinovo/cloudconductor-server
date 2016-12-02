package de.cinovo.cloudconductor.server.web.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import de.cinovo.cloudconductor.server.util.exception.FormErrorException;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class FormValidator {
	
	private MultivaluedMap<String, String> form;
	private Set<String> notNull = new HashSet<>();
	private Set<String> notEmpty = new HashSet<>();
	private Map<String, Object> equals = new HashMap<>();
	private Map<String, Object> notEquals = new HashMap<>();
	
	private FormErrorException error;
	
	
	private FormValidator() {
		// Nothing to do;
	}
	
	/**
	 * @param path the url
	 * @param form the form values
	 * @return the validator
	 */
	public static FormValidator create(String path, MultivaluedMap<String, String> form) {
		FormValidator validator = new FormValidator();
		validator.createError(path);
		validator.form = form;
		return validator;
	}

	/**
	 * @param field the field
	 * @return the validator
	 */
	public FormValidator notNull(String field) {
		this.notNull.add(field);
		return this;
	}
	
	/**
	 * @param field the field
	 * @return the validator
	 */
	public FormValidator notEmpty(String field) {
		this.notEmpty.add(field);
		return this;
	}
	
	/**
	 * @param field the field
	 * @param value the value
	 * @return the validator
	 */
	public FormValidator equals(String field, String value) {
		this.equals.put(field, value);
		return this;
	}
	
	/**
	 * @param field the field
	 * @param value the value
	 * @return the validator
	 */
	public FormValidator notEquals(String field, String value) {
		this.notEquals.put(field, value);
		return this;
	}

	/**
	 * @throws FormErrorException if validation shows an error
	 */
	public void validate() throws FormErrorException {
		boolean errorOccured = false;
		for (Entry<String, List<String>> entry : this.form.entrySet()) {
			boolean valid = true;
			if (this.notEmpty.contains(entry.getKey())) {
				valid = this.isNotEmpty(entry.getValue());
			}
			if (valid) {
				if (this.notNull.contains(entry.getKey())) {
					valid = this.isNotNull(entry.getValue());
				}
			}
			if (valid) {
				if (this.equals.keySet().contains(entry.getKey())) {
					valid = this.isEqual(entry.getValue(), this.equals.get(entry.getKey()));
				}
			}
			
			if (valid) {
				if (this.notEquals.keySet().contains(entry.getKey())) {
					valid = this.isNotEqual(entry.getValue(), this.notEquals.get(entry.getKey()));
				}
			}
			if (!valid && !errorOccured) {
				errorOccured = true;
			}
			this.error.addFormParam(entry.getKey(), entry.getValue().get(0), !valid);
		}
		if (errorOccured) {
			throw this.error;
		}
	}
	
	private boolean isNotEqual(List<String> list, Object object) {
		if ((list == null) || (list.get(0) == null) || list.isEmpty()) {
			if (object == null) {
				return false;
			}
			return true;
		}
		if (!list.get(0).equals(object)) {
			return true;
		}
		return false;
	}
	
	private boolean isEqual(List<String> list, Object object) {
		if ((list == null) || (list.get(0) == null) || list.isEmpty()) {
			if (object == null) {
				return true;
			}
			return false;
		}
		if (!list.get(0).equals(object)) {
			return false;
		}
		return true;
	}
	
	private boolean isNotNull(List<String> list) {
		if (list == null) {
			return false;
		} else if ((list.get(0) == null) || list.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private boolean isNotEmpty(List<String> list) {
		if (list == null) {
			return false;
		} else if ((list.get(0) == null) || list.isEmpty()) {
			return false;
		} else if (list.get(0).isEmpty()) {
			return false;
		}
		return true;
	}
	
	private void createError(String path) {
		this.error = new FormErrorException(path, "Please fill in all the information.");
	}
	
	/**
	 * @param formField the field
	 * @return whether the field exists and has a value
	 */
	public boolean fieldCheck(String formField) {
		if (this.form.get(formField) == null) {
			return false;
		} else if ((this.form.get(formField).get(0) == null) || this.form.get(formField).get(0).isEmpty()) {
			return false;
		}
		return true;
	}
}
