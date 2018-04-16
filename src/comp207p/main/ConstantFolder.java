package comp207p.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;

/**original skeleton code from project brief edited by lucywalsh**/


public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimized = null;
	
	public ConstantFolder(String classFilePath)
	{//done
		try{
			this.parser = new ClassParser(classFilePath);
			this.original = this.parser.parse();
			this.gen = new ClassGen(this.original);
			//this.cgen = gen.getConstantPool();
			//this.SimpleFolder = new SimpleFolder(cgen, cpgen);
			//this.VariableFolder = new VariableFolder(cgen, cpgen);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public void optimize()
	{//done I think

		//don't need these as already defined above ^^	
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();

		// Implement your optimization here
		Method[] methods = cgen.getMethods();
 		for (Method m : methods){
 			optimizeMethod(cgen, cpgen, m);
 		}
        
		this.optimized = gen.getJavaClass();
	}

	public void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method){//might need to extend this more
			System.out.println("********Optimising next method********");
			SimpleFolder simpleFolder = new SimpleFolder(cgen, cpgen);
			VariableFolder variableFolder = new VariableFolder(cgen, cpgen);

			//initialise methodCode, instructionList, methodGen for the current method
			Code methodCode = method.getCode();
			InstructionList instructionList = new InstructionList(methodCode.getCode());	
			MethodGen mg = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instructionList, cpgen);
			Method optimisedMethod = method;
			//run each optimiser on the method
			optimisedMethod = variableFolder.optimiseMethod(method, mg, instructionList);

			methodCode = optimisedMethod.getCode();
			instructionList = new InstructionList(methodCode.getCode());
			mg = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(), cgen.getClassName(), instructionList, cpgen);

			try{
            	optimisedMethod = simpleFolder.optimiseMethod(method, mg, instructionList);
        	}
        	catch (TargetLostException e){
            	// TODO Auto-generated catch block
            	System.out.println("simpleFolder not called successfully");
            	e.printStackTrace();
            }
			
			
			//replace the method in the original class with the optimised method
			cgen.replaceMethod(method, optimisedMethod);
	}

	
	public void write(String optimisedFilePath)
	{//don't need to touch this method
		this.optimize();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimized.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}