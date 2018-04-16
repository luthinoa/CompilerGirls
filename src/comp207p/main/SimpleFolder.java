package comp207p.main;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.bcel.internal.generic.DCMPL;
import com.sun.tools.hat.internal.util.Comparer;
import com.sun.tools.internal.jxc.ap.Const;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.DADD;
import org.apache.bcel.generic.DCMPG;
import org.apache.bcel.generic.DCMPL;
import org.apache.bcel.generic.DCMPG;
import org.apache.bcel.generic.DCMPL;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.FADD;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LADD;
import org.apache.bcel.generic.LCMP;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;

import javax.rmi.CORBA.Util;


/**
 * Created by yvettepinder on 12/04/2018.
 */
public class SimpleFolder{

    protected ClassGen classGen;
    protected ConstantPoolGen constPoolGen;
    private Comparer comparer;


    public SimpleFolder(ClassGen classGen, ConstantPoolGen constPoolGen) {
        this.classGen = classGen;
        this.constPoolGen = constPoolGen;
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

    private boolean arithmetic(InstructionList listOfInstructions, InstructionHandle instructionHandle) throws TargetLostException {
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

        int positionOfConstant = this.ArithmeticOperations(instruction, typeOfInstruction, firstNum, secondNum);
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

        return true;
    }

    private int ArithmeticOperations(ArithmeticInstruction operationType,Type type, Number firstNum, Number secondNum){

        String nameOperation = operationType.getClass().getSimpleName();

        if(nameOperation.equals("ISUB") || operationType.equals("DSUB") || operationType.equals("LSUB") || operationType.equals("FSUB")){
            return subtraction(type, firstNum, secondNum);
        }


        if(nameOperation.equals("IADD") || operationType.equals("DADD") || operationType.equals("LADD") || operationType.equals("FADD")){
            return addition(type, firstNum,secondNum);
        }

        if(nameOperation.equals("IMUL") || operationType.equals("DMUL") || operationType.equals("LMUL") || operationType.equals("FMUL")){
            return multiplication(type,firstNum,secondNum);
        }

        if(nameOperation.equals("IDIV") || operationType.equals("DDIV") || operationType.equals("LDIV") || operationType.equals("FDIV")){
            return divison(type, firstNum,secondNum);
        }

        return -1;
    }

    private int addition(Type type, Number firstNum, Number secondNum) {
        int i = -1;

        if (type.equals(Type.INT)) {
            i = this.constPoolGen.addInteger((int)firstNum+(int)secondNum);
        }

        if (type.equals(Type.LONG)) {
            i = this.constPoolGen.addLong((long)firstNum+(long)secondNum);
        }

        if (type.equals(Type.FLOAT)) {
            i = this.constPoolGen.addLong((float)firstNum+(float)secondNum);
        }

        if (type.equals(Type.DOUBLE)) {
            i = this.constPoolGen.addDouble((double)firstNum+(double)secondNum);
        }
        return i;
    }

    private int subtraction(Type type, Number firstNum, Number secondNum) {
        int i = -1;

        if (type.equals(Type.INT)) {
            i = this.constPoolGen.addInteger((int)firstNum-(int)secondNum);
        }

        if (type.equals(Type.LONG)) {
            i = this.constPoolGen.addLong((long)firstNum-(long)secondNum);
        }

        if (type.equals(Type.FLOAT)) {
            i = this.constPoolGen.addLong((float)firstNum-(float)secondNum);
        }

        if (type.equals(Type.DOUBLE)) {
            i = this.constPoolGen.addDouble((double)firstNum-(double)secondNum);
        }
        return i;
    }

    private int multiplication(Type type, Number firstNum, Number secondNum) {
        int i = -1;

        if (type.equals(Type.INT)) {
            i = this.constPoolGen.addInteger((int)firstNum*(int)secondNum);
        }

        if (type.equals(Type.LONG)) {
            i = this.constPoolGen.addLong((long)firstNum*(long)secondNum);
        }

        if (type.equals(Type.FLOAT)) {
            i = this.constPoolGen.addLong((float)firstNum*(float)secondNum);
        }

        if (type.equals(Type.DOUBLE)) {
            i = this.constPoolGen.addDouble((double)firstNum*(double)secondNum);
        }
        return i;
    }

    private int divison(Type type, Number firstNum, Number secondNum) {
        int i = -1;

        if (type.equals(Type.INT)) {
            i = this.constPoolGen.addInteger((int)firstNum / (int)secondNum);
        }

        if (type.equals(Type.LONG)) {
            i = this.constPoolGen.addLong((long)firstNum / (long)secondNum);
        }

        if (type.equals(Type.FLOAT)) {
            i = this.constPoolGen.addLong((float)firstNum / (float)secondNum);
        }

        if (type.equals(Type.DOUBLE)) {
            i = this.constPoolGen.addDouble((double)firstNum / (double)secondNum);
        }
        return i;
    }

    private boolean ifInstruction(InstructionList instructionList, InstructionHandle instructionHandle) throws TargetLostException {

        IfInstruction ifInstruction = (IfInstruction) instructionHandle.getInstruction();
        String nameOfIfInstruction = ifInstruction.getClass().getSimpleName();

        if(!nameOfIfInstruction.equals("IF_ACMPEQ") || !nameOfIfInstruction.equals("IF_ICMPEQ") ||!nameOfIfInstruction.equals("IF_ACMPNE") ||!nameOfIfInstruction.equals("IF_ICMPGT") ||!nameOfIfInstruction.equals("IF_ICMPLE") ||!nameOfIfInstruction.equals("IF_ICMPLT") || !nameOfIfInstruction.equals("IFEQ")||!nameOfIfInstruction.equals("IFNE")||!nameOfIfInstruction.equals("IFGT")||!nameOfIfInstruction.equals("IFLE")||!nameOfIfInstruction.equals("IFLT")||!nameOfIfInstruction.equals("IFGE")||!nameOfIfInstruction.equals("IFNONNULL")||!nameOfIfInstruction.equals("IFNULL")) {
            return false;
        }

        Type firstNumType = null;
        Type secondNumType = null;

        InstructionHandle prevHandler = instructionHandle.getPrev();

        Instruction instruction = prevHandler.getInstruction();

        if ( instruction instanceof DCMPG || instruction instanceof org.apache.bcel.generic.DCMPL || instruction instanceof FCMPG || instruction instanceof FCMPL || instruction instanceof LCMP) {

            if(prevHandler.getInstruction() instanceof LCMP) {
                firstNumType = Type.LONG;
                secondNumType = Type.LONG;
            } else if (prevHandler.getInstruction() instanceof org.apache.bcel.generic.DCMPL
                    || prevHandler.getInstruction() instanceof DCMPG) {
                firstNumType = Type.DOUBLE;
                secondNumType = Type.DOUBLE;
            } else if (prevHandler.getInstruction() instanceof FCMPG
                    || prevHandler.getInstruction() instanceof FCMPL) {
                firstNumType = Type.FLOAT;
                secondNumType = Type.FLOAT;
            }
            prevHandler = prevHandler.getPrev();
        }

        InstructionHandle secondHandle = prevHandler.getPrev();
        Number number = null;

        Instruction tempInstruction = prevHandler.getInstruction();

        if (tempInstruction instanceof LDC) {
            LDC ldc = (LDC) tempInstruction;
            Object value = ldc.getValue(constPoolGen);
            if (value instanceof Number) {
                number = (Number) value;
            }
        }

        if (tempInstruction instanceof ConstantPushInstruction) {
            ConstantPushInstruction push = (ConstantPushInstruction) tempInstruction;
            number = push.getValue();
        }

        if (instruction instanceof LDC2_W) {
            LDC2_W ldc2_w = (LDC2_W) instruction;
            Type type = ldc2_w.getType(constPoolGen);
            if (type.equals(Type.INT) || type.equals(Type.LONG)||type.equals(Type.FLOAT)||type.equals(Type.DOUBLE)) {
                number = ldc2_w.getValue(constPoolGen);
            }
        }

        if (instruction instanceof ConstantPushInstruction) {
            ConstantPushInstruction push = (ConstantPushInstruction) instruction;
            number = push.getValue();
        }

        if (number == null) return false;
        firstNumType = firstNumType != null ? firstNumType : ((TypedInstruction) prevHandler.getInstruction()).getType(constPoolGen);
        Number num2 = null;
        if (nameOfIfInstruction.equals("EQUAL_ZERO") || nameOfIfInstruction.equals("NOT_EQUAL_ZERO") || nameOfIfInstruction.equals("GREATER_EQUAL_ZERO") || nameOfIfInstruction.equals("GREATER_ZERO") || nameOfIfInstruction.equals("LESS_EQUAL_ZERO") || nameOfIfInstruction.equals("LESS_ZERO")){
            secondNumType = null;
        } else {
            if (secondHandle == null) {
                return false;
            } else {
                Instruction tempInstruction2 = secondHandle.getInstruction();
                if (tempInstruction2 instanceof LDC) {
                    LDC ldc = (LDC) tempInstruction2;
                    Object value = ldc.getValue(constPoolGen);
                    if (value instanceof Number) {
                        num2 = (Number) value;
                    }
                }

                if (tempInstruction2 instanceof LDC2_W) {
                    LDC2_W ldc2_w = (LDC2_W) tempInstruction2;
                    Type typeOfInstruction = ldc2_w.getType(constPoolGen);
                    if (typeOfInstruction.equals(Type.INT) || typeOfInstruction.equals(Type.LONG)||typeOfInstruction.equals(Type.FLOAT)||typeOfInstruction.equals(Type.DOUBLE)) {
                        num2 = ldc2_w.getValue(constPoolGen);
                    }
                }

                if (tempInstruction2 instanceof ConstantPushInstruction) {
                    ConstantPushInstruction push = (ConstantPushInstruction) tempInstruction2;
                    num2 = push.getValue();
                }

                num2 = num2 != null ? num2 : ((TypedInstruction) secondHandle.getInstruction()).getType(constPoolGen);
                if(num2 == null) {
                    return false;
                }

                boolean response = Comparer.comparisonFunction(nameOfIfInstruction, firstNumType, secondNumType, number, num2);
                InstructionHandle maybeHandle = instructionHandle.getPrev();
                InstructionHandle jump = instruction.getTarget();
                InstructionHandle jumpWithElse = jumpTarget.getPrev();
                InstructionHandle newHandle;
                if (response) {
                    newHandle = instructionHandle.getNext();
                    instructionList.delete(newHandle);
                    instructionList.setPositions(true);
                    instructionList.delete(newHandle);
                    instructionList.setPositions(true);

                } else {
                    //look for goto, then delete everything from and including if to goto and including
                    instructionList.delete(instructionHandle);
                    instructionList.setPositions(true);
                    newHandle = jump;
                }

                return true;
            }
        }


    return false;





    }
}



















































