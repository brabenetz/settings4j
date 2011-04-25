/* ***************************************************************************
 * Copyright (c) 2008 Brabenetz Harald, Austria.
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
 * 
 *****************************************************************************/

package org.settings4j.connector.db.dao;

import org.settings4j.connector.db.SettingsDTO;

/**
 * Settings DAO (Data Access Object) with common methods.
 *
 * <p>Implements this interface if you want create DAO's for SettingsDTO objects.
 * 
 */
public interface SettingsDAO {

    /**
     * Method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    SettingsDTO getById(Long id);

    /**
     * Method to get an object based on class and key. Returns null if
     * nothing is found.
     *
     * @param key the {@link SettingsDTO#getKey()}.
     * @return the {@link SettingsDTO}.
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    SettingsDTO getByKey(String key);

    /**
     * Method to store a SettingsDTO - handles both update and insert.
     * @param settingsDTO the {@link SettingsDTO} to save.
     */
    void store(SettingsDTO settingsDTO);

    /**
     * Method to delete an object based on class and id.
     * @param id the identifier (primary key) of the object to remove
     */
    void remove(Long id);
}
