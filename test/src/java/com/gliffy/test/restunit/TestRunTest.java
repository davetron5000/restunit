package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

public class TestRunTest
{
    @Test
    public void testRunTest()
    {
        RestTest mockTest = createMock("mockTest", RestTest.class);
        RestTest mockDerivedTest = createMock("mockDerivedTest", RestTest.class);
        RestTest mockDependentTest = createMock("mockDependentTest", RestTest.class);
        Executor mockExecutor = createMock(Executor.class);
        Derivable mockDerivable1 = createMock("mockDerivable1",Derivable.class);
        Derivable mockDerivable2 = createMock("mockDerivable2",Derivable.class);
        Set<RestTest> oneTest = new HashSet<RestTest>();
        oneTest.add(mockDependentTest);

        expect(mockExecutor.execute(mockTest)).andReturn(TestFactory.getSuccessfulResult(mockTest));
        expect(mockTest.getDependentTests()).andReturn(oneTest);
        expect(mockDerivable1.derive(mockTest)).andReturn(null);
        expect(mockDerivable2.derive(mockTest)).andReturn(mockDerivedTest);

        expect(mockExecutor.execute(mockDerivedTest)).andReturn(TestFactory.getSuccessfulResult(mockDerivedTest));
        expect(mockDerivable1.derive(mockDerivedTest)).andReturn(null);
        expect(mockDerivable2.derive(mockDerivedTest)).andReturn(null);
        Set<RestTest> emptySet = Collections.emptySet();
        expect(mockDerivedTest.getDependentTests()).andReturn(emptySet);

        expect(mockExecutor.execute(mockDependentTest)).andReturn(TestFactory.getSuccessfulResult(mockDerivedTest));
        expect(mockDerivable1.derive(mockDependentTest)).andReturn(null);
        expect(mockDerivable2.derive(mockDependentTest)).andReturn(null);
        expect(mockDependentTest.getDependentTests()).andReturn(emptySet);

        RestUnit unit = new RestUnit();
        unit.setExecutor(mockExecutor);
        unit.addDeriver(mockDerivable1);
        unit.addDeriver(mockDerivable2);

        replay(mockTest);
        replay(mockDerivedTest);
        replay(mockDependentTest);
        replay(mockExecutor);
        replay(mockDerivable1);
        replay(mockDerivable2);

        unit.runTest(mockTest);

        verify(mockTest);
        verify(mockDerivedTest);
        verify(mockDependentTest);
        verify(mockExecutor);
        verify(mockDerivable1);
        verify(mockDerivable2);
    }
}
