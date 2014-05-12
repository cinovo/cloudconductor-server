package de.cinovo.cloudconductor.server.web2.helper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Component
public class NavbarRegistry {
	
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	
	private TreeSet<NavbarElement> mainMenu = new TreeSet<>();
	private Multimap<NavbarHardLinks, NavbarElement> subMenu = TreeMultimap.create();
	
	
	@PostConstruct
	public void init() {
		List<EAdditionalLinks> links = this.dLinks.findList();
		int counter = 0;
		for (EAdditionalLinks link : links) {
			this.registerSubMenu(NavbarHardLinks.links, link.getLabel(), link.getUrl(), counter++);
		}
	}
	
	public void registerMainMenu(String identifier, String relativePath) {
		this.mainMenu.add(new NavbarElement(identifier, relativePath));
	}
	
	public void registerSubMenu(NavbarHardLinks menu, String identifier, String relativePath) {
		this.subMenu.put(menu, new NavbarElement(identifier, relativePath));
	}
	
	public void unregisterSubMenu(NavbarHardLinks menu, String identifier) {
		this.subMenu.get(menu).remove(new NavbarElement(identifier, null));
	}
	
	public void registerMainMenu(String identifier, String relativePath, int orderNo) {
		this.mainMenu.add(new NavbarElement(identifier, relativePath, orderNo));
	}
	
	public void registerSubMenu(NavbarHardLinks menu, String identifier, String relativePath, int orderNo) {
		this.subMenu.put(menu, new NavbarElement(identifier, relativePath, orderNo));
		
	}
	
	public TreeSet<NavbarElement> getMainMenu() {
		return this.mainMenu;
	}
	
	public Set<NavbarHardLinks> getSubMenuCategories() {
		return this.subMenu.keySet();
	}
	
	public Collection<NavbarElement> getSubMenu(NavbarHardLinks category) {
		return this.subMenu.get(category);
	}
	
	
	public class NavbarElement implements Comparable<NavbarElement> {
		
		private String identifier;
		private String path;
		private int position = -1;
		
		
		public NavbarElement(String identifier, String path) {
			this.identifier = identifier;
			this.path = path;
		}
		
		public NavbarElement(String identifier, String path, int orderNo) {
			this.identifier = identifier;
			this.path = path;
			this.position = orderNo;
		}
		
		public String getIdentifier() {
			return this.identifier;
		}
		
		public String getPath() {
			return this.path;
		}
		
		public int getPosition() {
			return this.position;
		}
		
		@Override
		public int compareTo(NavbarElement other) {
			int result = 0;
			if (this.position > 0) {
				result = Integer.compare(this.position, other.getPosition());
			}
			if (result == 0) {
				return this.identifier.compareTo(other.identifier);
			}
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NavbarElement)) {
				return false;
			}
			if (!this.identifier.equals(((NavbarElement) obj).identifier)) {
				return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return this.identifier.hashCode();
		}
	}
	
}
