EasyTest Core Module: A Data Driven Testing approach to JUnit
------------------------------------------------------------------------------------------------------
An updated version of EasyTest Core(1.2.3) module is now available in [Maven Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.easetech%22%20AND%20a%3A%22easytest-core%22)

What's new in Version 1.2.3
---------------------------
A new annotation [Duration](https://github.com/EaseTech/easytest-core/blob/master/src/main/java/org/easetech/easytest/annotation/Duration.java) is introduced.
This annotation is introduced to capture the time taken by the method under test and assert it with the user specified maximum time.
Thus a user can now say that the test should fail if the method it is trying to test takes more than "x"milliseconds.
<br>Let's look at an example

        public class TestDuration {
        
        @Duration(timeInMillis=20)
        ItemService testSubject;
        
        @Test
        public Item findItemTest(@Param(name='itemId')String itemId) {
        Item result = testSubject.findItem(itemId)
          Assert.notNull(result);
          return result;
        }
        
In the above case, if the method <B>findItem</B> of class <B>ItemService</B> took more than 20 milli seconds, then the test method 
will fail specifying that the method took more time than expected.

A second use of this annotation is if a user wants to override the value of <B>timeInMillis</B> attribute of Duration annotation for a specific Test. In such a case,
he can specify the Duration annotation at the Test Method level and EasyTest will override the value of timeInMillis only for that test.
Lets look at an example :
   
        public class TestDuration {
        
        @Duration(timeInMillis=20)
        ItemService testSubject;
        
        @Test
        public Item findItemTest(@Param(name='itemId')String itemId) {
        Item result = testSubject.findItem(itemId)
          Assert.notNull(result);
          return result;
          
        @Test
        @Duration(timeInMillis=50 , forClass=ItemService.class)
        public List<Item> getItemsTest(@Param(name='itemType')String itemType) {
        List<Item> result = testSubject.getItems(itemType)
          Assert.notNull(result);
          return result;
        }
        
In the above case, we are telling EasyTest that method <B>getItems</B> of class <B>ItemService</B> should not take more than 50 milliseconds
when run inside the test method with name <B>getItemsTest</B>. 

In order to get the complete picture, have a look at the Java docs of [Duration](https://github.com/EaseTech/easytest-core/blob/master/src/main/java/org/easetech/easytest/annotation/Duration.java) annotation.

What's changed in Version 1.2.2
-------------------------------
* A user can now specify a variable value as part of the DataLoader's filePaths attribute.
Thus it is now possible to use DataLoader annotation like this :

          @DataLoader(filePaths = {"${my.data.file}" , "${my.second.data.file}"})
    
Using the above way, a user can specify properties of the above variables "my.data.file" and "my.second.data.file" as System property using -D option of Java System Properties.

* A new System Property <B>"testDataFiles"</B> to provide a comma separated list of input test data files at runtime.
In order to use this option simply specify @DataLoader annotation at the top of your class without any input data. 
Thus in such a case DataLoader annotation acts as a marker annotation telling the EasyTest 
system that it has to fetch the value of filePaths attribute from the system property "testDataFiles".

* <B>NOTE</B> If a user has specified both <B>"testDataFiles" System Property AND a value for "dataFiles" attribute</B>, then the System Property files(specified using testDataFiles System Property) 
will override the files specified using the "dataFiles" attribute of DataLoader annotation.

What's changed in Version 1.2.1
------------------------------
Besides regular clean up stuff, one of the important things that changed in 1.2.1 is the way Test methods are now instantiated and their data handled.
Until version 1.2, all the test methods were running in a single test class instance, which, normally was not a problem, but caused
some concern with JUnit Rules, especially with Rules that depended on a new instance for each test method (ErrorCollector for eg.)
With 1.2.1 that has changed and each test method now runs in its own test instance.

You can download the latest version of EasyTest Core from [Maven Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.easetech%22%20AND%20a%3A%22easytest-core%22)  

Whats new in Version 1.2
------------------------
* Run Your tests in Parallel using @Parallel annotation. Heres an example :

        @RunWith(DataDrivenTestRunner.class)
        @DataLoader(filePaths = { "getItemsData.csv" })
        @Parallel(threads=2)
        public class TestConditionsSupportedByEasyTestRunner {

        
         @Test
         public void testGetItems(@Param(name="inputData")
        Map<String, String> inputData) {
            System.out.println("library Id : " + inputData.get("LibraryId") + " and item type : "
                + inputData.get("itemType") + " and search text array :" + inputData.get("searchText"));

         }
    
        @Test
        public void testAnotherItem(@Param(name="inputData") 
          Map<String, String> inputData) {
               // your test condition

          }
         }
    
Note the annotation @Parallel at the class level. This annotation is all you need to run your tests in Parallel.

* The code is a lot cleaner and is known to support all the known features of JUnit.

Download EasyTest simply by including the latest version of easytest-core and easytest-spring modules from Maven in your pom file.
   
        <groupId>org.easetech</groupId>
        
        <artifactId>easytest-core</artifactId>
        
        <version>1.2</version>
        
And for Spring module simply include :

        <groupId>org.easetech</groupId>
        
        <artifactId>easytest-spring</artifactId>
        
        <version>1.1</version>
        
EasyTest is a framework that lets you write Data Driven Tests using simple and intuitive annotations.
But it does not just provide you the ability to write Data Driven Tests. 
Instead it provides you with the facility to write sleek tests that are maintainable, easy to write and can be generated automatically using easytest-codegen library.


<br>For a more detailed and up to date introduction, look at the wiki page: https://github.com/EaseTech/easytest/wiki/EasyTest-:-An-Introduction<br>
For using EasyTest in your project, look at the WIKI page : https://github.com/EaseTech/easytest/wiki/EasyTest-:-Getting-Started-%7C-Maven-Dependency
Here are some quick links:

#### [EasyTest Introduction](https://github.com/EaseTech/easytest/wiki/EasyTest-:-An-Introduction) 
#### [Load data using CSV](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Loading-Data-using-CSV-file)
#### [Load data using XML](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Loading-Data-using-XML)
#### [Load data using Excel](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Loading-Data-using-Excel)
#### [EasyTest Spring Support](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Spring-Integration)
#### [Intercepting method calls](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Understanding-@Intercept-and-@SpringIntercept-annotation)
#### [@Param annotation and passing parameters to test method](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Understanding-@Param-annotation)
#### [Complex objects as parameters and Converters support](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Understanding-Converters-Support)
#### [Property Editors Support](https://github.com/EaseTech/easytest/wiki/EasyTest-:-Understanding-Property-Editor-Support)
####[JSON Support in EasyTest](https://github.com/EaseTech/easytest-core/wiki/Passing-JSON-Object-in-test-data)

Introduction:
-------------
The EasyTest Core Module is built as an extension of Junit, and has taken the approach of providing test data to the test classes/methods at the whole new level. 
to how the test data is provided to the test method by the JUnit Runner.

Before describing the changes proposed in this repository, let us walk through what JUnit provides us for performing Data Driven Testing.
JUnit, in its experimental package provides us two options:

1) Parameterized Runner, in which we provide test data to the test method with @Parameters annotation

2) Theories Runner, in which we provide test data to the test method using either @DataPpoint(s) annotations 
or by using @ParametersSuppliedBy and DataSupplier extension.

Both of the above approach requires the user to write boilerplate code in their test classes. Even though the data now resides outside the test case,
it still is coupled with the test class. Finally, the ease of use that JUnit has been synonymous with for so long appears to be missing in the above experimental Runners.

You can find the detailed examples of Parameterized Runner and its limitations here:
http://www.kumaranuj.com/2012/08/junits-parameterized-runner-and-data.html

and for Theories runner here :
http://www.kumaranuj.com/2012/08/junit-theories-and-data-driven-testing.html

All this and more, inspired me to write a test framework that is simple to use, is flexible in its approach and can be extended by the user in a consistent manner.
Finally I wanted to bring back the same ease of use to the testing world, like we had few years ago(annotate methods with @Test and relax).

What this code base consists of:
---------------------------------

This code base consists of :

1) A customized JUnit Runner that gives its user ability to provide test data in a consistent and user controlled manner. It is called DataDrivenTestRunner. 
This Runner works on our favorite annotation @Test from JUnit and also supports passing parameters to the test method. And this is not its only selling point.

2) DataDrivenTestRUNNER gives its users the ability to inspect the testMethod and its associated test data in the IDE. 
For example, when a user runs the test method with name : getTestData with the following test data:

<B> "libraryId=1 and itemId=2"</B>
 
 <B> "libraryId=2456 and itemId=789"</B>
 
 then, DataDrivenTest, will provide the details of the executing test method in the JUnit supported IDEs like this: 
 
 <B>getTestData{libraryId=1 ,itemId=2}
 
 <B>getTestData{libraryId=2456 ,itemId=789}</B>
 
 NOTE: In case the user has simple test methods(without parameters), DataDrivenTest runner supports that implicitly.
 
3) A Data Loading Strategy consisting of interface Loader and classes LoaderFactory and CSVDataLoader and an Enum LoaderType. 
EasyTest supports four different ways for the user to load Data:
  + CSV
  + XECEL
  + XML
  + CUSTOM
  
  CSV, EXCE,XML data loader implementation is already available and the users can use it out of the box.
  To give an example of the design, CSVDataLoader is an implementation of Loader interface and provides a mechanism to load test data from a CSV file.
