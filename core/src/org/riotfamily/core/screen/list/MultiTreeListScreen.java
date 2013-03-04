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
package org.riotfamily.core.screen.list;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.AbstractRiotScreen;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class MultiTreeListScreen extends AbstractRiotScreen implements Controller, 
		ListScreen, ApplicationContextAware, InitializingBean {

	private String viewName = ResourceUtils.getPath(
			MultiTreeListScreen.class, "multi-list.ftl");
	
	private TreeListScreen controlListScreen;

	private TreeListScreen mainListScreen;

	private List<RiotScreen> childScreens;

	public void setListScreens(List<RiotScreen> items) {
		controlListScreen = (TreeListScreen) items.get(0);
		mainListScreen = (TreeListScreen) items.get(1);
		
		this.childScreens = items;
		for (RiotScreen item : items) {
			item.setParentScreen(this);
		}
	}

	public Collection<RiotScreen> getChildScreens() {
		return childScreens;
	}	
	
	@Override
	public String getTitle(ScreenContext context) {
		if (context.getParent() != null && getParentScreen() instanceof ListScreen) {
			return ScreenUtils.getParentListScreen(this)
					.getItemLabel(context.getParent());			
		}
		return super.getTitle(context);
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ScreenContext screenContext = ScreenContext.Binding.get(request);
		ChooserSettings chooserSettings = new ChooserSettings(request);
		ListState controlState = controlListScreen.getOrCreateListState(request, screenContext, chooserSettings);
		//ListState mainState = mainListScreen.getOrCreateListState(request, screenContext, chooserSettings);

		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("controlListState", controlState);
		
		ScreenContext mainListContext = screenContext.createChildContext(mainListScreen);
		if (AccessController.isGranted("viewScreen", mainListScreen, mainListContext)) {
			ScreenLink mainListLink = new ScreenLink(mainListScreen.getTitle(mainListContext),
					HandlerUrlUtils.getContextRelativeUrl(request, mainListScreen.getId(), mainListContext),
					mainListScreen.getIcon(), false);

			mv.addObject("mainListLink", mainListLink);
		}
		
		return mv;
	}

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
	}

	/* ListScreen contract - delegating through to the main list */
	
	public RiotDao getDao() {
		return controlListScreen.getDao();
	}

	public RiotScreen getItemScreen() {
		return mainListScreen;
	}

	public String getItemLabel(Object object) {
		return mainListScreen.getItemLabel(object);
	}

	public String getListStateKey(ScreenContext screenContext) {
		return mainListScreen.getListStateKey(screenContext);
	}
		
}
