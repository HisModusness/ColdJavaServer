package coldjava;

import java.util.Date;

public class Time implements Protocol{

    @Override
    public String doProtocol(String Uri) {
        Date currentDate = new Date();
        return currentDate.toString();
    }
    
}