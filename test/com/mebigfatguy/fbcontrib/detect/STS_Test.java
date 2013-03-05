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
import java.util.HashMap;

import jp.co.minori.findbugs.utils.BaseFindBugsTest;
import jp.co.minori.findbugs.utils.EasyBugReporter;

import org.junit.Test;

/**
 * @author Zenichi Amano (crow.misia@gmail.com)
 */
@SuppressWarnings({ "serial", "boxing"})
public class STS_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter, Class.forName(getClass().getSimpleName().replace("_Test", "_Sample")));

		reporter.printResults();

		expect(reporter, new HashMap<String, Integer>() {{
			put("DLS_DEAD_LOCAL_STORE", 1);
			put("MDM_THREAD_YIELD", 2);
			put("MDM_WAIT_WITHOUT_TIMEOUT", 1);
			put("NOS_NON_OWNED_SYNCHRONIZATION", 1);
			put("NO_NOTIFY_NOT_NOTIFYALL", 1);
			put("STS_SPURIOUS_THREAD_STATES", 1);
			put("SWL_SLEEP_WITH_LOCK_HELD", 1);
			put("UW_UNCOND_WAIT", 1);
			put("WA_NOT_IN_LOOP", 1);
		}});
	}

}
