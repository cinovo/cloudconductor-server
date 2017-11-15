package de.cinovo.cloudconductor.server.rest.utils;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
public class PaginationUtils {
	
	private PaginationUtils() {
		// prevent instances
	}
	
	/**
	 * @param path the path of the paginated resource
	 * @param currentPage the current page
	 * @param pageSize number of elements on one page
	 * @param count the total count of elements
	 * @return the link header
	 */
	public static String buildLinkHeader(String path, long currentPage, long pageSize, long count) {
		long nextPage = currentPage + 1;
		long lastPage = (count / pageSize) + 1;
		StringBuilder linkHeader = new StringBuilder();
		linkHeader.append("<" + path + "?page=" + nextPage + "&per_page=" + pageSize + ">; rel=\"next\", ");
		linkHeader.append("<" + path + "?page=" + lastPage + "&per_page=" + pageSize + ">; rel=\"last\", ");
		linkHeader.append("<" + path + "?page=1&per_page=" + pageSize + ">; rel=\"first\", ");
		if (currentPage > 1) {
			long prevPage = currentPage - 1;
			linkHeader.append("<" + path + "?page=" + prevPage + "&per_page=" + pageSize + ">; rel=\"prev\"");
		}
		
		return linkHeader.toString();
	}
	
}
