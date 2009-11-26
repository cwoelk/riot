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
package org.riotfamily.core.security.policy;

import org.riotfamily.core.security.auth.RiotUser;

public interface AssertionPolicy extends AuthorizationPolicy {

    /**
	 * By contract this method is invoked whenever an action is about to be 
	 * executed. Implementors can use this hook to veto a previously granted
	 * permission.   
	 * 
	 * @param subject The user
	 * @param action The action to be performed
	 * @param object The object on which the action is to be performed
	 * @param context Optional context information
	 * @throws PermissionDeniedException if the permission is not granted
	 */
    public void assertIsGranted(RiotUser user, String action, Object object, Object context) 
    		throws PermissionDeniedException;
}
