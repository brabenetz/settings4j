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

package org.settings4j.config;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.UtilTesting;

/**
 * Abstract Class for TestCases .
 * <p>
 * Checkstyle:OFF MagicNumber
 */
public abstract class AbstractTestSettings4jConfig extends TestCase {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(AbstractTestSettings4jConfig.class);

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        final File tmpFolder = UtilTesting.getTmpFolder();
        LOG.info("Use temporary Folder: " + tmpFolder.getAbsolutePath());
        FileUtils.deleteDirectory(tmpFolder);

        final File testFolder = UtilTesting.getTestFolder();
        LOG.info("Use test Folder: " + testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        final File tmpFolder = UtilTesting.getTmpFolder();
        FileUtils.deleteDirectory(tmpFolder);
        final File testFolder = UtilTesting.getTestFolder();
        FileUtils.deleteDirectory(testFolder);
        super.tearDown();
    }

}
