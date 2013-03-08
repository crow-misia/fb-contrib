/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package jp.co.minori.findbugs.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.BeforeClass;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Plugin;
import edu.umd.cs.findbugs.PluginException;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.ProjectFilterSettings;
import edu.umd.cs.findbugs.config.UserPreferences;

/**
 * @author Zenichi Amano (crow.misia@gmail.com)
 */
public abstract class BaseFindBugsTest {

	private static Plugin loadedPlugin = null;

	@BeforeClass
	public static void initialize() throws PluginException, IOException {
		final ClassLoader cl = BaseFindBugsTest.class.getClassLoader();

		if (loadedPlugin == null) {
			final Project project = new Project();

			//Initialize the plugin base on the findbugs.xml
			final byte[] archive = buildFakePluginJar(cl);

			File f = new File(System.getProperty("java.io.tmpdir"), "plugin.jar");
			System.out.println("Writing " + f.getCanonicalPath());
			f.deleteOnExit();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(f);
				out.write(archive);
			} finally {
				closeStream(out);
			}

			loadedPlugin = Plugin.loadCustomPlugin(f, project);
		}
	}

	protected static void analyze(final BugReporter reporter, final Class<?>... classes) throws IOException, InterruptedException {
		final ClassLoader cl = BaseFindBugsTest.class.getClassLoader();

		final Project project = new Project();
		project.setProjectName("FindBugs Plugin Test");

		// add AUX Classpath
		project.addAuxClasspathEntry(System.getProperty("java.home") + "/lib/rt.jar");
		for (final String path : System.getProperty("java.class.path").split(";")) {
			if (path.endsWith(".jar")) {
				project.addAuxClasspathEntry(path);
			}
		}

		System.out.println();
		for (final Class<?> clazz : classes) {
			final String className = clazz.getSimpleName();

			final URL url = cl.getResource(clazz.getCanonicalName().replace('.', '/') + ".class");
			for (final File file : new File(url.getFile()).getParentFile().listFiles()) {
				if (file.isFile() && file.getName().endsWith(".class")) {
					if (!file.exists()) {
						System.out.println(file.getAbsolutePath() + " is not found.");
					} else if (file.getName().startsWith(className + ".") || file.getName().startsWith(className + "$")) {
						System.out.println(file.getAbsolutePath());
						project.addFile(file.getAbsolutePath());
					}
				}
			}
		}

		final FindBugs2 findBugs = new FindBugs2();
		findBugs.setProject(project);
		findBugs.setApplySuppression(false);
		findBugs.setNoClassOk(true);
		findBugs.setMergeSimilarWarnings(false);
		findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
		findBugs.setBugReporter(reporter);

		UserPreferences prefs = UserPreferences.createDefaultUserPreferences();

		ProjectFilterSettings filter = prefs.getFilterSettings();
		filter.setMinRank(20);
		filter.setDisplayFalseWarnings(true);
		filter.setMinPriority(ProjectFilterSettings.LOW_PRIORITY);

		findBugs.setUserPreferences(prefs);

		findBugs.finishSettings();

		findBugs.execute();
	}

	@SuppressWarnings("boxing")
	public static void expect(final EasyBugReporter reporter, final Map<String, Integer> expected) {
		final BugCollection collection = reporter.getBugCollection();

		final Map<String, Integer> actual = new TreeMap<String, Integer>();
		for (final BugInstance bug : collection) {
			final String type = bug.getBugPattern().getType();
			Integer count = actual.get(type);
			if (count == null) {
				actual.put(type, 1);
			} else {
				actual.put(type, count + 1);
			}
		}
		assertThat(actual, is(equalTo(expected)));
	}

	/**
	 * The minimum requirement to have a "valid" archive plugin is to include
	 * findbugs.xml, messages.xml and MANIFEST.MF files. The rest of the
	 * resources are load using the parent ClassLoader (Not requires to be in
	 * the jar).
	 * <p>
	 * Instead of building a file on disk, the result of the stream is kept in
	 * memory and return as a byte array.
	 * @return
	 * @throws IOException
	 */
	private static byte[] buildFakePluginJar(final ClassLoader cl) throws IOException {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		JarOutputStream jar = null;
		InputStream is = null;

		try {
			jar = new JarOutputStream(buffer);

			//Add files to the jar stream
			final byte[] buf = new byte[8192];
			for (String resource : Arrays.asList("etc/findbugs.xml", "etc/messages.xml", "etc/bugrank.txt", "META-INF/MANIFEST.MF")) {
				jar.putNextEntry(new ZipEntry(resource.replace("etc/", "")));
				
				is = new FileInputStream(resource);
				int n = 0;
				while ((n = is.read(buf)) != -1) {
					jar.write(buf, 0, n);
				}
				is.close();
				is = null;

				jar.closeEntry();
			}
			jar.finish();
		} finally {
			closeStream(jar);
			closeStream(is);
		}

		return buffer.toByteArray();
	}

	private static void closeStream(final Closeable s) {
		if (s != null) {
			try {
				s.close();
			} catch (final IOException e) {
				// do nothing.
			}
		}
	}
}
