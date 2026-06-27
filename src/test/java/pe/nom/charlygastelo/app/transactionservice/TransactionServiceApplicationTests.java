package pe.nom.charlygastelo.app.transactionservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TransactionServiceApplicationTests {

    @Test
    void applicationClassShouldExist() {
        TransactionServiceApplication application = new TransactionServiceApplication();

        assertNotNull(application);
    }
}