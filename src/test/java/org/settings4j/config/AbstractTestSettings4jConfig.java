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

import org.apache.commons.io.FileUtils;
import org.settings4j.test.TestUtils;

import junit.framework.TestCase;

/**
 * Abstract Class for TestCases .
 * <p>
 * Checkstyle:OFF MagicNumber
 * </p>
 */
public abstract class AbstractTestSettings4jConfig extends TestCase {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractTestSettings4jConfig.class);

    /** {@inheritDoc} */
    @Override
    protected void setUp() throws Exception {
        final File tmpFolder = TestUtils.getTmpFolder();
        LOG.info("Use temporary Folder: {}", tmpFolder.getAbsolutePath());
        FileUtils.deleteDirectory(tmpFolder);

        final File testFolder = TestUtils.getTestFolder();
        LOG.info("Use test Folder: {}", testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
        super.setUp();
    }

    /** {@inheritDoc} */
    @Override
    protected void tearDown() throws Exception {
        final File tmpFolder = TestUtils.getTmpFolder();
        FileUtils.deleteDirectory(tmpFolder);
        final File testFolder = TestUtils.getTestFolder();
        FileUtils.deleteDirectory(testFolder);
        super.tearDown();
    }

}
