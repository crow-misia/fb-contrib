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
public class S508C_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter,
				Class.forName("MyComponent"),
				Class.forName(getClass().getSimpleName().replace("_Test", "_Sample"))
		);

		reporter.printResults();

		expect(reporter, new HashMap<String, Integer>() {{
			put("DLS_DEAD_LOCAL_STORE", 4);
			put("FCBL_FIELD_COULD_BE_LOCAL", 3);
			put("S508C_APPENDED_STRING", 1);
			put("S508C_NON_ACCESSIBLE_JCOMPONENT", 1);
			put("S508C_NON_TRANSLATABLE_STRING", 4);
			put("S508C_NO_SETLABELFOR", 5);
			put("S508C_NO_SETSIZE", 1);
			put("S508C_NULL_LAYOUT", 1);
			put("S508C_SET_COMP_COLOR", 2);
		}});
	}

}
