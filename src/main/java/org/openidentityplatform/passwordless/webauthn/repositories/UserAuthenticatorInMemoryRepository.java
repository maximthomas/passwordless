/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.openidentityplatform.passwordless.webauthn.repositories;

import com.webauthn4j.authenticator.Authenticator;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository("userAuthenticatorRepository")
public class UserAuthenticatorInMemoryRepository implements UserAuthenticatorRepository{

    private Map<String, Set<Authenticator>> userAuthenticatorsMap = new HashMap<>();

    @Override
    public void save(String username, Authenticator authenticator) {
        if(!userAuthenticatorsMap.containsKey(username)) {
            userAuthenticatorsMap.put(username, new HashSet<>());
        }
        userAuthenticatorsMap.get(username).add(authenticator);

    }

    @Override
    public Set<Authenticator> load(String username) {
        if(!userAuthenticatorsMap.containsKey(username)) {
            return null;
        }
        return userAuthenticatorsMap.get(username);
    }
}
