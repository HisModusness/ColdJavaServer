/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coldjavaserver;

public class ColdJavaServer {

    public static void main(String[] args) {
        MyWebServer myWebServer;

        if (args.length == 2) {
            myWebServer = new MyWebServer(args[0], Integer.parseInt(args[1]));
        } else {
            myWebServer = new MyWebServer();
        }

        (new Thread(myWebServer)).start();
    }
}
