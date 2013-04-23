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

import java.util.BitSet;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * looks for methods that compare strings against literal strings, where the literal string
 * is passed as the parameter. If the .equals or .compareTo is called on the literal itself, passing
 * the variable as the parameter, you avoid the possibility of a NullPointerException.
 */
public class LiteralStringComparison extends OpcodeStackDetector
{
	private BugReporter bugReporter;
	
	/**
     * constructs a LSC detector given the reporter to report bugs on
     * @param bugReporter the sync of bug reports
	 */
	public LiteralStringComparison(final BugReporter bugReporter) {
		this.bugReporter = bugReporter;		
	}

	/**
	 * looks for methods that contain a LDC or LDC_W opcodes
	 * 
	 * @param method the context object of the current method
	 * @return if the class loads constants
	 */
	public boolean prescreen(Method method) {
		BitSet bytecodeSet = getClassContext().getBytecodeSet(method);
		return (bytecodeSet != null) && (bytecodeSet.get(Constants.LDC) || bytecodeSet.get(Constants.LDC_W));
	}
	
	/**
	 * overrides the visitor to reset the opcode stack
	 * 
	 * @param obj the code object for the currently parsed method
	 */
	@Override
	public void visit(final Code obj) {
		if (prescreen(getMethod())) {
			super.visit(obj);
		}
	}
	
	/**
	 * looks for strings comparisons where the stack object is a literal
	 * 
	 * @param seen the currently parsed opcode
	 */
	@Override
	public void sawOpcode(final int seen) {
		if ((seen == INVOKEVIRTUAL) && "java/lang/String".equals(getClassConstantOperand())) {
			String calledMethodName = getNameConstantOperand();
			String calledMethodSig = getSigConstantOperand();
			
			if (("equals".equals(calledMethodName) && "(Ljava/lang/Object;)Z".equals(calledMethodSig))
			||  ("compareTo".equals(calledMethodName) && "(Ljava/lang/String;)I".equals(calledMethodSig))
            ||  ("equalsIgnoreCase".equals(calledMethodName) && "(Ljava/lang/String;)Z".equals(calledMethodSig))) {
                
				if (stack.getStackDepth() > 0) {
					OpcodeStack.Item itm = stack.getStackItem(0);
					Object constant = itm.getConstant();
					if ((constant != null) && constant.getClass().equals(String.class)) {
						bugReporter.reportBug( new BugInstance( this, "LSC_LITERAL_STRING_COMPARISON", LOW_PRIORITY)
							.addClass(this)
							.addMethod(this)
							.addSourceLine(this));
					}
				}
			}						
		}
	}

}
