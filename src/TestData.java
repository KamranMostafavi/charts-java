import java.util.ArrayList; 
/*
 * This class represents the data that is needed for charting - This data was in the csv files and is
 * read and stored in an object of this type.
 */
public class TestData {
  String chart;
  Integer id;
  String app;
  String encoding;
  String metric;
  String value;
  /**
   * array of ids - string concatenation / chart - 
   * static means that it is created once and shared by all instances
   */
  static ArrayList<String> chartIds = new ArrayList(); 
/**
 * array of charts which has the test data
 */
  static ArrayList<TestData> charts = new ArrayList();

  public TestData(Integer id, String app, String encoding, String metric, String value) {
    this.id = id;
    this.app = app;
    this.encoding = encoding;
    this.metric = metric;
    this.value = value;
    this.chart = app + encoding + metric;
    // if chart is not added to chartIds array, it is added
    if (TestData.chartIds.contains(this.chart)) {
        ; 
    } else {
      TestData.chartIds.add(this.chart); 
    }
  }
  
  public TestData(){ //this is the constructor if no parameters are passed.
  }
  /*
   * used only for testing purposes and printing to the console.
   */
  public static void printTest(TestData t) {
   
    System.out.println(
           t.chart + ","
           + t.id + ","
           + t.app + ","
           + t.encoding + ","
           + t.metric + ","
           + t.value
    );
   
  }
     
  /*
   * used for test purposes - prints the parameter to console
   */
  public static void printTestList(ArrayList<TestData> array) {
   
    for (TestData t: array) {
      printTest(t);
    }
   }
  
  /**
   * it returns the data for a chart as specified by input parameters
   * @param chart a string which is the concatenation of app,encoding and metric
   * @param id is the id of the csv file that has the chart data and is the same as the id of data.
   * @return an object of type testData which contains the data for a chart
   */
  public static TestData getTestData (String chart, Integer id) {
    TestData t = new TestData(); // just initialize
    for (Integer i = 0; i < TestData.charts.size(); i++) {
      if ((TestData.charts.get(i).chart.equals(chart)) 
          && (TestData.charts.get(i).id == id)) {
        t = TestData.charts.get(i);
        return t; 
      }
    }
    return t;
  
  }
     
  /*
   * for test purposes. Prints to console.
   */
  public static void groupTests(ArrayList<TestData> array) {
   
    for (TestData t: array) {
      printTest(t);
    }
  }
     
}
    
    