
/*
 * Author: Kamran Mostafavi
 * Mar 2017
 * 
 * This utility takes as its input a set of csv files (Max of 4) that contain identical metrics
 * for measuring performance. It uses a configuration file to determine which metrics to chart
 * and creates a html file (charts2.html) which charts the configured metric.
 * 
 * It uses com.csvreader to read the input csv files
 * It uses simple.json.*  to read the json configuration file
 * 
 * src directory structure is as follows:
 *      css
 *          *.css
 *      js
 *          *.js
 *      
 *      xtests/logs/
 *          *.csv
 *      
 *      Test_measure_perf_charts.java
 *      TestData.java
 *      configuration.json
 *      head.txt        - used to build the top portion of charts2.html
 *         
 * 
*/
import com.csvreader.CsvReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class TestMeasurePerfCharts {
  // some global variables.
  static String description;
  static List params = new ArrayList();
  static Map<String, Integer> apps = new HashMap<String, Integer>();
  static Map<String, Integer> metrics = new HashMap<String, Integer>();
  static Map<String, Integer> encodings = new HashMap<String, Integer>();

  public static String readFile(String fileName) {
    // reads a file and returns it in a string
    StringBuilder sb = new StringBuilder();
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static void writeFile(String filename, String str, boolean append) {
    try {
      FileWriter writer = new FileWriter(filename, append);
      writer.write(str);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static Map clean(Map m1) {
    // only keep apps, metrics, encodings that are set to 1
    Map m2 = new HashMap();

    for (Object s1 : m1.keySet()) {
      if (Integer.parseInt(m1.get(s1).toString()) == 1) {
        m2.put(s1, m1.get(s1));
      }
    }
    return m2;
  }

  public static void addtests(Integer id, String filename) {
    // read csv test file and create the data structures for charting
    try {

      CsvReader data = new CsvReader(filename);

      data.readHeaders();
      System.out.println(id + "," + filename);

      while (data.readRecord()) {
        // while records remain to be read - data
        // contains row read
        if (apps.containsKey(data.get("Test Command")) 
            /* only read apps and encodings that are set in configuration file.*/
            && encodings.containsKey(data.get("Encoding"))) {
          System.out.print(data.get("Test Command") + "," + data.get("Encoding"));
          for (Object s1 : metrics.keySet()) {
            if (data.getIndex(s1.toString()) != -1) { 
              /*only read metrics that are set in the configuration file */

              // create a chart object and add it to charts
              // (array) to be used later for creating the
              // javascript charts
              // TestData = id, app, encodings, metric, value
              TestData t = new TestData(id, data.get("Test Command"), data.get("Encoding"), 
                      s1.toString(), data.get(s1.toString()));
              TestData.charts.add(t);
              System.out.print("," + data.get(s1.toString()));
            }
          }
          System.out.println();
        }

      }

      data.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void printMap(Map mp) {
    // print a HashMap
    Iterator it = mp.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      System.out.println(pair.getKey() + " = " + pair.getValue());
      it.remove(); // avoids a ConcurrentModificationException
    }
  }

  public static void init_config(String filename) {
    // read the configuration file and initialize charting data structures

    String s = readFile(filename);
    try {
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(s);

      // get String variables from the JSON object
      description = (String) jsonObject.get("description");

      // get HashMaps

      apps = (Map) jsonObject.get("apps"); // returns null if not found
      metrics = (Map) jsonObject.get("metrics");
      encodings = (Map) jsonObject.get("encodings");
      params = (List) jsonObject.get("params");

      // only keep items of interest for data collection - filter out
      // unwanted data collection
      apps = clean(apps);
      metrics = clean(metrics);
      encodings = clean(encodings);

      // Map m=(Map) params.get(1);

      // System.out.print(m.get("display"));
      System.out.println();

      System.out.println(apps);
      System.out.println(metrics);
      System.out.println(encodings);
      System.out.println(params);

    } catch (ParseException pe) {
      System.out.println("position: " + pe.getPosition());
      System.out.println(pe);
    }
  }

  public static void write_html(String filename) {
    // create the chart.html file
    // build the head of the html file
    String head = readFile("src/head.txt");

    // see if there are any charts to display

    // chartIds is an array of ids for charts that are to be
    // displayed/built.
    if (TestData.chartIds.isEmpty()) {
      System.out.println("There are no charts to display");
      return;
    } else { // display number of charts to build
      System.out.println("There are " + TestData.chartIds.size() + " charts to build!");
    }

    /*
     * 
     * var options = {canvas:true, grid: {margin: {top:50}, hoverable:
     * true}, series: {bars: {show: true, barWidth: 0.08}}, xaxis: {mode:
     * "categories", tickLength: 0, min: -0.3, max: 1}, colors: ["#cc0000",
     * "#787A40", "#9FBF8C", "#C8AB65", "#D4CBC3"]}; var e0_jpeg_1 =
     * [["deluxe", 14765028.0], ]; var e0_jpeg_2 = [["deluxe", 12718787.0],
     * ]; var d0_jpeg = [{label: "run 1", data: e0_jpeg_1,
     * bars:{order:0}},{label: "run 2", data: e0_jpeg_2, bars:{order:1}}];
     * var e1_jpeg_1 = [["deluxe", 11333003.0], ]; var e1_jpeg_2 =
     * [["deluxe", 9623829.0], ]; var d1_jpeg = [{label: "run 1", data:
     * e1_jpeg_1, bars:{order:0}},{label: "run 2", data: e1_jpeg_2,
     * bars:{order:1}}]; $(function() { var plot0 =
     * $.plot($("#placeholder_0_0"), d0_jpeg, options); var plot1 =
     * $.plot($("#placeholder_1_0"), d1_jpeg, options); set_title(0,
     * "Encoding Pixels/s ( jpeg )"); set_title(1,
     * "Pixels/s Sent ( jpeg )"); $("#metric_link_0").click(function()
     * {$("#metric_list").scrollTop(400.0*0);});$("#metric_link_1").click(
     * function() {$("#metric_list").scrollTop(400.0*1);}); }); </script>
     * <style>.metric_box {height: 400.0px}</style> </head> <body> <div
     * id="page"> <div id="header_box"> <div id="header"> <h2>Xpra
     * Performance Results</h2> <h3>Comparison between two test runs.</h3>
     * <div id="help_text">Click a metric to locate it in the results.</div>
     * </div> <div id="select_box"> <div id="metric_link_0"
     * style="float:left;height:20px;width:200px"><a href="#">Encoding
     * Pixels/s</a></div> <div id="metric_link_1"
     * style="float:left;height:20px;width:200px"><a href="#">Pixels/s
     * Sent</a></div> </div> </div> <div style="clear:both"></div> <div
     * id="metric_list"> <div class="metric_box" id="metric_box_0"> <div
     * class="metric_label">Encoding Pixels/s</div> <div class="container">
     * <div id="placeholder_0_0" class="placeholder"></div> </div> </div>
     * <div class="metric_box" id="metric_box_1"> <div
     * class="metric_label">Pixels/s Sent</div> <div class="container"> <div
     * id="placeholder_1_0" class="placeholder"></div> </div> </div> <div
     * class="metric_box"></div>
     */

    // This builds the javascript code block to display the charts
    // build the var options string
    String var = "var options = {canvas:true, grid: {margin: {top:50}, hoverable: true},"
        + " series: {bars: {show: true, barWidth: 0.08}},  xaxis: {mode: \"categories\","
        + " tickLength: 0, min: -0.3, max: 1},"
        + " colors: [\"#cc0000\", \"#787A40\", \"#9FBF8C\", \"#C8AB65\", \"#D4CBC3\"]};\n";
    System.out.println(var);

    /*
    * build chart specific variable var e0_jpeg_1 = [["deluxe",
    * 14765028.0], ]; var e0_jpeg_2 = [["deluxe", 12718787.0], ]; var a =
    * [[ "b", "c"], ];
    */

    int numOfBars = params.size();
    int numOfCharts = TestData.chartIds.size();

    System.out.println("number of bars per chart " + numOfBars);
    System.out.println("number of charts " + numOfCharts);

    Integer fileNum = 1;
    TestData test = new TestData(); // no parameters given - just used as a
    // pointer to charts objects
    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      System.out.println("Creating Scripts for chart " + TestData.chartIds.get(i));
      for (fileNum = 1; fileNum <= numOfBars; fileNum++) {
        test = TestData.getTestData(TestData.chartIds.get(i), fileNum);
        System.out.println("Grouped Test " + test.chart + " " + test.id);
        var = var + "var e" + i + "_" + test.encoding + "_" + fileNum + " = " + "[[" + "\"" 
          + test.app + "\"" + ", " + test.value + "], ];\n";
      }
      /*
    * var d0_jpeg = [{label: "run 1", data: e0_jpeg_1,
    * bars:{order:0}},{label: "run 2", data: e0_jpeg_2,
    * bars:{order:1}}];
    */

      var = var + "var d" + i + "_" + test.encoding + " = [";
      for (fileNum = 1; fileNum <= numOfBars; fileNum++) {
        Map m = (Map) params.get(fileNum - 1);
        var = var + "{label: " + "\"" + m.get("display") + "\", data: e" + i + "_" + test.encoding 
                + "_" + fileNum + ", bars:{order:" + (fileNum - 1) + "}}";
        if (fileNum < numOfBars) {
          var = var + ",";
        }
      }
      var = var + "];\n";
    }
    /*
    * $(function() { var plot0 = $.plot($("#placeholder_0_0"), d0_jpeg,
    * options); var plot1 = $.plot($("#placeholder_1_0"), d1_jpeg,
    * options); set_title(0, "Encoding Pixels/s ( jpeg )"); set_title(1,
    * "Pixels/s Sent ( jpeg )");
    */
    var = var + "$(function() {\n";

    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      test = TestData.getTestData(TestData.chartIds.get(i), 1);
      var = var + "\tvar plot" + i + " = " + "$.plot($(\"#placeholder_" + i + "_0\")," + "d" 
        + i + "_" + test.encoding + ", options);\n";
    }

    /*
    * set_title(0, "Encoding Pixels/s ( jpeg )"); set_title(1,
    * "Pixels/s Sent ( jpeg )");
    */
    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      test = TestData.getTestData(TestData.chartIds.get(i), 1);
      var = var + "\tset_title(" + i + ", " + "\"" + test.metric 
              + " (" + test.encoding + ")\"" + ");\n";
    }

    /*
    * $("#metric_link_0").click(function()
    * {$("#metric_list").scrollTop(400.0*0);});
    * $("#metric_link_1").click(function()
    * {$("#metric_list").scrollTop(400.0*1);});
    * 
    * });
    */
    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      test = TestData.getTestData(TestData.chartIds.get(i), 1);
      var = var + "\t$(\"#metric_link_" + i 
        + "\").click(function() {$(\"#metric_list\").scrollTop(400.0*"
        + i + ");});\n";
    }
    var = var + "});";

    var = var + "</script>\n" + "<style>.metric_box {height: 400.0px}</style>\n" 
      + "</head> \n" + "<body> \n" + "<div id=\"page\">\n" 
      + "\t<div id=\"header_box\">\n" + "\t\t<div id=\"header\">\n"
      + "\t\t\t<h2>Xpra Performance Results</h2>\n" 
      + "\t\t\t<h3>Comparison between two test runs.</h3>\n"
      + "\t\t\t<div id=\"help_text\">Click a metric to locate it in the results.</div>\n" 
      + "\t\t</div>\n" + "\t\t<div id=\"select_box\">\n";

    /*
    * 
    * <div id="metric_link_0" style="float:left;height:20px;width:200px"><a
    * href="#">Encoding Pixels/s</a></div> <div id="metric_link_1"
    * style="float:left;height:20px;width:200px"><a href="#">Pixels/s
    * Sent</a></div>
    * 
    */
    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      test = TestData.getTestData(TestData.chartIds.get(i), 1);
      var = var + "\t\t\t<div id=\"metric_link_" + i
        + "\" style=\"float:left;height:20px;width:200px\"><a href=\"#\">" + test.metric + " ("
        + test.encoding + ")</a></div>\n";
    }
    var = var + "\t\t</div>\n" + "\t</div>\n" + "\t<div style=\"clear:both\"></div>\n"
       + "\t<div id=\"metric_list\">\n";

    /*
    * <div class="metric_box" id="metric_box_0"> <div
    * class="metric_label">Encoding Pixels/s</div> <div class="container">
    * <div id="placeholder_0_0" class="placeholder"></div> </div> </div>
    * 
    * <div class="metric_box" id="metric_box_1"> <div
    * class="metric_label">Pixels/s Sent</div> <div class="container"> <div
    * id="placeholder_1_0" class="placeholder"></div> </div> </div>
    * 
    * <div class="metric_box"></div>
    */
    for (Integer i = 0; i < TestData.chartIds.size(); i++) {
      test = TestData.getTestData(TestData.chartIds.get(i), 1);
      var = var + "\t<div class=\"metric_box\" id=\"metric_box_" + i + "\">\n"
        + "\t\t<div class=\"metric_label\">" + test.metric + "</div>\n" 
        + "\t\t<div class=\"container\">\n"
        + "\t\t\t<div id=\"placeholder_" + i + "_0\" class=\"placeholder\"></div>\n"
        + "\t\t</div>\n\t</div>\n";
    }

    var = var + "<div class=\"metric_box\"></div>\n" + "</div>\n" + "</div>\n" 
      + "</body>\n" + "</html>\n";

    System.out.print(var);
    writeFile(filename, head + var, false);
  }

  public static void main(String[] args) {
    /*
         The files this generator acts upon are the CSV files output
         from one or more runs of test_measure_perf.py.
        
         Data file naming convention: prefix_id_rep.csv
        
         This script takes no arguments. When it's run, it will
         produce an HTML file called "test_perf_charts.html".
        
         Open that file in your browser to see the charts.
    */ 

    // read charting config file - what to chart
    init_config("src\\configuration.json");

    // read the input csv files and build the charting data structures
    for (Object el : params) {
      Map m = (Map) el;
      String filename = m.get("file").toString();
      Integer id = Integer.parseInt(m.get("id").toString());
      addtests(id, filename);
    }

    System.out.println(TestData.chartIds);
    TestData.printTestList(TestData.charts);

    write_html("src/charts2.html");

  }
}
