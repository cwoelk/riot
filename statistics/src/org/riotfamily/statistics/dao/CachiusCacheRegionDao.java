/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusStatistics;
import org.riotfamily.common.util.Generics;
import org.riotfamily.statistics.domain.CachiusCacheRegionStatsItem;
import org.riotfamily.statistics.domain.StatsItem;
import org.springframework.dao.DataAccessException;

public class CachiusCacheRegionDao extends AbstractCachiusStatisticsDao {

	public CachiusCacheRegionDao(CacheService cachius) {
		super(cachius);
	}

	@Override
	public boolean canSortBy(String property) {
		return true;
	}

	public Class<?> getEntityClass() {
		return CachiusCacheRegionStatsItem.class;
	}
	
	@Override
	protected List<? extends StatsItem> getStats() {
		ArrayList<CachiusCacheRegionStatsItem> stats = Generics.newArrayList();
		
		Map<String, CachiusStatistics> cachiusStatistics = getCachius().getStatistics();
		Set<String> regions = cachiusStatistics.keySet();
		for (String region : regions) {
			CachiusCacheRegionStatsItem item = new CachiusCacheRegionStatsItem(region);
			CachiusStatistics statistics = cachiusStatistics.get(region);
			item.setCapacity(statistics.getCapacity());
			item.setSize(statistics.getSize());
			item.setHits(statistics.getHits());
			item.setMisses(statistics.getMisses());
			item.setMaxUpdateTime(statistics.getMaxUpdateTime());
			item.setSlowestUpdate(statistics.getSlowestUpdate());
			item.setAverageOverflowInterval(statistics.getAverageOverflowInterval());
			stats.add(item);
		}
		
		return stats;
	}
	
	public Object load(String id) throws DataAccessException {
		return new CachiusCacheRegionStatsItem(id);
	}

}
