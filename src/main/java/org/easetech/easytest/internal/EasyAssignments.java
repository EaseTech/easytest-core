
package org.easetech.easytest.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.easetech.easytest.annotation.Param;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.runners.model.TestClass;

/**
 * 
 * A internal util class for working with the parameters of a test method.
 * This class provides EasyTest the facility to identify the method arguments, identify the DataSupplier
 * associated with the Test Framework and more.
 * 
 * @author Anuj Kumar
 *
 */
public class EasyAssignments {

    /**
     * A list of {@link PotentialAssignment} that have already been used by the test framework
     */
    private List<PotentialAssignment> fAssigned;

    /**
     * A list of unassigned {@link EasyParamSignature}.
     */
    private final List<EasyParamSignature> fUnassigned;

    /**
     * Test Class associated with te tgiven test method
     */
    private final TestClass fClass;

    /**
     * 
     * Construct a new EasyAssignments
     * @param assigned
     * @param unassigned
     * @param testClass
     */
    public EasyAssignments(List<PotentialAssignment> assigned, List<EasyParamSignature> unassigned, TestClass testClass) {
        fUnassigned = unassigned;
        fAssigned = assigned;
        fClass = testClass;
    }

    /**
     * Returns a new assignment list for {@code testMethod}, with no params assigned.
     * @param testMethod 
     * @param testClass 
     * @return {@link EasyAssignments}
     * @throws Exception 
     */
    public static EasyAssignments allUnassigned(Method testMethod, TestClass testClass) throws Exception {
        List<EasyParamSignature> signatures;
        signatures = EasyParamSignature.signatures(testClass.getOnlyConstructor());
        signatures.addAll(EasyParamSignature.signatures(testMethod));
        return new EasyAssignments(new ArrayList<PotentialAssignment>(), signatures, testClass);
    }

    public boolean isComplete() {
        return fUnassigned.size() == 0;
    }

    public EasyParamSignature nextUnassigned() {
        return fUnassigned.get(0);
    }

    public EasyAssignments assignNext(PotentialAssignment source) {
        List<PotentialAssignment> assigned = new ArrayList<PotentialAssignment>(fAssigned);
        assigned.add(source);

        return new EasyAssignments(assigned, fUnassigned.subList(1, fUnassigned.size()), fClass);
    }

    public Object[] getActualValues(int start, int stop, boolean nullsOk) throws CouldNotGenerateValueException {
        Object[] values = new Object[stop - start];
        for (int i = start; i < stop; i++) {
            Object value = fAssigned.get(i).getValue();
            if (value == null && !nullsOk)
                throw new CouldNotGenerateValueException();
            values[i - start] = value;
        }
        return values;
    }

    public List<PotentialAssignment> potentialsForNextUnassigned() throws InstantiationException,
        IllegalAccessException {
        EasyParamSignature unassigned = nextUnassigned();
        return getSupplier(unassigned).getValueSources(unassigned);
    }

    /**
     * Get the instance of class that provides the functionality to provide Data.
     * In our case, its always {@link org.easetech.easytest.annotation.Param.DataSupplier}
     * @param unassigned
     * @return {@link org.easetech.easytest.annotation.Param.DataSupplier}
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Param.DataSupplier getSupplier(EasyParamSignature unassigned) throws InstantiationException,
        IllegalAccessException {
        return new Param.DataSupplier();
    }

   

    public Object[] getConstructorArguments(boolean nullsOk) throws CouldNotGenerateValueException {
        return getActualValues(0, getConstructorParameterCount(), nullsOk);
    }

    public Object[] getMethodArguments(boolean nullsOk) throws CouldNotGenerateValueException {
        return getActualValues(getConstructorParameterCount(), fAssigned.size(), nullsOk);
    }

    public Object[] getAllArguments(boolean nullsOk) throws CouldNotGenerateValueException {
        return getActualValues(0, fAssigned.size(), nullsOk);
    }

    private int getConstructorParameterCount() {
        List<EasyParamSignature> signatures = EasyParamSignature.signatures(fClass.getOnlyConstructor());
        int constructorParameterCount = signatures.size();
        return constructorParameterCount;
    }

    public Object[] getArgumentStrings(boolean nullsOk) throws CouldNotGenerateValueException {
        Object[] values = new Object[fAssigned.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = fAssigned.get(i).getDescription();
        }
        return values;
    }

}
