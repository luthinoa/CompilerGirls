package comp207p.main;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Map;


public class VariableFolder{

    private ClassGen cg;
    private ConstantPoolGen cpg;
    private Map<Integer, Number> variables = null;

    public VariableFolder(ClassGen cg, ConstantPoolGen cpg) {
        this.cg = cg;
        this.cpg = cpg;
    }

    Method optimiseMethod(Method method, MethodGen mg, InstructionList instructionList) {
        boolean optimised = false;
        variables = new HashMap<>();
        for (InstructionHandle instructionHandle : instructionList.getInstructionHandles()) {
            Instruction currentInstruction = instructionHandle.getInstruction();
            if (instructionHandle.getNext() != null) {
                Instruction nextInstruction = (instructionHandle.getNext()).getInstruction();
                if (nextInstruction instanceof StoreInstruction) {
                    updateVariables(currentInstruction, nextInstruction);
                }
            }

            //constant variable propagation
            if ((currentInstruction instanceof LoadInstruction)&&!(currentInstruction instanceof ALOAD)) {
                boolean arithmeticOptimised = propagateVariable(instructionHandle, instructionList);
                optimised = optimised || arithmeticOptimised;
            }
        }
        return optimised ? mg.getMethod() : null;
    }

    //Will replace load instructions for a push for constant variables
    private boolean propagateVariable(InstructionHandle instructionHandle, InstructionList instructionList) {
        LoadInstruction loadInstruction = (LoadInstruction) instructionHandle.getInstruction();
        int key = loadInstruction.getIndex();

        if (!isConstantVariable(key, instructionHandle, instructionList)){
            return false;
        }

        if(loopAffectedVariable(key, instructionList, instructionHandle)){
            return false;
        }
        Number num = this.variables.get(key);
        Instruction newInstruction = null;
        if (num instanceof Double) {
            newInstruction = new LDC2_W(cpg.addDouble(num.doubleValue()));
        } else if (num instanceof Long) {
            newInstruction = new LDC2_W(cpg.addLong(num.longValue()));
        } else if (num instanceof Float) {
            newInstruction = new LDC(cpg.addFloat(num.floatValue()));
        } else if (num instanceof Integer) {
            newInstruction = new LDC(cpg.addInteger(num.intValue()));
        }
        InstructionHandle newHandle = instructionList.append(instructionHandle, newInstruction);
        deleteVariable(instructionList, instructionHandle, newHandle);
        return true;

    }

    private void updateVariables() {
        //Lucy
    }

    private void deleteVariable(){
        //Lucy
    }

    private boolean isConstantVariable(int key, InstructionHandle instructionHandle, InstructionList instructionList){
        //If not a variable
        if(!variables.containsKey(key)){
            return false;
        }

        //If targeted by a GoToInstruction
        for(InstructionTargeter instructionTargeter : instructionHandle.getTargeters()){
            if (instructionTargeter instanceof GotoInstruction){
                return false;
            }
        }

        //If changed within a loop
        if(loopAffectedVariable(key, instructionList, instructionHandle)){
            return false;
        }

        return true;
    }

    private boolean loopAffectedVariable(int key, InstructionList instructionList, InstructionHandle instructionHandle){
        int start = -1;
        int end = -1;
        boolean loop = false;
        int pos = instructionHandle.getPosition();

        //Determines whether variable is in loop
        while(instructionHandle.getNext()!=null){
            Instruction instruction = instructionHandle.getInstruction();
            if(instruction instanceof GotoInstruction){
                GotoInstruction gotoInstruction = (GotoInstruction) instruction;
                if((gotoInstruction.getTarget()).getPosition() < pos){
                    loop = true;
                    start = (gotoInstruction.getTarget()).getPosition();
                    end = instructionHandle.getPosition();
                }
            }
            instructionHandle = instructionHandle.getNext();
        }

        //If the variable is not in a loop here, it is not affected by a loop
        if(!loop){return false;}

        int loopCurrent = start;
        while (loopCurrent<end){
            InstructionHandle loopInstructionHandle = instructionList.findHandle(loopCurrent);
            if(loopInstructionHandle!=null){
                Instruction loopInstruction = loopInstructionHandle.getInstruction();
                if(loopInstruction instanceof  LocalVariableInstruction){
                    if (!(loopInstruction instanceof LoadInstruction)){
                        LocalVariableInstruction loopVariableInstruction = (LocalVariableInstruction) loopInstruction;
                        int loopKey = loopVariableInstruction.getIndex();
                        if(loopKey == key){
                            return true;
                        }
                    }
                }
            }
            loopCurrent = loopCurrent + 1;
        }
        return false;
    }

}