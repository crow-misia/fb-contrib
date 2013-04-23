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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XField;

/**
 * looks for classes that maintain collections or StringBuffer/StringBuilders in static member
 * variables, and that do not appear to provide a way to clear or remove items from these members.
 * Such class fields are likely causes of memory bloat.
 *
 */
public class PossibleMemoryBloat extends BytecodeScanningDetector
{
	private static final Set<String> bloatableSigs = new HashSet<String>();
	static {
		bloatableSigs.add("Ljava/util/ArrayList;");
		bloatableSigs.add("Ljava/util/Collection;");
		bloatableSigs.add("Ljava/util/HashMap;");
		bloatableSigs.add("Ljava/util/HashSet;");
		bloatableSigs.add("Ljava/util/Hashtable;");
		bloatableSigs.add("Ljava/util/IdentityHashMap;");
		bloatableSigs.add("Ljava/util/LinkedHashMap;");
		bloatableSigs.add("Ljava/util/LinkedList;");
		bloatableSigs.add("Ljava/util/List;");
		bloatableSigs.add("Ljava/util/Map;");
		bloatableSigs.add("Ljava/util/Set;");
		bloatableSigs.add("Ljava/util/SortedSet;");
		bloatableSigs.add("Ljava/util/SortedMap;");
		bloatableSigs.add("Ljava/util/Stack;");
		bloatableSigs.add("Ljava/util/Queue;");
		bloatableSigs.add("Ljava/lang/StringBuffer;");
		bloatableSigs.add("Ljava/lang/StringBuilder;");
		bloatableSigs.add("Ljava/util/TreeMap;");
		bloatableSigs.add("Ljava/util/TreeSet;");
		bloatableSigs.add("Ljava/util/Vector;");
	}
	private static final Set<String> decreasingMethods = new HashSet<String>();
	static {
		decreasingMethods.add("clear");
		decreasingMethods.add("delete");
		decreasingMethods.add("deleteCharAt");
		decreasingMethods.add("pop");
		decreasingMethods.add("remove");
		decreasingMethods.add("removeAll");
		decreasingMethods.add("removeAllElements");
		decreasingMethods.add("removeElementAt");
		decreasingMethods.add("removeRange");
		decreasingMethods.add("setLength");
	}
	
	private static final Set<String> increasingMethods = new HashSet<String>();
	static {
		increasingMethods.add("add");
		increasingMethods.add("addAll");
		increasingMethods.add("addElement");
		increasingMethods.add("addFirst");
		increasingMethods.add("addLast");
		increasingMethods.add("append");
		increasingMethods.add("insertElementAt");
		increasingMethods.add("put");
	}
	private final BugReporter bugReporter;
	private Map<XField, SourceLineAnnotation> bloatableFields;
	private OpcodeStack stack;
	private String methodName;
	
	/**
     * constructs a PMB detector given the reporter to report bugs on
     * @param bugReporter the sync of bug reports
	 */
	public PossibleMemoryBloat(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}
	
	/**
	 * collects static fields that are likely bloatable objects and if found
	 * allows the visitor to proceed, at the end report all leftover fields
	 * 
	 * @param classContext the class context object of the currently parsed java class
	 */
	@Override
	public void visitClassContext(ClassContext classContext) {
		try {
			bloatableFields = new HashMap<XField, SourceLineAnnotation>();
			JavaClass cls = classContext.getJavaClass();
			Field[] fields = cls.getFields();
			for (Field f : fields) {
				if (f.isStatic()) {
					String sig = f.getSignature();
					if (bloatableSigs.contains(sig)) {
						bloatableFields.put(XFactory.createXField(cls.getClassName(), f.getName(), f.getSignature(), f.isStatic()), null);
					}
				}
			}
			
			if (bloatableFields.size() > 0) {
				stack = new OpcodeStack();
				super.visitClassContext(classContext);
	
				for (Map.Entry<XField, SourceLineAnnotation> entry : bloatableFields.entrySet()) {
					SourceLineAnnotation sla = entry.getValue();
					if (sla != null) {
						bugReporter.reportBug(new BugInstance(this, "PMB_POSSIBLE_MEMORY_BLOAT", NORMAL_PRIORITY)
									.addClass(this)
									.addSourceLine(sla)
									.addField(entry.getKey()));
					}
				}
			}
		} finally {
			stack = null;
			bloatableFields = null;
		}
	}
	
	/**
	 * implements the visitor to collect the method name
	 * 
	 * @param obj the context object of the currently parsed method
	 */
	@Override
	public void visitMethod(Method obj) {
		methodName = obj.getName();
	}
	
	/**
	 * implements the visitor to reset the opcode stack
	 * 
	 * @param obj the context object of the currently parsed code block
	 */
	@Override
	public void visitCode(Code obj) {
		stack.resetForMethodEntry(this);
		
		if ("<clinit>".equals(methodName)
		||  "<init>".equals(methodName))
			return;
		
		if (bloatableFields.size() > 0)
			super.visitCode(obj);
	}
	
	/**
	 * implements the visitor to look for methods that empty a bloatable field
	 * if found, remove these fields from the current list
	 * 
	 * @param seen the opcode of the currently parsed instruction
	 */
	@Override
	public void sawOpcode(int seen) {
		try {
			stack.mergeJumps(this);
			
			if (bloatableFields.isEmpty())
				return;
			
			if ((seen == INVOKEVIRTUAL)
			||  (seen == INVOKEINTERFACE)) {
				String sig = getSigConstantOperand();
				int argCount = Type.getArgumentTypes(sig).length;
				if (stack.getStackDepth() > argCount) {
					OpcodeStack.Item itm = stack.getStackItem(argCount);
					XField field = itm.getXField();
					if (field != null) {
						if (bloatableFields.containsKey(field)) {
							String mName = getNameConstantOperand();
							if (decreasingMethods.contains(mName)) {
								bloatableFields.remove(field);
							} else if (increasingMethods.contains(mName)) {
								if (bloatableFields.get(field) == null) {
									SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction(this);
									bloatableFields.put(field, sla);
								}
							}
						}
					}
				}
			}
		}
		finally {
			stack.sawOpcode(this, seen);
		}
	}
}
