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
public class NAB_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter, Class.forName(getClass().getSimpleName().replace("_Test", "_Sample")));

		reporter.printResults();

		expect(reporter, new HashMap<String, Integer>() {{
			put("BX_BOXING_IMMEDIATELY_UNBOXED", 7);
			put("BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION", 15);
			put("BX_UNBOXING_IMMEDIATELY_REBOXED", 8);
			put("DLS_DEAD_LOCAL_STORE", 84);
			put("DM_BOOLEAN_CTOR", 5);
			put("DM_FP_NUMBER_CTOR", 19);
			put("DM_NUMBER_CTOR", 29);
			put("LSC_LITERAL_STRING_COMPARISON", 1);
			put("NAB_NEEDLESS_AUTOBOXING_CTOR", 8);
			put("NAB_NEEDLESS_AUTOBOXING_VALUEOF", 8);
			put("NAB_NEEDLESS_BOOLEAN_CONSTANT_CONVERSION", 6);
			put("NAB_NEEDLESS_BOXING_PARSE", 7);
			put("NAB_NEEDLESS_BOXING_STRING_CTOR", 7);
			put("NAB_NEEDLESS_BOXING_VALUEOF", 7);
			put("NAB_NEEDLESS_BOX_TO_CAST", 30);
			put("NAB_NEEDLESS_BOX_TO_UNBOX", 14);
			put("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", 13);
		}});
	}

}