LoaderFactory is a Factory class that is responsible for returning the right type of Loader based on the loaderType.

3) Param annotation that is an extension of ParametersSuppliedBy annotation and provides a lot of useful features to its user. Some of them include:
 + A mechanism to provide custom Objects to the test method. For eg. if a test method requires a user defined object LibraryId, then the Param annotation 
 can automatically convert the string data(provided in the CSV file) to the LibraryId Object.This is based on Java RegistryEditorsSupport. In case the standard PropertyEditor find mechanism does not apply to your project, 
 you can always register your own custom editors in your test class and the Framework will take care of the rest. For example look in the test package at LibraryId and LibraryIdEditor.
 
 + Another way to provide custom objects to the test method is by using ConverterManager and AbstractConverter. 
 A user can provide its own implementation of converting a Map (containing the key value pair) to an object that is expected by the test method and the extension framework will take care of the rest.
 See <B>CASE 4 </B>below 

4) DataLoader annotation to be used by the user in the test to provide information about the test data like:
   + The list of files from which to load the input test data. This is a OPTIONAL field whose type is a String[]</li>
   
   + The type of loader to load the files, identified by loaderType.</li>
   + The custom loader that is used by users to provide custom data loaders. It is an OPTIONAL field. 
 
 DataLoader annotation can be used both at the class level as well as at the method level.
 In case the annotation is applied at both places, then method level takes precedence over Class level.
    
   Currently the framework supports CSV , XML, Excel and Custom loader Type.
   
