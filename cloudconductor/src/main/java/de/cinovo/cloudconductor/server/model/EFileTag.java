package de.cinovo.cloudconductor.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import de.cinovo.cloudconductor.api.model.INamed;
import de.cinovo.cloudconductor.server.model.enums.TagColor;
import de.taimos.dao.IEntity;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "filetag", schema = "cloudconductor")
public class EFileTag implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private TagColor color;
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the color
	 */
	public TagColor getColor() {
		return this.color;
	}
	
	/**
	 * @param color the color to set
	 */
	public void setColor(TagColor color) {
		this.color = color;
	}
	
}
