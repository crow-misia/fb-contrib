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

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.AnalysisError;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.SortingBugReporter;

/**
 * @author Zenichi Amano (crow.misia@gmail.com)
 */
public final class EasyBugReporter extends SortingBugReporter {

	private final String filter;

	private static final Logger log = Logger.getLogger(EasyBugReporter.class.getName());

	public EasyBugReporter() {
		this(null);
	}

	public EasyBugReporter(final String filter) {
		this.filter = filter;
		
		setPriorityThreshold(20);
		setApplySuppressions(false);
		setShowRank(true);
	}

	public void finish() {
		for (final BugInstance bugInstance : getBugCollection()) {
			printBug(bugInstance);
		}
	}

	public void printResults() {
		// for JUnit
		final Map<String, Integer> counts = new TreeMap<String, Integer>();
		for (final BugInstance bugInstance : getBugCollection()) {
			final String bugType = bugInstance.getBugPattern().getType();
			Integer count = counts.get(bugType);
			if (count == null) {
				counts.put(bugType, 1);
			} else {
				counts.put(bugType, count + 1);
			}
		}

		for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
			System.out.printf("put(\"%s\", %d);\n", entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void doReportBug(BugInstance bugInstance) {
		if (filter != null && !StringUtils.startsWith(bugInstance.getBugPattern().getType(), filter)) {
			return;
		}
		
		super.doReportBug(bugInstance);
	}

	@Override
	public void reportAnalysisError(AnalysisError error) {
		if (error.getException() != null) {
			log.log(Level.SEVERE, "Report Analysis Error" + error.getException().getMessage() + "\n", error.getException());
		} else {
			log.log(Level.SEVERE, "Report Analysis Error", error);
		}
	}

	@Override
	public void reportMissingClass(String className) {
		throw new RuntimeException("Missing class " + className);
	}

}