package de.cinovo.cloudconductor.server.web.interfaces;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.RenderedView;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Path(IWebPath.DEFAULTVIEW)
public interface IInstall {
	
	/**
	 * @return the main page
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView view();
	
	/**
	 * @param form the form data
	 * @return a response
	 * @throws FormErrorException on error
	 */
	@POST
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response save(MultivaluedMap<String, String> form) throws FormErrorException;

	/**
	 * @return the progress view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW + "finish/")
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView progressView();
	
	/**
	 * Returns the css style used in the web pages.
	 *
	 * @param css the file name
	 * @return the css style used in the web pages
	 */
	@GET
	@Path("css/{css}")
	@Produces(MediaType.TEXT_CSS)
	public abstract InputStream getCSS(@PathParam("css") String css);

	/**
	 * @param css the file name
	 * @return the bootstrap css style
	 */
	@GET
	@Path("bootstrap/css/{css}")
	@Produces(MediaType.TEXT_CSS)
	public abstract InputStream getBSCSS(@PathParam("css") String css);

	/**
	 * Returns the CSS style used in the web pages.
	 *
	 * @param img the img name
	 * @return the CSS style used in the web pages
	 */
	@GET
	@Path("images/{img}")
	@Produces(MediaType.IMAGE_GIF)
	public abstract InputStream getImage(@PathParam("img") String img);

	/**
	 * Returns the java script files
	 *
	 * @param js the file name
	 * @return the java script files
	 */
	@GET
	@Path("js/{js}")
	@Produces(MediaType.APPLICATION_JAVASCRIPT)
	public abstract InputStream getJS(@PathParam("js") String js);

	/**
	 * @param js the file name
	 * @return the bootstrap java script files
	 */
	@GET
	@Path("bootstrap/js/{js}")
	@Produces(MediaType.APPLICATION_JAVASCRIPT)
	public abstract InputStream getBSJS(@PathParam("js") String js);

	/**
	 * @param font the file name
	 * @return the bootstrap font files
	 */
	@GET
	@Path("bootstrap/fonts/{font}")
	@Produces(MediaType.FONT_OPENTYPE)
	public abstract InputStream getBSFonts(@PathParam("font") String font);
}
