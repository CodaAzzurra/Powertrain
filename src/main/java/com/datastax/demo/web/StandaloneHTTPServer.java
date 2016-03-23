package com.datastax.demo.web;

/**
 * Created by sebastianestevez on 3/14/16.
 */
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StandaloneHttpServer {
        public int port = 0;
        private HttpServer server;
        private int reqCount = 0;
        private String serverStatus = "";

        public Logger logger = null;
        public Logger accessLogger = null;

        public StandaloneHttpServer() {
            logger = Logger.getLogger("httpserver.log");
            accessLogger = Logger.getLogger("httpserveraccess.log");

            this.port = getFreePorts(15000, 15500, 1)[0];
            this.reqCount = 0;
            serverStatus = "ready";
        }

        public StandaloneHttpServer(Logger log) {
            logger = log;
            accessLogger = Logger.getLogger("httpserveraccess.log");

            this.port = getFreePorts(15000, 15500, 1)[0];
            this.reqCount = 0;
            serverStatus = "ready";
        }

        public StandaloneHttpServer(int port, int reqCount) {
            logger = Logger.getLogger("httpserver.log");
            accessLogger = Logger.getLogger("httpserveraccess.log");

            this.port = port;
            this.reqCount = reqCount;
            serverStatus = "ready";
        }

        public StandaloneHttpServer(int port, int reqCount, Logger log) {
            logger = log;
            accessLogger = Logger.getLogger("httpserveraccess.log");

            this.port = port;
            this.reqCount = reqCount;
            serverStatus = "ready";
        }

        public int getPort()
        {
            return port;
        }

        public void start() {
            logger.log(Level.ALL, "Starting http server. (" + port + ")");
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/", new FileHandler());
                server.setExecutor(null); // creates a default executor
                server.start();
                serverStatus = "started";
            } catch (Exception e) {
                serverStatus = "failed";
                logger.log(Level.ALL, "Initialization failed." + port, e);
            }
            logger.log(Level.ALL, "Started HttpServer:" + port);
            System.out.println("Started HttpServer:" + port);
        }

        public void stop() {
            server.stop(0);
            server = null;
            serverStatus = "stopped";
            logger.log(Level.ALL, "Stopped Server:" + port);
            System.out.println("Stopped Server:" + port);
        }

        public String getServerStatus() {
            return serverStatus;
        }

        public int requestCount() {
            int tempCount = reqCount;
            reqCount = 0;
            return tempCount;
        }


        class FileHandler implements HttpHandler {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();

            public void handle(HttpExchange t) throws IOException {
                String targetPath = t.getRequestURI().getPath();

                // Check if file exists
                File fileFolder = new File(".");
                File targetFile = new File(fileFolder, targetPath.replace('/',File.separatorChar));

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss z", Locale.getDefault());

                if (targetFile.exists()) {
                    // If it exists and it's a file, serve it
                    int bufLen = 10000 * 1024;
                    byte[] buf = new byte[bufLen];
                    int len = 0;
                    Headers responseHeaders = t.getResponseHeaders();

                    String mimeType = fileNameMap.getContentTypeFor(targetFile.toURI().toURL().toString());

                    if (targetFile.isDirectory() || targetFile.getName().endsWith(".html") || targetFile.getName().endsWith(".htm"))
                    {
                        mimeType = "text/html; charset=UTF-8";
                    }
                    else
                    {
                        mimeType = "text/plain; charset=UTF-8";
                    }

                    //responseHeaders.set("Content-Type", mimeType);
                    logger.log(Level.ALL, "Server Directory Listing:" + targetFile.getAbsolutePath());
                    accessLogger.log(Level.ALL, "Server Directory Listing:" + targetFile.getAbsolutePath());

                    if (targetFile.isFile()) {
                        t.sendResponseHeaders(200, targetFile.length());
                        FileInputStream fileIn = new FileInputStream(targetFile);
                        OutputStream out = t.getResponseBody();

                        while ((len = fileIn.read(buf, 0, bufLen)) != -1) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        fileIn.close();
                    } else if (targetFile.isDirectory()) {
                        File files[] = targetFile.listFiles();
                        StringBuffer sb = new StringBuffer();
                        sb.append("\n<html>");
                        sb.append("\n<head>");
                        sb.append("\n<style>");
                        sb.append("\n.datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #006699; -webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; }.datagrid table td, .datagrid table th { padding: 3px 10px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#ffffff; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00496B; border-left: 1px solid #E1EEF4;font-size: 12px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEF4; color: #00496B; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }.datagrid table tfoot td div { border-top: 1px solid #006699;background: #E1EEF4;} .datagrid table tfoot td { padding: 0; font-size: 12px } .datagrid table tfoot td div{ padding: 2px; }.datagrid table tfoot td ul { margin: 0; padding:0; list-style: none; text-align: right; }.datagrid table tfoot  li { display: inline; }.datagrid table tfoot li a { text-decoration: none; display: inline-block;  padding: 2px 8px; margin: 1px;color: #FFFFFF;border: 1px solid #006699;-webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; }.datagrid table tfoot ul.active, .datagrid table tfoot ul a:hover { text-decoration: none;border-color: #006699; color: #FFFFFF; background: none; background-color:#00557F;}div.dhtmlx_window_active, div.dhx_modal_cover_dv { position: fixed !important; }");
                        sb.append("\n</style>");
                        sb.append("\n<title>List of files/dirs under /scratch/mseelam/view_storage/mseelam_otd1/otd_test/./work</title>");
                        sb.append("\n</head>");
                        sb.append("\n<body>");
                        sb.append("\n<div class=\"datagrid\">");
                        sb.append("\n<table>");
                        sb.append("\n<caption>Directory Listing</caption>");
                        sb.append("\n<thead>");
                        sb.append("\n	<tr>");
                        sb.append("\n		<th>File</th>");
                        sb.append("\n		<th>Dir ?</th>");
                        sb.append("\n		<th>Size</th>");
                        sb.append("\n		<th>Date</th>");
                        sb.append("\n	</tr>");
                        sb.append("\n</thead>");
                        sb.append("\n<tfoot>");
                        sb.append("\n	<tr>");
                        sb.append("\n		<th>File</th>");
                        sb.append("\n		<th>Dir ?</th>");
                        sb.append("\n		<th>Size</th>");
                        sb.append("\n		<th>Date</th>");
                        sb.append("\n	</tr>");
                        sb.append("\n</tfoot>");
                        sb.append("\n<tbody>");

                        int numberOfFiles = files.length;

                        for (int i=0; i<numberOfFiles ; i++)
                        {
                            //System.out.println("In Work:" + f.getAbsolutePath());
                            if (i%2 ==0)
                                sb.append("\n\t<tr class='alt'>");
                            else
                                sb.append("\n\t<tr>");
                            if (files[i].isDirectory())
                                sb.append("\n\t\t<td><a href='" + targetPath + files[i].getName() + "/'>"+ files[i].getName() + "</a></td>" +
                                        "<td>Y</td>" + "<td>" + files[i].length() +
                                        "</td>" + "<td>" + formatter.format(new Date(files[i].lastModified())) + "</td>\n\t</tr>");
                            else
                                sb.append("\n\t\t<td><a href='" + targetPath + files[i].getName() + "'>"+ files[i].getName() + "</a></td>" +
                                        "<td> </td>" + "<td>" + files[i].length() +
                                        "</td>" + "<td>" + formatter.format(new Date(files[i].lastModified())) + "</td>\n\t</tr>");
                        }
                        sb.append("\n</tbody>");
                        sb.append("\n</table>");
                        sb.append("\n</div>");
                        sb.append("\n</body>");
                        sb.append("\n</html>");

                        t.sendResponseHeaders(200, sb.length());
                        OutputStream out = t.getResponseBody();
                        out.write(sb.toString().getBytes());
                        out.close();
                    }
                } else {
                    // If it doesn't exist, send error
                    String message = "404 Not Found " + targetFile.getAbsolutePath();
                    t.sendResponseHeaders(404, 0);
                    OutputStream out = t.getResponseBody();
                    out.write(message.getBytes());
                    out.close();
                }
            }
        }

        /**
         * Check if the current <b>port</b> is free ( Not bound by any process ).
         *
         * @param port The port that needs to be checked if free.
         * @return true if the port is free, false otherwise.
         */
        public static boolean isPortFree (int port)
        {
            ServerSocket socket = null;
            try
            {
                socket = new ServerSocket (port);
                socket.close();
            }
            catch (IOException e)
            {
                return false;
            }
            return true;
        }

        /**
         * To get the Freee ports.
         * @param rangeMin
         * @param rangeMax
         * @param count
         * @return
         */
        public static int[] getFreePorts (int rangeMin, int rangeMax, int count)
        {
            int currPortCount = 0;

            int port[] = new int [count];

            for (int currPort = rangeMin; currPortCount < count && currPort <= rangeMax; ++currPort)
            {
                if (isPortFree(currPort))
                    port[currPortCount++] = currPort;
            }

            if (currPortCount < count)
                throw new IllegalStateException ("Could not find " + count + " free ports to allocate within range " +
                        rangeMin + "-" + rangeMax + ".");

            return port;
        }

        public static void kickOff() {
            int port[] = getFreePorts(15000, 15100, 1);
            StandaloneHttpServer server = new StandaloneHttpServer();
            final String dir = "/game";
            System.out.println("Directory Listing is for:" + dir);
            Runnable task = () -> { server.start(); };
            new Thread(task).start();
        }

}


