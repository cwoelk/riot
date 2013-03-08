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
package org.riotfamily.statistics.domain;

public class CachiusCacheRegionStatsItem extends StatsItem {
	
	private Integer capacity;

	private Integer size; 

	private Long hits;
	
	private Long misses;

	private long maxUpdateTime;
	
	private String slowestUpdate;

	private long averageOverflowInterval;
	
	public CachiusCacheRegionStatsItem(String name) {
		super(name);
	}
	
	public Integer getCapacity() {
		return capacity;
	}
	
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	
	public Integer getSize() {
		return size;
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Long getMisses() {
		return misses;
	}

	public void setMisses(Long misses) {
		this.misses = misses;
	}
	
	public long getMaxUpdateTime() {
		return maxUpdateTime;
	}
	
	public void setMaxUpdateTime(long maxUpdateTime) {
		this.maxUpdateTime = maxUpdateTime;
	}
	
	public String getSlowestUpdate() {
		return slowestUpdate;
	}
	
	public void setSlowestUpdate(String slowestUpdate) {
		this.slowestUpdate = slowestUpdate;
	}
	
	public long getAverageOverflowInterval() {
		return averageOverflowInterval;
	}
	
	public void setAverageOverflowInterval(long averageOverflowInterval) {
		this.averageOverflowInterval = averageOverflowInterval;
	}

}
