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
package org.riotfamily.statistics.commands;

import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.statistics.dao.AbstractCachiusStatisticsDao;
import org.riotfamily.statistics.domain.CachiusCacheRegionStatsItem;

public class ResetCachiusStatisticsCommand extends AbstractCommand {
	
	@Override
	protected String getIcon() {
		return "cancel";
	}

	public CommandResult execute(CommandContext context, Selection selection)
			throws Exception {
		
		AbstractCachiusStatisticsDao dao = (AbstractCachiusStatisticsDao) context.getScreenContext().getDao();
		Set<String> regions;
		
		if (selection.size() == 0) {
			 regions = dao.getCachius().getStatistics().keySet();			
		}
		else {
			regions = Generics.newHashSet();
			for (SelectionItem item : selection) {
				CachiusCacheRegionStatsItem crs = (CachiusCacheRegionStatsItem) item.getObject();
				regions.add(crs.getName());
			}
		}
		
		for (String region : regions) {
			dao.getCachius().getStatistics(region).reset();
		}
		
		return new RefreshListResult();
	}

}
