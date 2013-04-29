package coldjavaserver;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.tools.Diagnostic;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Class [MyWebServer] <p> This is a simple web server, which only implements the GET-method.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version Feb. 2000
 */
public class MyWebServer extends GenericServer {
    public static final String configPath = "src/coldjavaserver/config.properties";
    static String documentRoot;
    static String indexfile = "index.html";
    Properties prop = new Properties();
    
    

    /**
     * The constructors
     */
    public MyWebServer(String documentRoot, int port) {

        super(port);
        
        this.documentRoot = documentRoot;
    }

    /*
     public MyWebServer() {
    
     super(80);
    
     this.documentRoot = ".";
     }
     */
    public MyWebServer() {
	super(23657);
	try {
     		prop.load(new FileInputStream(configPath));
     		documentRoot =  prop.getProperty("root");
            } catch (IOException ex) {
        	ex.printStackTrace();
        	documentRoot = "";
            }
    }

    /**
     * The method
     * <code>processConnection()</code> implements the functionality of the web server.
     */
    @Override
    protected void processConnection(Socket socket) {
        (new SocketThread(socket)).start();
    }

    /**
     * This thread processes a client (web browser) request. In the meantime the web server can accept other clients.
     */
    class SocketThread extends Thread {

        Socket socket = null;
        BufferedReader readFromNet = null;
        PrintStream writeToNet = null;
        String inputLine;
        String httpMethod;
        StringTokenizer tokenizer;
        String fileString;
        String protocolName;
        String className;
        String version;
        String contentType;
        File fileToServe;

        /**
         * The Constructor
         */
        SocketThread(Socket socket) {
            this.socket = socket;
        }

