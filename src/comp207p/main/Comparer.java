package comp207p.main;
import org.apache.bcel.generic.Type;



/**
 * Created by yvettepinder on 15/04/2018.
 */
public class Comparer {

    public static boolean comparisonFunction(
            String comparisonType,
            Type firstNumType,
            Type secondNumType,
            Number firstNum,
            Number secondNum){

        Type typeToCompare = null;

        if(firstNumType.equals(Type.DOUBLE) || secondNumType.equals(Type.DOUBLE) || firstNumType.equals(Type.FLOAT) || secondNumType.equals(Type.FLOAT)){
            if(firstNumType.equals(Type.DOUBLE) || secondNumType.equals(Type.DOUBLE)){
                typeToCompare = Type.DOUBLE;
            }
            else{
                typeToCompare = Type.FLOAT;
            }
        }
        else{
            if(firstNumType.equals(Type.LONG) || secondNumType.equals(Type.LONG)){
                typeToCompare = Type.LONG;
            }
            else{
                typeToCompare = Type.INT;
            }

        }



        if(comparisonType.equals("EQUAL")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() == secondNum.doubleValue();
                case "FLOAT":
                    return firstNum.floatValue() == secondNum.floatValue();
                case "INT":
                    return firstNum.intValue() == secondNum.intValue();
                case "LONG":
                    return firstNum.longValue() == secondNum.longValue();
            }
        }
        if(comparisonType.equals("NOT_EQUAL")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return !(firstNum.doubleValue() == secondNum.doubleValue());
                case "FLOAT":
                    return !(firstNum.floatValue() == secondNum.floatValue());
                case "INT":
                    return !(firstNum.intValue() == secondNum.intValue());
                case "LONG":
                    return !(firstNum.longValue() == secondNum.longValue());
            }
        }
        if(comparisonType.equals("EQUAL_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() == 0;
                case "FLOAT":
                    return firstNum.floatValue() == 0;
                case "INT":
                    return firstNum.intValue() == 0;
                case "LONG":
                    return firstNum.longValue() == 0;
            }
        }
        if(comparisonType.equals("NOT_EQUAL_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return !(firstNum.doubleValue() == 0);
                case "FLOAT":
                    return !(firstNum.floatValue() == 0);
                case "INT":
                    return !(firstNum.intValue() == 0);
                case "LONG":
                    return !(firstNum.longValue() == 0);
            }
        }
        if(comparisonType.equals("GREATER")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() > secondNum.doubleValue();
                case "FLOAT":
                    return firstNum.floatValue() > secondNum.floatValue();
                case "INT":
                    return firstNum.intValue() > secondNum.intValue();
                case "LONG":
                    return firstNum.longValue() > secondNum.longValue();
            }
        }
        if(comparisonType.equals("GREATER_EQUAL")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() >= secondNum.doubleValue();
                case "FLOAT":
                    return firstNum.floatValue() >= secondNum.floatValue();
                case "INT":
                    return firstNum.intValue() >= secondNum.intValue();
                case "LONG":
                    return firstNum.longValue() >= secondNum.longValue();
            }
        }
        if(comparisonType.equals("LESS")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() < secondNum.doubleValue();
                case "FLOAT":
                    return firstNum.floatValue() < secondNum.floatValue();
                case "INT":
                    return firstNum.intValue() < secondNum.intValue();
                case "LONG":
                    return firstNum.longValue() < secondNum.longValue();
            }
        }
        if(comparisonType.equals("LESS_EQUAL")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() <= secondNum.doubleValue();
                case "FLOAT":
                    return firstNum.floatValue() <= secondNum.floatValue();
                case "INT":
                    return firstNum.intValue() <= secondNum.intValue();
                case "LONG":
                    return firstNum.longValue() <= secondNum.longValue();
            }
        }
        if(comparisonType.equals("GREATER_EQUAL_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() >= 0;
                case "FLOAT":
                    return firstNum.floatValue() >= 0;
                case "INT":
                    return firstNum.intValue() >= 0;
                case "LONG":
                    return firstNum.longValue() >= 0;
            }
        }
        if(comparisonType.equals("GREATER_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() > 0;
                case "FLOAT":
                    return firstNum.floatValue() > 0;
                case "INT":
                    return firstNum.intValue() > 0;
                case "LONG":
                    return firstNum.longValue() > 0;
            }
        }
        if(comparisonType.equals("LESS_EQUAL_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() <= 0;
                case "FLOAT":
                    return firstNum.floatValue() <= 0;
                case "INT":
                    return firstNum.intValue() <= 0;
                case "LONG":
                    return firstNum.longValue() <= 0;
            }
        }
        if(comparisonType.equals("LESS_ZERO")){
            switch(typeToCompare.toString()){
                case "DOUBLE":
                    return firstNum.doubleValue() < 0;
                case "FLOAT":
                    return firstNum.floatValue() < 0;
                case "INT":
                    return firstNum.intValue() < 0;
                case "LONG":
                    return firstNum.longValue() < 0;
            }
        }
        return false;
    }


}
