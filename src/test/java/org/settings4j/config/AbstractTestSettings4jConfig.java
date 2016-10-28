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
package org.settings4j.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.settings4j.test.TestUtils;

/**
 * Abstract Class for TestCases .
 */
public abstract class AbstractTestSettings4jConfig {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractTestSettings4jConfig.class);

    /**
     * delete test and tmp folder.
     *
     * @throws Exception
     *         in case of an error
     */
    @Before
    public void setUp() throws Exception {
        deleteTestWorkingDirectories();
    }

    /**
     * delete test and tmp folder.
     *
     * @throws Exception
     *         in case of an error
     */
    @After
    public void tearDown() throws Exception {
        deleteTestWorkingDirectories();
    }

    private void deleteTestWorkingDirectories() throws IOException {
        final File tmpFolder = TestUtils.getTmpFolder();
        LOG.info("Use temporary Folder: {}", tmpFolder.getAbsolutePath());
        FileUtils.deleteDirectory(tmpFolder);

        final File testFolder = TestUtils.getTestFolder();
        LOG.info("Use test Folder: {}", testFolder.getAbsolutePath());
        FileUtils.deleteDirectory(testFolder);
    }

}
