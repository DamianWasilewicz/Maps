package edu.brown.cs.student.logic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.brown.cs.student.maps.CheckinThread;
import edu.brown.cs.student.maps.UserCheckin;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;

import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static MapsHandler mapHandler = new MapsHandler();
  private static CheckinThread userCheckin;
  private static boolean bool = false;
  private static Repl repl;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    /* implemented: Process commands in a REPL. */
    repl = new Repl();
    this.loadCommands(repl);
    repl.runRepl();

  }

  private void loadCommands(Repl repl) {
    StarsHandler starsHandler = new StarsHandler();
    for (Map.Entry<String, Commandable> entry : starsHandler.getCommands()) {
      repl.addCommands(entry.getKey(), entry.getValue());
    }

    MockHandler mockHandler = new MockHandler();
    for (Map.Entry<String, Commandable> entry : mockHandler.getCommands()) {
      repl.addCommands(entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Commandable> entry : mapHandler.getCommands()) {
      repl.addCommands(entry.getKey(), entry.getValue());
    }
    repl.addMapHandler(mapHandler);
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    // Setup Spark Routes
    Spark.get("/stars", new FrontHandler(), freeMarker);
    Spark.post("/results", new SubmitHandler(), freeMarker);
    Spark.post("/route", new RouteHandler());
    Spark.post("/tile-ways", new TileHandler());
    Spark.post("/nearest", new NearestHandler());
    Spark.post("/checkin", new checkinHandler());
    Spark.post("/userCheckins", new userCheckinHandler());

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }
      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
  }

  /**
   * Handle requests to the front page of our Stars website.
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars Query", "message", ""); //map the variables to some object
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * A handler to print out suggestions once form is submitted.
   *
   * @return ModelAndView to render.
   * (autocorrect.ftl).
   */
  private static class SubmitHandler implements TemplateViewRoute {
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      StarsHandler handler = new StarsHandler();
      //load stars
      String filePath = qm.value("filepath");
      List<String> a = new ArrayList();
      a.add("stars");
      a.add(filePath);
      handler.getMap().get("stars").execute(a);
      //load commands
      String commandType = qm.value("command");
      String searchType = qm.value("search");
      List<String> arguments = new ArrayList<>();
      //index 0: command
      arguments.add(commandType);
      //index 1: r or k
      String rk = qm.value("rk");
      arguments.add(rk);
      //index 2: name or 2,3,4 coords
      if (searchType.equals("byName")) {
        String name = qm.value("nameField");
        arguments.add(name);
      } else if (searchType.equals("byCoords")) {
        String x = qm.value("x");
        String y = qm.value("y");
        String z = qm.value("z");
        arguments.add(x);
        arguments.add(z);
        arguments.add(y);
      }
      //execute
      handler.getMap().get(arguments.get(0)).execute(arguments);
      //print to gui
      ArrayList<Integer> outputStarsID = handler.getOutputStars();
      String returnable = "";
      for (int i = 0; i < outputStarsID.size(); i++) {
        if (i == 0) {
          returnable = returnable + outputStarsID.get(i);
        } else {
          returnable = returnable + "<br>" + outputStarsID.get(i);
        }
      }
      Map<String, String> variables = ImmutableMap
          .of("title", "Stars Query", "message", returnable);
      //connect to variables now
      return new ModelAndView(variables, "query.ftl");
    }

  }

  /**
   * Manages calls for route.
   */
  private static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      //add parsing logic
      List<String> arguments = new ArrayList<>();
      try {
        double sLat = data.getDouble("srclat");
        double sLon = data.getDouble("srclong");
        double dLat = data.getDouble("destlat");
        double dLon = data.getDouble("destlong");
        arguments.add("route");
        arguments.add(Double.toString(sLat));
        arguments.add(Double.toString(sLon));
        arguments.add(Double.toString(dLat));
        arguments.add(Double.toString(dLon));
      } catch (Exception e){
        String sLat = "\"" + data.getString("srclat") + "\"";
        String sLon = "\"" + data.getString("srclong") + "\"";
        String dLat = "\"" + data.getString("destlat") + "\"";
        String dLon = "\"" + data.getString("destlong") + "\"";
        arguments.add("route");
        arguments.add(sLat);
        arguments.add(sLon);
        arguments.add(dLat);
        arguments.add(dLon);
      }
//      Double[] src = {sLat, sLon};
//      Double[] dest = {dLat, dLon};
      //calling the method to run
      mapHandler.getMap().get("route").execute(arguments);
      //retrieving the newly populated array
//      List<String> route = mapHandler.getShortestPathWayIDs();
      List<List<Double>> route = mapHandler.getShortestPathCoords();

      //TODO: SEE getShortestPathCoords in mapsHandler
//      Double[] rand1 = {Math.random()*100, Math.random()*100};
//      Double[] rand2 = {Math.random()*100, Math.random()*100};
//      List<Double[]> coordinates = new ArrayList<>();
//      coordinates.add(src);
//      coordinates.add(dest);
//      coordinates.add(rand1);
//      coordinates.add(rand2);
      Map<String, Object> variables = ImmutableMap.of("route", route);
      return GSON.toJson(variables);
    }
  }

  /**
   *
   */
  private static class NearestHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double latCoord = data.getDouble("lat");
      double longCoord = data.getDouble("long");
      List<String> arguments = new ArrayList<>();
      arguments.add("nearest");
      arguments.add(Double.toString(latCoord));
      arguments.add(Double.toString(longCoord));
      mapHandler.getMap().get("nearest").execute(arguments);
      List<Double> nearestCoords = mapHandler.getNearestCoords();
      Map<String, Object> variables = ImmutableMap.of("nearest", nearestCoords);
      return GSON.toJson(variables);
    }
  }

  /**
   * Manages populating tiles.
   */
  private static class TileHandler implements Route {
    private static int counter = 0;

    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      double nwLat = data.getDouble("NWlat");
      double nwLong = data.getDouble("NWlong");
      double seLat = data.getDouble("SElat");
      double seLong = data.getDouble(("SElong"));
      List<Double> coords = new ArrayList<>();
      coords.add(nwLat);
      coords.add(nwLong);
      coords.add(seLat);
      coords.add(seLong);
      //TODO - Actually retrieve data for ways command from backend
      //Discuss best way of storing information about ways - nested array of coords of start and end
      //node coordinates

      List<List<List<String>>> ways = mapHandler.getWaysInTile(coords);
//      System.out.println("Reached post get ways");
//      System.out.println(ways);
      Map<String, Object> variables = ImmutableMap.of("way", ways);
//      System.out.println(variables.toString());
//      counter++;
//      System.out.println(counter);
      return GSON.toJson(variables);
    }
  }

   private static class userCheckinHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String userid = data.getString("userid");
      //make request from server
      List<UserCheckin> checkins = mapHandler.getDB().queryUserData(userid);
      //pass it as a map
      Map<String, Object> variables = ImmutableMap.of("userCheckins", checkins);
      System.out.println("requesting user checkins" + variables.size());
      return GSON.toJson(variables);
    }
  }

  private static class checkinHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      if (repl.isLoadedDB()){
        if(!bool){
          userCheckin = new CheckinThread(mapHandler.getDB());
          userCheckin.start();
          bool = true;
        }
      }
      //make request from server
      Map<Double, UserCheckin> checkins = repl.getRecentCheckin();
      //pass it as a map
      Map<String, Object> variables = ImmutableMap.of("checkin", checkins);
      return GSON.toJson(variables);
    }
  }


  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}

