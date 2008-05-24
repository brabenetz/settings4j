package org.settings4j;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.connector.ClasspathConnector;
import org.settings4j.connector.SystemPropertyConnector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;
import org.settings4j.settings.DefaultSettings;
import org.settings4j.settings.HierarchicalSettingsRepository;

/**
 * Unit test for the Hirachical Structure in Settings4j
 * Also test simply Connectors, and ContentResolver
 */
public class SettingsRepositoryTest extends TestCase {
    
    private File testDir;

    protected void setUp() throws Exception {
        super.setUp();
        testDir = (new File(UtilTesting.getTestFolder(), "ContentResolverTest".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(testDir);
    }

    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        super.tearDown();
    }

    /**
     * Test simply getString() in Hierarchical way.<br />
     * <br />
     * Tested Connectors:<br />
     * {@link SystemPropertyConnector}, {@link UnionContentResolver}, {@link ClasspathConnector} (only since hierarchy node: "x.y.z")<br />
     * <br />
     * Tested ContentResolver: <br />
     * {@link FSContentResolver}<br />
     * <br />
     * 
     * @throws Exception 
     */
    public void testHierarchy() throws Exception {
        assertTrue(true);
        SettingsRepository settingsRepository = new HierarchicalSettingsRepository(new DefaultSettings("root"));

        Settings rootSettings;
        Settings xy;
        Settings xyz;
        
        Connector connector = new SystemPropertyConnector();
        ContentResolver unionContentResolver = new UnionContentResolver();
        
        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath());
        unionContentResolver.addContentResolver(fsContentResolver);
        
        unionContentResolver.addContentResolver(new ClasspathContentResolver());
        connector.setContentResolver(unionContentResolver);
        
        // SystemPropertyConnector with  fsContentResolver & ClasspathContentResolver
        rootSettings = settingsRepository.getRootSettings();
        rootSettings.addConnector(connector);

        // inheridents all from rootSettings
        xy = settingsRepository.getSettings("x.y");
        
        // search first in ClasspathConnector and than in SystemPropertyConnector from rootSettings
        xyz = settingsRepository.getSettings("x.y.z");

        connector = new ClasspathConnector();
        connector.setContentResolver(unionContentResolver);
        xyz.addConnector(connector);
        // The connector Count must be greater 0
        // otherwise the default-fallback-configuration will be used.
        settingsRepository.setConnectorCount(2);
        
        /*
         * Now starts the Unittests
         */
        rootSettings = settingsRepository.getRootSettings();
        xy = settingsRepository.getSettings("x.y");
        xyz = settingsRepository.getSettings("x.y.z");
        
        
        String resultString;
        byte[] resultContent;
        Object resultObject;

        // The RootSetting have only the SystemPropertyConnector with the FSContentResolver
        // all result shoul return null
        resultString = rootSettings.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultString);
        
        resultContent = rootSettings.getContent("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultContent);
        
        resultObject = rootSettings.getObject("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultObject);


        resultString = xy.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultString);
        
        resultContent = xy.getContent("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultContent);
        
        resultObject = xy.getObject("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultObject);

        // xyz have the classpath Connector:
        resultString = xyz.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNotNull(resultString);
        assertEquals("Hello World 2", resultString);
        
        resultContent = xyz.getContent("org/settings4j/connector/HelloWorld2.txt");
        assertNotNull(resultContent);
        assertEquals("Hello World 2", new String(resultContent, "UTF-8"));
        
        resultObject = xyz.getObject("org/settings4j/connector/HelloWorld2.txt");
        assertNull(resultObject);
        
        // set Value into System Properties
        System.setProperty("org/settings4j/connector/HelloWorld2.txt", "test"); 

        resultString = rootSettings.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNotNull(resultString);
        assertEquals("test", resultString);

        resultString = xy.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNotNull(resultString);
        assertEquals("test", resultString);
        
        resultString = xyz.getString("org/settings4j/connector/HelloWorld2.txt");
        assertNotNull(resultString);
        assertEquals("Hello World 2", resultString);

        
        System.setProperty("org/settings4j/connector/HelloWorld5.txt", "test5"); 

        resultString = rootSettings.getString("org/settings4j/connector/HelloWorld5.txt");
        assertNotNull(resultString);
        assertEquals("test5", resultString);

        resultString = xy.getString("org/settings4j/connector/HelloWorld5.txt");
        assertNotNull(resultString);
        assertEquals("test5", resultString);
        
        // not in classpath => get it from rootSetting (SystemProperties)
        resultString = xyz.getString("org/settings4j/connector/HelloWorld5.txt");
        assertNotNull(resultString);
        assertEquals("test5", resultString);
    }
}
