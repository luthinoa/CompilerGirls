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
        Method oldMethod = method;
        variables = new HashMap<>();
        while(!optimised){
          oldMethod=mg.getMethod();
          for (InstructionHandle instructionHandle : instructionList.getInstructionHandles()) {
              Instruction currentInstruction = instructionHandle.getInstruction();
              if (instructionHandle.getNext() != null) {
                  Instruction nextInstruction = (instructionHandle.getNext()).getInstruction();
                  if (nextInstruction instanceof StoreInstruction) {
                      updateVariables(currentInstruction, (StoreInstruction) nextInstruction);
                  }
              }

              //constant variable propagation
              if ((currentInstruction instanceof LoadInstruction)&&!(currentInstruction instanceof ALOAD)) {
                  boolean arithmeticOptimised = propagateVariable(instructionHandle, instructionList);
              }
          }
          if(mg.getMethod().equals(method)){optimised=true;} //No more optimisations can occur
        }
        //return optimised ? mg.getMethod() : null;
        return mg.getMethod();
    }

    //Will replace load instructions for a push for constant variables
    private boolean propagateVariable(InstructionHandle instructionHandle, InstructionList instructionList) {
        System.out.println("propagating variables...");
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

    private void updateVariables(Instruction currentInstruction, StoreInstruction nextInstruction) {
      int index = nextInstruction.getIndex();
      Number value = getInstructionValue(currentInstruction);
       if (value != null) {
         System.out.println("not null");
           this.variables.put(index, value);
       } else {
           this.variables.remove(index);
       }
   }

    private Number getInstructionValue(Instruction currentInstruction){
      try {
           if (currentInstruction instanceof LDC) {
               LDC ldc = (LDC) currentInstruction;
                Object maybeValue = ldc.getValue(this.cpg);
               if (maybeValue instanceof Number) {
                   return (Number) maybeValue;
               }
           }
           if (currentInstruction instanceof LDC2_W) {
               LDC2_W ldc2_w = (LDC2_W) currentInstruction;
               //if (extractArithmeticType(ldc2_w.getType(constPoolGen)) != ArithmeticType.OTHER) {
                   return ldc2_w.getValue(this.cpg);
               //}
           }
       } catch (Exception e) {
           System.out.println("Error");
            return null;
       }
       if (currentInstruction instanceof ConstantPushInstruction) {
           ConstantPushInstruction push = (ConstantPushInstruction) currentInstruction;
           return push.getValue();
       }
       return null;
     }

        /*
        if(instruction instanceof DSTORE){
          System.out.println("double");
        }

        else if(instruction instanceof FSTORE){
          System.out.println("float");
        }

        else if(instruction instanceof LSTORE){
          System.out.println("long");
        }

        else if(instruction instanceof ISTORE){
          System.out.println("int");
        }

        if(instruction instanceof StoreInstruction){
          System.out.println("store");
        }        else{
          System.out.println("can't get val");;
        }
      //  System.out.println("can't get val");
        //return null;
      if (instruction instanceof LDC) {
              System.out.println("ldc");
          LDC ldc = (LDC) instruction;
          Object value = ldc.getValue(cpg);
          if (value instanceof Number) {
              return (Number) value;
          }
      }
      if (instruction instanceof LDC2_W) {
              System.out.println("ldc2w");
          LDC2_W ldc2_w = (LDC2_W) instruction;
        //  if (extractArithmeticType(ldc2_w.getType(cpg)) != ArithmeticType.OTHER) {
        //      return ldc2_w.getValue(cpg);
        //  }
      }
      else if (instruction instanceof ConstantPushInstruction) {
            System.out.println("constant push");
        ConstantPushInstruction push = (ConstantPushInstruction) instruction;
        return push.getValue();
    }else {
      System.out.println("Could not extract constant!");
      return null;
  }

  return null;*/

    //}

    private void deleteVariable(InstructionList instructionList, InstructionHandle instructionHandle, InstructionHandle newHandle){
        try{
            instructionList.delete(instructionHandle);
        }
        catch (TargetLostException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            for(InstructionHandle target : e.getTargets()){
                for(InstructionTargeter targeter : target.getTargeters()){
                    targeter.updateTarget(target, newHandle);
                }
            }
        }
        instructionList.setPositions(true);
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
