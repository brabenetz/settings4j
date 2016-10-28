/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j;


/**
 * Some Constants like the Return Values (success or not) of Connectors.set*(...) Methods.
 *
 * @author Harald.Brabenetz
 *
 */
public final class Constants {

    /**
     * Return value for:<br>
     * {@link org.settings4j.connector.JNDIConnector#setObject(String, Object)}.
     * <br>
     * If Settings is not possible.
     */
    public static final int SETTING_NOT_POSSIBLE = 0;

    /**
     * Return value for:<br>
     * {@link org.settings4j.connector.JNDIConnector#setObject(String, Object)}.
     * <br>
     * if Setting was successful: The value for the given key was replaced.
     */
    public static final int SETTING_SUCCESS = 1;

    /** Hide Constructor. */
    private Constants() {
        super();
    }
}
