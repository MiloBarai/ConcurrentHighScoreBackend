package com.test.concurrent.high.score.backend.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.test.concurrent.high.score.backend.controller.ServerController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.currentThread;

/**
 * HttpRequestHandler,
 * Responsible for handling and responding to all HTTP requests made to the server.
 */
public class HttpRequestHandler implements HttpHandler {
    private ServerController serverController;

    /*
     * TODO: Improvement:
     *   Put all URI-regexes into a hashmap with URI-regex to method, each time a request is received
     *   loop over each of keys in the map, when a match is found convert all the "(.*)" to arguments to input into the
     *   method that the key is pointing to.
     *   There might need to be some extra engineering when it comes to handling the key matching order, since
     *   a hashmap will have them unordered.
     */
    private String loginRegexURI = "/(.*)/login";
    private String postScoreRegexURI = "/(.*)/score";
    private String highScoreRegexURI = "/(.*)/highscorelist";

    private String sessionKeyParamKey = "sessionkey";

    public HttpRequestHandler(ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("Thread: "+currentThread().getName());
        String requestAddress = exchange.getRequestURI().getPath();
        System.out.println("Received call: " + requestAddress);

        //Block for GET requests
        if (exchange.getRequestMethod().equals("GET")) {

            //Path : "/ping"
            if (requestAddress.equals("/ping")) {
                handlePing(exchange);
            }

            // Path: "/<UserId>/login/"
            else if (requestAddress.matches(loginRegexURI)) {
                handleLogin(exchange, requestAddress);
            }

            // Path: "/<levelId>/highscorelist"
            else if(requestAddress.matches(highScoreRegexURI)){
                handleHighScoreList(exchange, requestAddress);
            }

            // No path for GET request
            else {
                handle404(exchange);
            }
        }

        //Block for Post Requests
        else if (exchange.getRequestMethod().equals("POST")) {

            // Path: "/<levelid>/score?sessionkey=<sessionkey>"
            if (requestAddress.matches(postScoreRegexURI)) {
                handleSetScore(exchange, requestAddress);
            }

            //No path for POST
            else {
                handle404(exchange);
            }
        } else {
            handle404(exchange);
        }

    }

    private void handleHighScoreList(HttpExchange exchange, String requestAddress) {
        List<String> arguments = getRegexURIArguments(highScoreRegexURI, requestAddress);
        if(arguments != null){
            int level = parseTo31bitUnsigned(arguments.get(0));
            if(level != -1){
                String scoreList = serverController.getLevelHighScoreList(level);
                response(exchange, 200, scoreList);
            } else{
                handle400(exchange, "invalid arguments provided");
            }

        } else {
            handle400(exchange, "no arguments provided");
        }
    }

    private void handle400(HttpExchange exchange, String msg) {
        response(exchange, 400, msg);
    }

    private void handle409(HttpExchange exchange, String msg) {
        response(exchange, 409, msg);
    }

    private void handle404(HttpExchange exchange) {
        response(exchange, 404, null);
    }

    private void handlePing(HttpExchange exchange) {
        response(exchange, 200, "ok");
    }

    private void handleLogin(HttpExchange exchange, String requestAddress) {
        List<String> arguments = getRegexURIArguments(loginRegexURI, requestAddress);

        if (arguments != null) {
            int userId = parseTo31bitUnsigned(arguments.get(0));
            if (userId != -1) {
                String sessionId = serverController.login(userId);
                response(exchange, 200, sessionId);
            } else //invalid user id
                handle400(exchange, "invalid userId");

        } else {//no user id
            handle400(exchange, "no userId");
        }
    }

    private void handleSetScore(HttpExchange exchange, String requestAddress) {
        int level = -1;
        List<String> arguments = getRegexURIArguments(loginRegexURI, requestAddress);

        try {
            int score = parseTo31bitUnsigned(getRequestBody(exchange));

            if (arguments != null)
                level = parseTo31bitUnsigned(arguments.get(0));

            if (level != -1 && score != -1) {
                Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

                if (serverController.validateSession(params.get(sessionKeyParamKey))) {
                    boolean succeeded = serverController.setUserScore(params.get(sessionKeyParamKey), level, score);
                    if (succeeded) {
                        response(exchange, 200, null);
                    } else {
                        response(exchange, 401, "expired session key");
                    }
                } else {
                    response(exchange, 401, "invalid or no session key provided as param");
                }
            } else {
                handle400(exchange, "invalid level or score");
            }
        } catch (IOException e) {
            System.out.println("Error reading body");
            e.printStackTrace();
            handle409(exchange, "unable to read body");
        }
    }

    private List<String> getRegexURIArguments(String regex, String URI) {
        var splitURI = URI.split("/");
        var splitRegex = regex.split("/");
        if (splitURI.length != splitRegex.length) {
            System.out.println("Regex and URI length does not match, unable to extract arguments");
            return null;
        }

        var result = new ArrayList<String>();

        for (int i = 0; i < splitRegex.length; i++) {
            if (splitRegex[i].equals("(.*)"))
                result.add(splitURI[i]);
        }
        return result;
    }

    private Map<String, String> queryToMap(String query) {
        HashMap<String, String> paramToVal = new HashMap<>();
        for (String param : query.split("&")) {
            String[] paramSplit = param.split("=");
            if (paramSplit.length > 1) {
                paramToVal.put(paramSplit[0], paramSplit[1]);
            } else {
                paramToVal.put(paramSplit[0], "");
            }
        }

        return paramToVal;
    }

    private int parseTo31bitUnsigned(String val) {
        int result = -1;
        if (val.matches("[0-9]+")) {
            try {
                result = Integer.parseInt(val);
            } catch (NumberFormatException ex) {
                System.out.println("Input String val: " + val + ", to large");
            }
        }

        return result;
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }

    private void response(HttpExchange exchange, int responseCode, String bodyResponse) {
        int bodyLength = -1;
        byte[] bodyResponseBytes = null;
        if (bodyResponse != null) {
            bodyResponseBytes = bodyResponse.getBytes();
            bodyLength = bodyResponseBytes.length;
        }
        try {
            String debugmsg = "Request: " + exchange.getRequestURI() + ", Response:  [code: " + responseCode + ", bodyLength: " + bodyLength;
            exchange.sendResponseHeaders(responseCode, bodyLength);
            if (bodyLength != -1) {
                var body = exchange.getResponseBody();
                body.write(bodyResponseBytes);
                body.flush();
                body.close();
                debugmsg += ", body: " + bodyResponse;
            }
            System.out.println(debugmsg + "]");
        } catch (IOException e) {
            /*
             * Catching exception to not down the server and printing the error for debugging purposes
             * All printing should be changed for logging.
             */
            System.out.println("Was unable to handle: " + exchange.getRequestURI());
            e.printStackTrace();
        }
    }
}
