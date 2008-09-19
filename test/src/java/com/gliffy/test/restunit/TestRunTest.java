package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

public class TestRunTest
{
    /** This test is a bit complicated, but it ensures that the derived tests are called
     * and that the dependent ones are, as per the javadoc
     */
    @Test(groups = {"runtest"} )
    public void testRunTest()
    {
        RestTest mockTest = createMock("mockTest", RestTest.class);
        RestTest mockDerivedTest = createMock("mockDerivedTest", RestTest.class);
        Executor mockExecutor = createMock(Executor.class);
        Http mockHTTP = createMock(Http.class);
        Derivable mockDerivable1 = createMock("mockDerivable1",Derivable.class);
        Derivable mockDerivable2 = createMock("mockDerivable2",Derivable.class);
        Set<RestTest> oneTest = new HashSet<RestTest>();
        
        expect(mockExecutor.execute(mockTest)).andReturn(TestFactory.getSuccessfulResult(mockTest));
        expect(mockDerivable1.derive(mockTest,null)).andReturn(null);
        expect(mockDerivable2.derive(mockTest,null)).andReturn(mockDerivedTest);

        expect(mockExecutor.execute(mockDerivedTest)).andReturn(TestFactory.getSuccessfulResult(mockDerivedTest));
        expect(mockDerivable1.derive(mockDerivedTest,null)).andReturn(null);
        expect(mockDerivable2.derive(mockDerivedTest,null)).andReturn(null);
        Set<RestTest> emptySet = Collections.emptySet();

        mockExecutor.setHttp(mockHTTP);

        RestUnit unit = new RestUnit();
        unit.setExecutor(mockExecutor);
        unit.addDeriver(mockDerivable1);
        unit.addDeriver(mockDerivable2);

        replay(mockTest);
        replay(mockDerivedTest);
        replay(mockExecutor);
        replay(mockDerivable1);
        replay(mockDerivable2);

        mockExecutor.setHttp(mockHTTP);

        unit.runTest(mockTest);

        verify(mockTest);
        verify(mockDerivedTest);
        verify(mockExecutor);
        verify(mockDerivable1);
        verify(mockDerivable2);
    }

    @Test(groups = {"runtest"} )
    public void testForSkippedDependentsFail()
    {
        testForSkippedDependents(Result.FAIL);
    }

    @Test(groups = {"runtest"} )
    public void testForSkippedDependentsException()
    {
        testForSkippedDependents(Result.EXCEPTION);
    }

    private void testForSkippedDependents(Result res)
    {
        Executor executor = createMock(Executor.class);
        RestUnit restUnit = new RestUnit(executor);
        RestTest test = TestFactory.getRandomTest();
        ExecutionResult failure = new ExecutionResult();
        failure.setResult(res);
        failure.setDescription("There is no description");
        failure.setTest(test);

        expect(executor.execute(test)).andReturn(failure);

        replay(executor);

        List<ExecutionResult> results = restUnit.runTest(test);

        assert results.size() == 1 : "Expected one result";

        for (ExecutionResult result: results)
        {
            if (result.getTest() == test)
            {
                assert result.getResult() == res : "Expected initial test to be " + res.toString();
            }
            else
            {
                assert result.getResult() == Result.SKIP : "Got a result of " + result.getResult() + " instead of " + Result.SKIP.toString() + "(" + result.toString() + ")";
            }
        }
    }
}
