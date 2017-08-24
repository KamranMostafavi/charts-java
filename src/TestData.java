import java.util.ArrayList;    
public class TestData {
  String chart;
  Integer id;
  String app;
  String encoding;
  String metric;
  String value;
  //array of ids - string concatenation / chart - static means that it is created once and shared by all instances
  static ArrayList<String> chartIds = new ArrayList(); 
  static ArrayList<TestData> charts = new ArrayList(); //array of charts which has the test data
  
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
     
  public static void printTestList(ArrayList<TestData> array) {
   
    for (TestData t: array) {
      printTest(t);
    }
   }
  
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
      
  public static void groupTests(ArrayList<TestData> array) {
   
    for (TestData t: array) {
      printTest(t);
    }
  }
     
}
    
    