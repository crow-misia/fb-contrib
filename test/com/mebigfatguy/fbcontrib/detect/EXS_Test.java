/*
 * fb-contrib - Auxiliary detectors for Java programs
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.mebigfatguy.fbcontrib.detect;

import java.io.IOException;
import java.util.TreeMap;

import jp.co.minori.findbugs.utils.BaseFindBugsTest;
import jp.co.minori.findbugs.utils.EasyBugReporter;

import org.junit.Test;

/**
 * @author Zenichi Amano (crow.misia@gmail.com)
 */
@SuppressWarnings({ "serial", "boxing"})
public class EXS_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter,
				Class.forName("Super"),
				Class.forName(getClass().getSimpleName().replace("_Test", "_Sample"))
		);

		reporter.printResults();

		expect(reporter, new TreeMap<String, Integer>() {{
			put("DLS_DEAD_LOCAL_STORE", 8);
			put("DRE_DECLARED_RUNTIME_EXCEPTION", 1);
			put("EXS_EXCEPTION_SOFTENING_HAS_CHECKED", 1);
			put("EXS_EXCEPTION_SOFTENING_NO_CHECKED", 2);
			put("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS", 1);
			put("LEST_LOST_EXCEPTION_STACK_TRACE", 4);
			put("OS_OPEN_STREAM", 4);
			put("UVA_USE_VAR_ARGS", 1);
			put("WEM_WEAK_EXCEPTION_MESSAGING", 1);
		}});
	}

}
