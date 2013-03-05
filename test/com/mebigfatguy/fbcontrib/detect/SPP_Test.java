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
public class SPP_Test extends BaseFindBugsTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		final EasyBugReporter reporter = new EasyBugReporter();
		analyze(reporter, Class.forName(getClass().getSimpleName().replace("_Test", "_Sample")));

		reporter.printResults();

		expect(reporter, new TreeMap<String, Integer>() {{
			put("DLS_DEAD_LOCAL_STORE", 9);
			put("DMI_BIGDECIMAL_CONSTRUCTED_FROM_DOUBLE", 1);
			put("DM_CONVERT_CASE", 2);
			put("FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER", 2);
			put("LSC_LITERAL_STRING_COMPARISON", 3);
			put("LSYC_LOCAL_SYNCHRONIZED_COLLECTION", 2);
			put("MDM_RANDOM_SEED", 1);
			put("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", 2);
			put("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", 7);
			put("RV_01_TO_INT", 2);
			put("SA_LOCAL_DOUBLE_ASSIGNMENT", 1);
			put("SE_INNER_CLASS", 1);
			put("SIC_INNER_SHOULD_BE_STATIC_ANON", 1);
			put("SPP_EMPTY_CASING", 1);
			put("SPP_EQUALS_ON_ENUM", 1);
			put("SPP_INTERN_ON_CONSTANT", 1);
			put("SPP_INVALID_BOOLEAN_NULL_CHECK", 2);
			put("SPP_INVALID_CALENDAR_COMPARE", 1);
			put("SPP_NEGATIVE_BITSET_ITEM", 1);
			put("SPP_NON_ARRAY_PARM", 5);
			put("SPP_NO_CHAR_SB_CTOR", 1);
			put("SPP_SERIALVER_SHOULD_BE_PRIVATE", 1);
			put("SPP_STRINGBUFFER_WITH_EMPTY_STRING", 1);
			put("SPP_STRINGBUILDER_IS_MUTABLE", 2);
			put("SPP_STUTTERED_ASSIGNMENT", 1);
			put("SPP_SUSPECT_STRING_TEST", 6);
			put("SPP_TEMPORARY_TRIM", 2);
			put("SPP_USELESS_CASING", 2);
			put("SPP_USELESS_TRINARY", 1);
			put("SPP_USE_BIGDECIMAL_STRING_CTOR", 1);
			put("SPP_USE_CONTAINSKEY", 1);
			put("SPP_USE_GETPROPERTY", 1);
			put("SPP_USE_ISEMPTY", 2);
			put("SPP_USE_ISNAN", 2);
			put("SPP_USE_MATH_CONSTANT", 1);
			put("SPP_USE_STRINGBUILDER_LENGTH", 2);
		}});
	}

}
