package com.sequenceiq.it.cloudbreak.newway.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.Stack;
import com.sequenceiq.it.cloudbreak.newway.StackEntity;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;

public class StackStopTest extends AbstractIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackStopTest.class);

    @BeforeMethod
    public void beforeMethod(Object[] data) {
        TestContext testContext = (TestContext) data[0];

        createDefaultUser(testContext);
        createDefaultCredential(testContext);
        createDefaultImageCatalog(testContext);
    }

    @Test(dataProvider = "testContext")
    public void testCreateAndStopStack(TestContext testContext) throws InterruptedException {
        testContext.given(StackEntity.class)
                .when(Stack.postV3())
                .await(STACK_AVAILABLE)
                .when(Stack.stop())
                .await(STACK_STOPPED)
                .when(Stack.start())
                .await(STACK_AVAILABLE)
                .validate();
    }

    @AfterMethod(alwaysRun = true)
    public void tear(Object[] data) {
        TestContext testContext = (TestContext) data[0];
        //testContext.cleanupTestContextEntity();
    }
}
