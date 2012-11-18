
package org.easetech.easytest.example;



import java.beans.PropertyEditorManager;
import java.sql.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.example.editors.LibraryIdEditor;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * 
 * An example test class that tries to list different scenarios of using {@link DataDrivenTestRunner} and its supports
 * annotations and classes. We are loading the test data at the class level, but the user can override the data at the
 * method level as well.
 * 
 */
@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "getDDTData.csv" }, loaderType = LoaderType.CSV)
public class TestConditionsSupportedByDataDrivenTest {
    
    @Test
    public void testBug(@Param(name="className") String name, @Param(name="dateDebut")Date dateDebut , @Param(name="dateFin")Date dateFin){
        System.out.println("Class Name :" + name + " DateDebut : " + dateDebut + " dateFin :" + dateFin);
    }

    /**
     * A Junit annotated({@link Rule}) expected Exception rule that gives us the ability to specify what exception is
     * expected to be be thrown by the test case.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Example showing the usage of propertyEditors for getting custom object.
     */
    @BeforeClass
    public static void setUpGone() {
        System.out.println("Should be printed only once");
        PropertyEditorManager.registerEditor(LibraryId.class, LibraryIdEditor.class);
        ConverterManager.registerConverter(ItemConverter.class);
    }
    
    @AfterClass
    public static void tearDown() {
        System.out.println("Should ALSO be printed only once");
        
    }

    /**
     * Test DDT runner with a generic MAP parameter
     * 
     * @param inputData
     */
    @Test
    public void testDDTGetItem(
    Map<String, String> inputData) {
        System.out.print("Executing testDDTGetItem :");
        System.out.println("library Id : " + inputData.get("LibraryId") + " and item type : "
            + inputData.get("itemType") + " and search text array :" + inputData.get("searchText"));

    }

    /**
     * Test DDT with no parameter and Rule annotation
     */
    @Test
    public void testDDTGetItemsWithoutFileTypeWithStrongParameters() {
        System.out.print("Executing testDDTGetItemsWithoutFileTypeWithStrongParameters :");
        thrown.expect(RuntimeException.class);
        throw new RuntimeException("ExceptionTest");

    }

    /**
     * Test case showing the use of {@link DataLoader} annotation. This example can also be used as a test to using
     * PropertyEditors that are registered by the test class itself.
     * 
     * @param inputData
     */
    @Test
    @DataLoader(loader = CustomObjectDataLoader.class)
    public void testDDTGetItemsWithCustomLoader(
    TreeMap<String, Object> inputData) {
        System.out.print("Executing testDDTGetItemsWithCustomLoader :");
        System.out.println("library Id : " + inputData.get("LibraryId"));

    }

    /**
     * Test case that uses {@link ItemConverter} to convert from a Hashmap to an Item object instance.
     * 
     * @param item an instance of Item object that is automatically converted from a map to an Item instance.
     */
    @Test
    public void testDDTConverter(@Param(name="items")LinkedList<ItemId> items) {
        System.out.print("Executing testDDTConverter :");
        for(ItemId itemId : items){
            System.out.println("Item Id is:" + itemId);
        }
//        Assert.assertNotNull(item);
//        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());

    }
    
//    @Test
//    public void testDDTConverter(@Param(name="items")HashSet<ItemId> items) {
//        System.out.print("Executing testDDTConverter :");
//        for(ItemId itemId : items){
//            System.out.println("Item Id is:" + itemId);
//        }
////        Assert.assertNotNull(item);
////        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());
//
//    }
//    
//    @Test
//    public void testDDTConverter(@Param(name="items")Queue<ItemId> items) {
//        System.out.print("Executing testDDTConverter :");
//        for(ItemId itemId : items){
//            System.out.println("Item Id is:" + itemId);
//        }
////        Assert.assertNotNull(item);
////        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());
//
//    }
//    
//    @Test
//    public void testDDTConverter(@Param(name="items")Set<ItemId> items) {
//        System.out.print("Executing testDDTConverter :");
//        for(ItemId itemId : items){
//            System.out.println("Item Id is:" + itemId);
//        }
////        Assert.assertNotNull(item);
////        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());
//
//    }
    
//    @Test
//    public void testDDTConverter(@Param(name="items")List<ItemId> items) {
//        System.out.print("Executing testDDTConverter :");
//        for(ItemId itemId : items){
//            System.out.println("Item Id is:" + itemId);
//        }
////        Assert.assertNotNull(item);
////        System.out.println(item.getDescription() + item.getItemId() + item.getItemType());
//
//    }
}
