
package coldjava;


public class Acrostic implements Protocol {
    static final String badExpression = "<b><h1><center>Bad expression.</center></h1></b>";
    
    public String doProtocol(String Uri){
        
        String expression = Uri.substring(Uri.indexOf(':') + 1, Uri.length());
        String[] nameChars = expression.split("\\");
        char letters[] = nameChars[0].toCharArray();
        
        
        StringBuilder sb = new StringBuilder();
        sb.append("<b><h1><center>");
        
        for(int i = 0; i < letters.length; i++){
            sb.append(makeName(letters[i]));
            sb.append("\n");
        }
        
        sb.append("</center></h1></b>");
        return sb.toString();
    }
    
    public String makeName(char c){
        
        char letter = Character.toLowerCase(c);
        int randNum = (int) (1 + (Math.random() * (2 - 1)));
        
        switch(letter){
            
            case 'a':
                if(randNum == 1){return "Awesome";}
                else return "Arrogant";
                            
            case 'b':
                if(randNum == 1){return "Brilliant";}
                else return "Bold";
                            
            case 'c':
                if(randNum == 1){return "Calculating";}
                else return "Careless";
                            
            case 'd':
                if(randNum == 1){return "Diligent";}
                else return "Direct";
                            
            case 'e':
                if(randNum == 1){return "Extravagent";}
                else return "Edgy";
                            
            case 'f':
                if(randNum == 1){return "Funky";}
                else return "Friendly";
                            
            case 'g':
                if(randNum == 1){return "Generous";}
                else return "Greedy";
                            
            case 'h':
                if(randNum == 1){return "Healthy";}
                else return "Haphazard";
                            
            case 'i':
                if(randNum == 1){return "Interesting";}
                else return "Illegible";
                            
            case 'j':
                if(randNum == 1){return "Joking";}
                else return "Jovial";
                            
            case 'k':
                if(randNum == 1){return "Kind";}
                else return "Knobby";
                            
            case 'l':
                if(randNum == 1){return "Legendary";}
                else return "Lethal";
                            
            case 'm':
                if(randNum == 1){return "Manly";}
                else return "Maniacal";
                            
            case 'n':
                if(randNum == 1){return "Nasal";}
                else return "Natural";
                            
            case 'o':
                if(randNum == 1){return "Optimistic";}
                else return "Odd";
                            
            case 'p':
                if(randNum == 1){return "Putrid";}
                else return "Patient";
                            
            case 'q':
                if(randNum == 1){return "Questionable";}
                else return "Quarrelsome";
                            
            case 'r':
                if(randNum == 1){return "Radical";}
                else return "Raucous";
                            
            case 's':
                if(randNum == 1){return "Sandy";}
                else return "Sensual";
                            
            case 't':
                if(randNum == 1){return "Terribad";}
                else return "Technical";
                            
            case 'u':
                if(randNum == 1){return "Underwhelming";}
                else return "Ultimate";
                            
            case 'v':
                if(randNum == 1){return "Vexatious";}
                else return "Vicious";
                            
            case 'w':
                if(randNum == 1){return "Whimsical";}
                else return "Wild";
                            
            case 'x':
                if(randNum == 1){return "Xenolithic";}
                else return "Xenophobic";
                            
            case 'y':
                if(randNum == 1){return "Yappy";}
                else return "Yeasty";
                            
            case 'z':
                if(randNum == 1){return "Zealous";}
                else return "Zonked";
                       
        }
        
        return "Invalid symbol";
    }
}
