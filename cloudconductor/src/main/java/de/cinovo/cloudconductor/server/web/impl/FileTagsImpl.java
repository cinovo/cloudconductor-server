package de.cinovo.cloudconductor.server.web.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileTagsDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileTag;
import de.cinovo.cloudconductor.server.model.enums.TagColor;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer.AjaxAnswerType;
import de.cinovo.cloudconductor.server.web.interfaces.IFileTags;
import de.cinovo.cloudconductor.server.web.interfaces.IFiles;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class FileTagsImpl extends AWebPage implements IFileTags {
	
	@Autowired
	protected IFileDAO dFiles;
	@Autowired
	protected IFileTagsDAO dTags;
	
	
	@Override
	protected String getTemplateFolder() {
		return "filetags";
	}
	
	@Override
	protected void init() {
		// nothign to do
	}
	
	@Override
	protected String getNavElementName() {
		return null;
	}
	
	@Override
	@Transactional
	public RenderedView viewFilesTags(Long fileid) {
		RESTAssert.assertNotNull(fileid);
		EFile file = this.dFiles.findById(fileid);
		List<EFileTag> tags = this.dTags.findList();
		final CSViewModel modal = this.createModal("mManageTags");
		modal.addModel("FILE", file);
		modal.addModel("TAGS", tags);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView newTagView(Long fileid) {
		final CSViewModel modal = this.createModal("mNewTags");
		modal.addModel("TAGTYPES", TagColor.values());
		modal.addModel("FILEID", fileid);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView deleteTagView(Long fileid, Long tagid) {
		EFileTag tag = this.dTags.findById(tagid);
		List<EFile> files = this.dFiles.findByTag(tag);
		final CSViewModel modal = this.createModal("mDeleteTag");
		modal.addModel("FILES", files);
		modal.addModel("TAG", tag);
		modal.addModel("FILEID", fileid);
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer saveTaggedFile(Long fileid, Long[] tags) throws FormErrorException {
		EFile file = this.dFiles.findById(fileid);
		file.getTags().clear();
		if ((tags != null) && (tags.length > 0)) {
			List<EFileTag> tag = this.dTags.findByIds(tags);
			file.setTags(tag);
		}
		this.dFiles.save(file);
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT, this.getCurrentFilter());
	}
	
	@Override
	@Transactional
	public AjaxAnswer saveNewTag(Long fileid, String name, String type) throws FormErrorException {
		FormErrorException error = null;
		error = this.assertNotEmpty(name, error, "name");
		error = this.assertNotEmpty(type, error, "type");
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", name);
			error.addFormParam("type", type);
			error.setParentUrl(IFileTags.ROOT);
			throw error;
		}
		
		EFileTag tag = new EFileTag();
		tag.setName(name);
		tag.setColor(TagColor.valueOf(type));
		this.dTags.save(tag);
		
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IFileTags.ROOT + "/" + fileid, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully added");
		return ajaxRedirect;
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteTag(Long fileid, Long tagid) throws FormErrorException {
		EFileTag tag = this.dTags.findById(tagid);
		List<EFile> files = this.dFiles.findByTag(tag);
		for (EFile f : files) {
			f.getTags().remove(tag);
			this.dFiles.save(f);
		}
		this.dTags.delete(tag);
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IFileTags.ROOT + "/" + fileid, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully deleted");
		return ajaxRedirect;
	}
}
