package coldjava;

public class Arith implements Protocol {
    static final String badExpression = "<b><h1><center>Bad expression.</center></h1></b>";
    
    public String doProtocol(String Uri) {
        String expression = Uri.substring(Uri.indexOf(':') + 1, Uri.length());
        char operator;
        if (expression.indexOf('+') != -1) {
            operator = '+';
        }
        else if (expression.indexOf('-') != -1) {
            operator = '-';
        }
        else if (expression.indexOf('*') != -1) {
            operator = '*';
        }
        else if (expression.indexOf('/') != -1) {
            operator = '/';
        }
        else {
            return badExpression;
        }

        String[] operandStrings = expression.split("\\"+operator);
        if (operandStrings.length != 2 || operandStrings[0].length() < 1 || operandStrings[1].length() < 1) {
            return badExpression;
        }
        
        float operand1, operand2;
        try {
            operand1 = Float.valueOf(operandStrings[0]);
            operand2 = Float.valueOf(operandStrings[1]);
        }
        catch (NumberFormatException e) {
           return badExpression;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<b><h1><center>");

        switch(operator) {
            case '+':
                sb.append(operand1 + operand2);
                break;
            case '-':
                sb.append(operand1 - operand2);
                break;
            case '*':
                sb.append(operand1 * operand2);
                break;
            case '/':
                if (operand2 == 0.0) return "You divided by 0. The multiverse enters permanent heat death.";
                sb.append(operand1 / operand2);
                break;
        }

        sb.append("</center></h1></b>");
        return sb.toString();
    }
}
