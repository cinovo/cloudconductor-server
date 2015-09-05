package de.cinovo.cloudconductor.server.web.helper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	
	
	/**
	 * Initialization for spring
	 */
	// @PostConstruct
	public void init() {
		List<EAdditionalLinks> links = this.dLinks.findList();
		int counter = 0;
		for (EAdditionalLinks link : links) {
			this.registerSubMenu(NavbarHardLinks.links, link.getLabel(), link.getUrl(), counter++);
		}
		this.subMenu.put(NavbarHardLinks.options, new NavbarElement(null, null));
	}
	
	/**
	 * @param identifier the identifier
	 * @param relativePath the relative path to link to
	 */
	public void registerMainMenu(String identifier, String relativePath) {
		this.mainMenu.add(new NavbarElement(identifier, relativePath));
	}
	
	/**
	 * @param menu an existing menu item
	 * @param identifier the identifier
	 * @param relativePath the relative path to link to
	 */
	public void registerSubMenu(NavbarHardLinks menu, String identifier, String relativePath) {
		this.subMenu.put(menu, new NavbarElement(identifier, relativePath));
	}
	
	/**
	 * @param menu an existing menu item
	 * @param identifier the identifier
	 */
	public void unregisterSubMenu(NavbarHardLinks menu, String identifier) {
		this.subMenu.get(menu).remove(new NavbarElement(identifier, null));
	}
	
	/**
	 * @param identifier the identifier
	 * @param relativePath the relative path to link to
	 * @param orderNo the position within the list
	 */
	public void registerMainMenu(String identifier, String relativePath, int orderNo) {
		this.mainMenu.add(new NavbarElement(identifier, relativePath, orderNo));
	}
	
	/**
	 * @param menu an existing menu item
	 * @param identifier the identifier
	 * @param relativePath the relative path to link to
	 * @param orderNo the position within the list
	 */
	public void registerSubMenu(NavbarHardLinks menu, String identifier, String relativePath, int orderNo) {
		this.subMenu.put(menu, new NavbarElement(identifier, relativePath, orderNo));
		
	}
	
	/**
	 * @return the main menu subset
	 */
	public TreeSet<NavbarElement> getMainMenu() {
		return this.mainMenu;
	}
	
	/**
	 * @return the sub-menu categories
	 */
	public Set<NavbarHardLinks> getSubMenuCategories() {
		return this.subMenu.keySet();
	}
	
	/**
	 * @param category the category
	 * @return sub-menu for a given hard-link
	 */
	public Collection<NavbarElement> getSubMenu(NavbarHardLinks category) {
		return this.subMenu.get(category);
	}
	
}