        /**
         * The method
         * <code>run()</code> is the core of the server
         */
        @Override
        public void run() {
            try {
                writeToNet = new PrintStream(socket.getOutputStream());
                readFromNet = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                inputLine = readFromNet.readLine();

                tokenizer = new StringTokenizer(inputLine);
                httpMethod = tokenizer.nextToken();

                if (httpMethod.equals("CLASS")) {
                    protocolName = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }
                    
                    //Skip the rest
                    while ((inputLine = readFromNet.readLine()) != null) {
                        if (inputLine.trim().equals("")) {
                            break;
                        }
                    }
                    
                    className = getFullyQualifiedClassName(protocolName);
                    if (className != null) {
                        //Corresponds to a class we have, let's go go go!
                        if (version.startsWith("HTTP/")) {
                            // Send a MIME header
                            writeToNet.print("HTTP/1.0 200 OK\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-length: " + className.length() + "\r\n");
                            writeToNet.print("Content-type: " + "text/plain" + "\r\n\r\n");
                        }
                        // Send the file
                        writeToNet.write(className.getBytes());
                        writeToNet.close();
                    }
                    else {
                        if (version.startsWith("HTTP/")) {
                        // send a MIME header
                        writeToNet.print("HTTP/1.0 501 Not Implemented\r\n");
                        writeToNet.print("Date: " + new Date() + "\r\n");
                        writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                        writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                    }

                    writeToNet.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                    writeToNet.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                    writeToNet.close();
                    }
                }
                else if (httpMethod.equals("UPLOAD")) {
                    System.out.println(inputLine);
                    
                    
                    StringTokenizer t = new StringTokenizer(inputLine);
                    t.nextToken();
                    String name = t.nextToken();
                    int length = -1;
                    int count = 0;
                    String protocol = null;
                    int c;
                    boolean inContent = false;
                    boolean newLine = false;
                    boolean doOnce = true;
                    ByteArrayOutputStream content = new ByteArrayOutputStream();
                    ByteArrayOutputStream header = new ByteArrayOutputStream();
                    
                    while ((c = readFromNet.read()) != -1) {
                        if (inContent) {
                            if(doOnce)
                            {
                        	System.out.println(header.toString());
                        	StringTokenizer ht = new StringTokenizer(header.toString());
                        	while(ht.hasMoreTokens())
                        	{
                        	   String s = ht.nextToken();
                        	   if(s.equals("Content-Length:"))
                        	   {
                        	       length = Integer.parseInt(ht.nextToken());
                        	   }else if(s.equals("protocol:"))
                        	   {
                        	       protocol = ht.nextToken();
                        	   }
                        	}
                        	doOnce = false;
                            }
                            if(++count >= length)
                        	break;
                            content.write(c);
                        } else {
                            header.write(c);
                            if (c == 10 && newLine) {
                                inContent = true;
                            } else if (c == 13) {
                                continue;
                            } else {
                                newLine = false;
                            }
                            if (c == 10) {
                                newLine = true;
                            }
                        }
                    }

                    PrintWriter out = new PrintWriter(documentRoot + "/"+ name.replace('.', '/') + ".java");
                    out.write(content.toString());
                    out.close();
                    
                    CompilationTask task = Compiler.makeCompilerTask(documentRoot + "/"+ name.replace('.', '/') + ".java");

                    System.out.println("Compiling ...");

                    if (!task.call()) {
                        System.out.println("Compilation failed");
                        writeToNet.print("HTTP/1.0 500 Internal Server Error\r\n");
                        writeToNet.println();
                        writeToNet.close();
                    }else {
                	 try {
                  		prop.load(new FileInputStream(configPath));
                  		prop.setProperty(protocol, name.replace('.', '/') + ".class");
                  		prop.store(new FileOutputStream(configPath), null);
                         } catch (IOException ex) {
                     		ex.printStackTrace();
                         }
                	
                	System.out.println("Responding OK");
                        writeToNet.print("HTTP/1.0 200 OK\r\n");
                        writeToNet.println();
                        writeToNet.close();
                    }

                    
                    
                }
                else if (httpMethod.equals("GET")) {
                    fileString = tokenizer.nextToken();
                    if (fileString.endsWith("/")) {
                        fileString += indexfile;
                    }
                    contentType = guessContentTypeFromName(fileString);

                    if (tokenizer.hasMoreTokens()) {
                        version = tokenizer.nextToken();
                    }

                    // Skip the rest
                    while ((inputLine = readFromNet.readLine()) != null) {
                        if (inputLine.trim().equals("")) {
                            break;
                        }
                    }

                    try {
                        System.err.println("FileString: " + "\"" + fileString + "\"");
                        fileToServe = new File(documentRoot, fileString.substring(1, fileString.length()));
                        FileInputStream fis = new FileInputStream(fileToServe);
                        byte[] theData = new byte[(int) fileToServe.length()];

                        fis.read(theData);
                        fis.close();

                        if (version.startsWith("HTTP/")) {
                            // Send a MIME header
                            writeToNet.print("HTTP/1.0 200 OK\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-length: " + theData.length + "\r\n");
                            writeToNet.print("Content-type: " + contentType + "\r\n\r\n");
                        }

                        // Send the file
                        writeToNet.write(theData);
                        writeToNet.close();
                        System.err.println("File: " + fileToServe + " sent\n");

                    } catch (IOException e) {
                        // Cannot find the file
                        if (version.startsWith("HTTP/")) {
                            // send a MIME header
                            writeToNet.print("HTTP/1.0 404 File Not Found\r\n");
                            writeToNet.print("Date: " + new Date() + "\r\n");
                            writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                            writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                        }
                        writeToNet.println("<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD>");
                        writeToNet.println("<BODY><H1>HTTP Error 404: File Not Found</H1></BODY></HTML>");
                        writeToNet.close();
                        System.err.println("File: " + fileToServe + " not found\n");
                    }
                } else {
                    System.out.println(inputLine);
                    // Method doesn't equal "GET" or "CLASS"
                    if (version.startsWith("HTTP/")) {
                        // send a MIME header
                        writeToNet.print("HTTP/1.0 501 Not Implemented\r\n");
                        writeToNet.print("Date: " + new Date() + "\r\n");
                        writeToNet.print("Server: MyWebServer Version Feb 2000\r\n");
                        writeToNet.print("Content-type: text/html" + "\r\n\r\n");
                    }

                    writeToNet.println("<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD>");
                    writeToNet.println("<BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>");
                    writeToNet.close();

                    System.err.println("Method: " + httpMethod + " is not supported\n");

                }
            } catch (IOException e) {
            }

            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        /**
         * The method
         * <code>guessContentTypeFromName()</code> returns the MIME-type of a
         * file, which is guessed from the file's extention.
         */
        public String guessContentTypeFromName(String name) {
            if (name.endsWith(".html") || name.endsWith(".htm")) {
                return "text/html";
            } else if (name.endsWith(".txt") || name.endsWith(".java")) {
                return "text/plain";
            } else if (name.endsWith(".gif")) {
                return "image/gif";
            } else if (name.endsWith(".class")) {
                return "application/octet-stream";
            } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "text/plain";
            }
        }
        
        public String getFullyQualifiedClassName(String protocol) {
            
            try {
     		prop.load(new FileInputStream(configPath));
     		return prop.getProperty(protocol);
            } catch (IOException ex) {
        	ex.printStackTrace();
        	return null;
            }
        
        }
    }
}
