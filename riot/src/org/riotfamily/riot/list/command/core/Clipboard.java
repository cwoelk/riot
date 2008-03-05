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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.riotfamily.riot.dao.CopyAndPasteEnabledDao;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.BatchResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.WebUtils;

public class Clipboard {

	public static final int MODE_EMPTY = 0;

	public static final int MODE_COPY = 1;

	public static final int MODE_CUT = 2;

	private static final String SESSION_ATTRIBUTE = Clipboard.class.getName();

	private int mode;

	private LinkedHashSet items = new LinkedHashSet();
	

	public static Clipboard get(CommandContext context) {
		return get(context.getRequest().getSession());
	}

	public static Clipboard get(HttpSession session) {
		return (Clipboard) WebUtils.getOrCreateSessionAttribute(
				session, SESSION_ATTRIBUTE, Clipboard.class);
	}

	public void cut(CommandContext context) {
		put(context, MODE_CUT);
	}

	public void copy(CommandContext context) {
		put(context, MODE_COPY);
	}

	private void put(CommandContext context, int mode) {
		if (context.getBatchIndex() == 0) {
			clear();
		}
		this.mode = mode;
		items.add(new ClipboardItem(context));
	}

	public void clear() {
		mode = MODE_EMPTY;
		items.clear();
	}

	public boolean isEmpty() {
		return mode == MODE_EMPTY;
	}

	public List getObjects() {
		if (isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List result = new ArrayList(items.size());
		Iterator it = items.iterator();
		while (it.hasNext()) {
			ClipboardItem item = (ClipboardItem) it.next();
			result.add(EditorDefinitionUtils.loadBean(item.listDefinition, item.objectId));
		}
		return result;
	}
	
	public boolean canCopy(CommandContext context) {
		return context.getDao() instanceof CopyAndPasteEnabledDao;
	}

	public boolean canCut(CommandContext context) {
		return context.getDao() instanceof CutAndPasteEnabledDao
				&& !isCut(context);
	}

	public boolean canPaste(CommandContext context) {
		return !isEmpty()
				&& isSupportedDao(context)
				&& isCompatibleEntityClass(context)
				&& (mode == MODE_COPY || !isSameParent(context))
				&& !isCutObjectAncestor(context);
	}

	public CommandResult paste(CommandContext context) {
		if (mode == MODE_CUT) {
			return pasteCut(context);
		}
		else if (mode == MODE_COPY) {
			return pasteCopied(context);
		}
		return null;
	}

	private CommandResult pasteCut(CommandContext context) {
		BatchResult result = new BatchResult();
		CutAndPasteEnabledDao dao = (CutAndPasteEnabledDao) context.getDao();
		Object parent = context.getParent();
		Iterator it = items.iterator();
		while (it.hasNext()) {
			ClipboardItem item = (ClipboardItem) it.next();
			Object bean = dao.load(item.objectId);
			dao.addChild(bean, parent);
			result.add(new RefreshSiblingsResult(item.objectId));
			
			if (item.parentId != null) {
				Object previousParent = EditorDefinitionUtils.loadParent(
						item.listDefinition, item.parentId);

				CutAndPasteEnabledDao previousDao = (CutAndPasteEnabledDao)
						item.listDefinition.getListConfig().getDao();

				previousDao.removeChild(item, previousParent);
				result.add(new RefreshSiblingsResult(item.parentId));	
			}
			else {
				result.add(new RefreshSiblingsResult());
			}
		}
		clear();
		return result;
	}

	private CommandResult pasteCopied(CommandContext context) {
		Object parent = context.getParent();
		CopyAndPasteEnabledDao dao = (CopyAndPasteEnabledDao) context.getDao();
		Iterator it = items.iterator();
		while (it.hasNext()) {
			ClipboardItem item = (ClipboardItem) it.next();
			Object bean = dao.load(item.objectId);
			dao.addCopy(bean, parent);
		}
		clear();
		return new RefreshSiblingsResult(context);
	}


	public boolean isCut(CommandContext context) {
		return mode == MODE_CUT && isInClipboard(context);
	}

	public boolean isCopied(CommandContext context) {
		return mode == MODE_COPY && isInClipboard(context);
	}
	
	private boolean isInClipboard(CommandContext context) {
		return items.contains(new ClipboardItem(context));
	}

	private boolean isSameParent(CommandContext context) {
		Iterator it = items.iterator();
		while (it.hasNext()) {
			ClipboardItem item = (ClipboardItem) it.next();
			if (ObjectUtils.nullSafeEquals(context.getParentId(), item.parentId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCutObjectAncestor(CommandContext context) {
		if (mode == MODE_CUT) {
			EditorReference ref = context.getListDefinition().createEditorPath(
					null, context.getParentId(), context.getMessageResolver());

			Iterator it = items.iterator();
			while (it.hasNext()) {
				ClipboardItem item = (ClipboardItem) it.next();
				EditorReference itemRef = ref;
				//FIXME Break if itemRef belongs to another list
				while (itemRef != null) {
					if (item.objectId.equals(itemRef.getObjectId())) {
						return true;
					}
					itemRef = itemRef.getParent();
				}
			}
		}
		return false;
	}

	private boolean isSupportedDao(CommandContext context) {
		RiotDao dao = context.getDao();
		return (mode == MODE_CUT && dao instanceof CutAndPasteEnabledDao)
				|| (mode == MODE_COPY && dao instanceof CopyAndPasteEnabledDao);
	}

	private boolean isCompatibleEntityClass(CommandContext context) {
		Class daoEntityClass = context.getDao().getEntityClass();
		Iterator it = items.iterator();
		while (it.hasNext()) {
			ClipboardItem item = (ClipboardItem) it.next();
			RiotDao sourceDao = item.listDefinition.getListConfig().getDao();
			if (!daoEntityClass.equals(sourceDao.getEntityClass())) {
				return false;
			}
		}
		return true;
	}
	
	private static class ClipboardItem {

		private String objectId;
		
		private String parentId;
		
		private ListDefinition listDefinition;

		public ClipboardItem(CommandContext context) {
			this (context.getObjectId(), context.getParentId(), context.getListDefinition());
		}
		
		public ClipboardItem(String objectId, String parentId,
				ListDefinition listDefinition) {
			
			this.objectId = objectId;
			this.parentId = parentId;
			this.listDefinition = listDefinition;
		}
		
		public int hashCode() {
			int hashCode = 0;
			if (objectId != null) {
				hashCode += objectId.hashCode();
			}
			if (listDefinition != null) {
				hashCode += listDefinition.hashCode();
			}
			return hashCode;
		}
		
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof ClipboardItem) {
				ClipboardItem other = (ClipboardItem) obj;
				return ObjectUtils.nullSafeEquals(objectId, other.objectId)
						&& ObjectUtils.nullSafeEquals(parentId, other.parentId)
						&& ObjectUtils.nullSafeEquals(listDefinition, other.listDefinition);
			}
			return false;
		}
		
	}

}
