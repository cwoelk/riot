/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.command;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.PopupCommand;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class GotoPageCommand extends PopupCommand {

	public static final String STYLE_CLASS = "link";

	private PathCompleter pathCompleter;

	public GotoPageCommand(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
	}

	protected String getUrl(CommandContext context) {
		HttpServletRequest request = context.getRequest();
		Page page = (Page) context.getBean();
		Site site = page.getSite();
		StringBuffer url = site.getAbsoluteUrl(request);
		url.append(pathCompleter.addServletMapping(page.getPath())); 
		return ServletUtils.resolveUrl(url.toString(), request);
	}

	protected String getStyleClass(CommandContext context, String action) {
		return STYLE_CLASS;
	}

}
