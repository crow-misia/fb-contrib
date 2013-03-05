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
public class NSE_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter, Class.forName(getClass().getSimpleName().replace("_Test", "_Sample")));

		reporter.printResults();

		expect(reporter, new TreeMap<String, Integer>() {{
			put("BC_IMPOSSIBLE_CAST", 1);
			put("EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS", 3);
			put("EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC", 1);
			put("HE_EQUALS_USE_HASHCODE", 4);
			put("ITC_INHERITANCE_TYPE_CHECKING", 2);
			put("NSE_NON_SYMMETRIC_EQUALS", 2);
			put("SIC_INNER_SHOULD_BE_STATIC", 2);
			put("UWF_UNWRITTEN_FIELD", 2);
			put("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", 2);
		}});
	}

}
