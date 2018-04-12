package comp207p.main;

import com.sun.tools.internal.jxc.ap.Const;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import javax.rmi.CORBA.Util;


/**
 * Created by yvettepinder on 12/04/2018.
 */
public class SimpleFolder extends Optimizer {


    public SimpleFolder(ClassGen gen, ConstantPoolGen constPoolGen) {
        super(gen, constPoolGen, DebugStage.Folding);
    }

    private boolean conversion(InstructionList listOfInstructions, InstructionHandle instructionHandle) throws TargetLostException {
        ConversionInstruction instruction = (ConversionInstruction) instructionHandle.getInstruction();
        Type typeOfInstruction = instruction.getType(this.constPoolGen);

        if (typeOfInstruction != Type.INT || typeOfInstruction != Type.LONG || typeOfInstruction != Type.FLOAT || typeOfInstruction != Type.DOUBLE) {
            return false;
        }

        InstructionHandle previousInstructionHandle = instructionHandle.getPrev();
        Instruction previousInstruction = previousInstructionHandle.getInstruction();
        Number valueOfInstruction = null;

        // find the value according to instance of instruction

        if (previousInstruction instanceof LDC) {
            LDC ldc = (LDC) previousInstruction;
            Object value = ldc.getValue(constPoolGen);
            if (value instanceof Number) {
                valueOfInstruction = (Number) value;
            }
        }
        if (previousInstruction instanceof ConstantPushInstruction) {
            ConstantPushInstruction push = (ConstantPushInstruction) previousInstruction;
            valueOfInstruction = push.getValue();
        }
        if (previousInstruction instanceof LDC2_W) {
            LDC2_W ldc2_w = (LDC2_W) previousInstruction;
            Type tempType = ldc2_w.getType(constPoolGen);

            if (tempType == Type.INT || tempType == Type.LONG || tempType == Type.DOUBLE || tempType == Type.FLOAT) {
                valueOfInstruction = ldc2_w.getValue(constPoolGen);
            }
        }

        if (valueOfInstruction == null) {
            return false;
        }


        Instruction instructionToPush = null;
        if (typeOfInstruction.equals(Type.INT)) {
            instructionToPush = new LDC(constPoolGen.addInteger(valueOfInstruction.intValue()));
        } else if (typeOfInstruction.equals(Type.FLOAT)) {
            instructionToPush = new LDC(constPoolGen.addFloat(valueOfInstruction.floatValue()));
        } else if (typeOfInstruction.equals(Type.LONG)) {
            instructionToPush = new LDC2_W(constPoolGen.addInteger(valueOfInstruction.longValue()));
        } else if (typeOfInstruction.equals(Type.DOUBLE)) {
            instructionToPush = new LDC2_W(constPoolGen.addInteger(valueOfInstruction.doubleValue()));
        } else {
            return false;
        }


        InstructionHandle replacementInstructionHandle = listOfInstructions.insert(instructionHandle, instructionToPush);
        listOfInstructions.delete(instructionHandle);
        listOfInstructions.setPositions(true);
        listOfInstructions.delete(previousInstructionHandle);
        listOfInstructions.setPositions(true);

      return true;
    }

    private boolean arithmetic(InstructionList listOfInstructions, InstructionHandle instructionHandle){
        ArithmeticInstruction instruction = (ArithmeticInstruction) instructionHandle.getInstruction();
        Type typeOfInstruction = instruction.getType(this.constPoolGen);

        if (typeOfInstruction != Type.INT || typeOfInstruction != Type.LONG || typeOfInstruction != Type.FLOAT || typeOfInstruction != Type.DOUBLE) {
            return false;
        }

        InstructionHandle previousInstructionHandle = instructionHandle.getPrev();
        InstructionHandle twoPreviousInstructionHandle = previousInstructionHandle.getPrev();

        Instruction previousInstruction = previousInstructionHandle.getInstruction();
        Instruction twoPreviousInstruction = twoPreviousInstructionHandle.getInstruction();

        Number firstNum = null;
        // find the value according to instance of instruction

        if (previousInstruction instanceof LDC) {
            LDC ldc = (LDC) previousInstruction;
            Object value = ldc.getValue(constPoolGen);
            if (value instanceof Number) {
                firstNum = (Number) value;
            }
        }
        if (previousInstruction instanceof ConstantPushInstruction) {
            ConstantPushInstruction push = (ConstantPushInstruction) previousInstruction;
            firstNum = push.getValue();
        }
        if (previousInstruction instanceof LDC2_W) {
            LDC2_W ldc2_w = (LDC2_W) previousInstruction;
            Type tempType = ldc2_w.getType(constPoolGen);

            if (tempType == Type.INT || tempType == Type.LONG || tempType == Type.DOUBLE || tempType == Type.FLOAT) {
                firstNum = ldc2_w.getValue(constPoolGen);
            }
        }

        if (firstNum == null) {
            return false;
        }



        Number secondNum = null;

        // find the value according to instance of instruction

        if (previousInstruction instanceof LDC) {
            LDC ldc = (LDC) previousInstruction;
            Object value = ldc.getValue(constPoolGen);
            if (value instanceof Number) {
                secondNum = (Number) value;
            }
        }
        if (previousInstruction instanceof ConstantPushInstruction) {
            ConstantPushInstruction push = (ConstantPushInstruction) previousInstruction;
            secondNum = push.getValue();
        }
        if (previousInstruction instanceof LDC2_W) {
            LDC2_W ldc2_w = (LDC2_W) previousInstruction;
            Type tempType = ldc2_w.getType(constPoolGen);

            if (tempType == Type.INT || tempType == Type.LONG || tempType == Type.DOUBLE || tempType == Type.FLOAT) {
                secondNum = ldc2_w.getValue(constPoolGen);
            }
        }

        if (secondNum == null) {
            return false;
        }

        Type typeOfArithmeticOperation = instruction.getType(constGenPool);
        int positionOfConstant = this.ArithmeticOperations(typeOfArithmeticOperation, typeOfInstruction, firstNum, secondNum);
        if(positionOfConstant == -1){ return false;}

        Instruction instruction2;


        if (typeOfInstruction == Type.DOUBLE || typeOfInstruction == Type.LONG) {
            instruction2 = new LDC2_W(positionOfConstant);
        } else {
            instruction2 = new LDC(positionOfConstant);
        }

        InstructionHandle handle2 = listOfInstructions.insert(instructionHandle, instruction2);

        listOfInstructions.delete(instructionHandle);
        listOfInstructions.setPositions(true);
        listOfInstructions.delete(previousInstructionHandle);
        listOfInstructions.setPositions(true);
        listOfInstructions.delete(twoPreviousInstructionHandle);
        listOfInstructions.setPositions(true);





    }




}