5)DataContext class that contains thread-local variables that stores test data as well as the name of the currently executing test method.

6)Finally, EasyTest also supports DataPoint, DataPoints and ParameterSuppliedBy annotations as well.


Some Examples of using EasyTest
---------------------------------------------------
<B>CASE 1</B>: Provides input test data in the form of CSV file at the class level, that is used by the test methods.

    @RunWith(DataDrivenTestRunner.class)
    @DataLoader(filePaths = { "getItemsData.csv" }, loaderType = LoaderType.CSV)
    public class TestConditionsSupportedByEasyTestRunner {


    /**
     * A Simple test that uses data provided by TestData annotation present at the Class level
     * @param inputData a generic map of input test data that contains all the required parameters for the test data.
     */
    @Test
    public void testGetItems(@Param(name="inputData") //@Param annotation is optional and can be omitted when the class name of the parameter is the same as the input parameter name
    Map<String, String> inputData) {
        System.out.println("library Id : " + inputData.get("LibraryId") + " and item type : "
            + inputData.get("itemType") + " and search text array :" + inputData.get("searchText"));

    }
    
<B>CASE 2</B>: User provides input test data in the form of EXCEL file at the method level only.

    @RunWith(DataDrivenTestRunner.class)
    public class TestConditionsSupportedByEasyTestRunner {


    /**
     * A Simple test that uses data provided by TestData annotation present at the Method level
     * @param inputData a generic map of input test data that contains all the required parameters for the test data.
     */
    @Test
    @DataLoader(filePaths = { "getItemsData.xls" }, loaderType = LoaderType.EXCEL)
    public void testGetItems(@Param(name="inputData")
    Map<String, String> inputData) {
        System.out.println("library Id : " + inputData.get("LibraryId") + " and item type : "
            + inputData.get("itemType") + " and search text array :" + inputData.get("searchText"));

    }
    
<B>CASE 3</B>: User provides input test data in the form of XML file at the Class level and as CSV file at method level. In this case method level test data takes priority over class level test data.

    @RunWith(DataDrivenTestRunner.class)
    @DataLoader(filePaths = { "getItemsData.xml" }, loaderType = LoaderType.XML)
    public class TestConditionsSupportedByEasyTestRunner {


    /**
     * A Simple test that uses data provided by TestData annotation present at the Method level
     * @param inputData a generic map of input test data that contains all the required parameters for the test data.
     */
    @Test
    @DataLoader(filePaths = { "getCustomData.csv" }, loaderType = LoaderType.CSV)
    public void testGetItems(@Param(name="items")
    List<ItemId> inputData) {
        .........

    }


    
<B>CASE 4</B>: User can also use their custom defined Objects as parameters in the test case. In this case LibraryId and ItenmId will be resolved using RegsitryEditorSupport of java:

    @RunWith(DataDrivenTestRunner.class)
    @DataLoader(filePaths = { "getItemsData.csv" }, loaderType = LoaderType.CSV)
    public class TestConditionsSupportedByEasyTestRunner {

    @BeforeClass
    public static void before(){
      //This is optional in case your editors follow Java Editor definition convention    
      RegistryEditorManager.registerEditor(ItemId.class , ItemIdEditor.class);
      }

    /**
     * A Simple test that uses data provided by TestData annotation present at the Method level
     * @param inputData a generic map of input test data that contains all the required parameters for the test data.
     */
    @Test
    @DataLoader(loader = MyDataLoader.class, loaderType = LoaderType.CUSTOM)
    public void testGetItems(
    LibraryId id , @Param(name="itemid") ItemId itemId) {
        System.out.println("library Id : " + id.getValue() + " and item type : "
            + itemId.getValue());
            # Param annotation tells the framework that the parameter's value should be provided by the framework.
               It can also take an optional name attribute which gives more control over the data to the user.

    }
    
    
<B>CASE 5</B>: User can also use their custom defined objects when RegistryEditor support is not enough. The user simply has to either extend AbstractConverter class or implement the Converter interface and register it with the framework using ConverterManager class.

    @RunWith(DataDrivenTestRunner.class)
    @DataLoader(filePaths = { "getItemsData.csv" }, loaderType = LoaderType.CSV)
    public class TestConditionsSupportedByEasyTestRunner {

    @BeforeClass
    public static void before(){   
      ConverterManager.registerConverter(ItemConverter.class);
      }
      
    @Test
    @DataLoader(filePaths = { "getItemsData.csv" })
    public void testConverter(@Param(name="item") Item item){
        Assert.assertNotNull(item);
        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());
        
    }
    
    
And the framework supports many more functionalities. To review the functionalities supported, please visit :https://github.com/EaseTech/easytest/wiki    
    
Conclusion
-----------
This extension to JUnit focuses on bringing back the simplicity back to JUnit in JUnit way.
This extension also focuses mainly on performing Data Driven Testing within your system with ease and at the same time giving Flexibility and Extensibility to the user to use their custom behavior.
This extension is meant for people who want to write Test cases once and then reuse them again and again.
A single test can act both as a Unit Test and an integration test. Nothing in the test case should or will change. Only the test data and the tesSubject will change. This saves a lot of developers time and in turn of the project.
