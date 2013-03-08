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
package org.riotfamily.statistics.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusStatistics;
import org.riotfamily.statistics.domain.Statistics;
import org.riotfamily.statistics.domain.StatsItem;
import org.springframework.util.StringUtils;

public class CachiusStatisticsDao extends AbstractCachiusStatisticsDao {

	public CachiusStatisticsDao(CacheService cachius) {
		super(cachius);
	}

	@Override
	protected List<? extends StatsItem> getStats() throws Exception {
		Statistics stats = new Statistics();
		populateStats(stats);
		return stats.getItems();
	}

	protected void populateStats(Statistics stats) throws Exception {
		int totalCapacity = 0;
		int totalSize = 0; 
		long totalHits = 0; 
		long totalMisses = 0; 
		
		long maxUpdateTime = 0;
		String slowestUpdate = "";
		String slowestUpdateRegion = "";
		
		long averageOverflowInterval = 0;
		String averageOverflowRegion = "";
		
		Map<String, CachiusStatistics> cachiusStatistics = getCachius().getStatistics();
		Set<String> regions = cachiusStatistics.keySet();
		for (String region : regions) {
			CachiusStatistics statistics = cachiusStatistics.get(region);
			totalCapacity += statistics.getCapacity();
			totalSize += statistics.getSize();
			totalHits += statistics.getHits();
			totalMisses += statistics.getMisses();
			if (statistics.getMaxUpdateTime() > maxUpdateTime) {
				maxUpdateTime = statistics.getMaxUpdateTime();
				slowestUpdate = statistics.getSlowestUpdate();
				slowestUpdateRegion = region;
			}
			
			if (statistics.getAverageOverflowInterval() > 0 &&
					(averageOverflowInterval == 0 ||
						averageOverflowInterval > statistics.getAverageOverflowInterval())) {
				
				averageOverflowInterval = statistics.getAverageOverflowInterval();
				averageOverflowRegion = region;
			}
			
		}
		
		stats.add("Total Capacity", totalCapacity);
		stats.add("Total Cached items", totalSize);
		stats.add("Total Hits", totalHits);
		stats.add("Total Misses", totalMisses);
		
		if (StringUtils.hasText(slowestUpdateRegion)) {
			stats.add(String.format("Max update time [ms] in region '%s'", slowestUpdateRegion), maxUpdateTime);
			stats.add(String.format("Slowest update in region '%s'", slowestUpdateRegion), slowestUpdate);
		}
		
		if (StringUtils.hasText(averageOverflowRegion)) {
			stats.add(String.format("Smallest average overflow interval [ms] in region '%s'", averageOverflowRegion), averageOverflowInterval);
		}
		
	}

}
