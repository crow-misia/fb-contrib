/*
 * fb-contrib - Auxiliary detectors for Java programs
 * Copyright (C) 2005-2013 Dave Brosius
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

import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * looks for constructors that operate through side effects, specifically
 * constructors that aren't assigned to any variable or field.
 */
public class SideEffectConstructor extends OpcodeStackDetector {

	private final BugReporter bugReporter;

	private Integer secPC;
	private int initDepth;
	private boolean isNew = false;

	/**
     * constructs a SEC detector given the reporter to report bugs on
     * 
     * @param bugReporter the sync of bug reports
	 */
	public SideEffectConstructor(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	/**
	 * overrides the visitor to set up and tear down the opcode stack
	 * 
	 * @param classContext the context object of the currently parsed class
	 */
	@Override
	public void visitClassContext(ClassContext classContext) {
		secPC = null;
		initDepth = -1;
		isNew = false;
		super.visitClassContext(classContext);
	}

	/**
	 * overrides the visitor to look for constructors who's value is 
	 * popped off the stack, and not assigned before the pop of the value, or if a
	 * return is issued with that object still on the stack.
	 * 
	 * @param seen the opcode of the currently parse opcode
	 */
	@Override
	public void sawOpcode(final int seen) {
		switch (seen) {
		case NEW:
			isNew = true;
			break;

		case INVOKESTATIC:
		case INVOKEINTERFACE:
			if (initDepth == 1) {
				detectBug();
			}
			secPC = null;
			break;

		case INVOKESPECIAL:
			if (initDepth == 1) {
				detectBug();
			}

			final String name = getNameConstantOperand();
			if (isNew && "<init>".equals(name)) {
				isNew = false;
				final String sig = getSigConstantOperand();
				final int numArgs = Type.getArgumentTypes(sig).length;
				if (stack.getStackDepth() > numArgs) {
					final OpcodeStack.Item caller = stack.getStackItem(numArgs);
					if (caller.getRegisterNumber() != 0) {
						secPC = Integer.valueOf(getPC());
						initDepth = stack.getStackDepth() - numArgs;
					}
				}
			} else {
				secPC = null;
			}
			break;

		case ASTORE_0:
		case ASTORE_1:
		case ASTORE_2:
		case ASTORE_3:
		case ASTORE:
		case AASTORE:
		case INVOKEVIRTUAL:
		case ATHROW:
		case ARETURN:
		case PUTFIELD:
		case PUTSTATIC:
			secPC = null;
			break;

		case POP:
		case RETURN:
			detectBug();
			break;

		default:
			break;
		}
	}

	private void detectBug() {
		if (secPC != null) {
			bugReporter.reportBug(new BugInstance(this, "SEC_SIDE_EFFECT_CONSTRUCTOR", NORMAL_PRIORITY)
			.addClass(this)
			.addMethod(this)
			.addSourceLine(this, secPC.intValue()));

			secPC = null;
			initDepth = -1;
		}
	}
}
