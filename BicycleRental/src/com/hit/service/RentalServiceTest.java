package com.hit.service;

import com.hit.dao.DataSourceManager;
import com.hit.dm.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RentalServiceTest {

    private RentalService rentalService;

    // Use static IDs for consistent testing
    private static final long TEST_BICYCLE_ID_1 = 1001L;
    private static final long TEST_BICYCLE_ID_2 = 1002L;
    private static final long TEST_BICYCLE_ID_3 = 1003L;
    private static final long TEST_USER_ID_1 = 2001L;
    private static final long TEST_USER_ID_2 = 2002L;

    @BeforeEach
    void setUp() {
        System.out.println("🔧 Setting up test...");

        // CRITICAL: Reset the singleton instance
        DataSourceManager.resetInstance();

        // Clean up any existing files
        cleanupFiles();

        // Create completely fresh service
        rentalService = new RentalService();

        // Add consistent test data
        setupTestData();

        System.out.println("✅ Test setup completed");
    }

    @AfterEach
    void tearDown() {
        System.out.println("🧹 Cleaning up after test...");

        // Reset the singleton
        DataSourceManager.resetInstance();

        // Clean up files
        cleanupFiles();

        System.out.println("✅ Test cleanup completed\n");
    }

    private void cleanupFiles() {
        // List of files to clean
        String[] filesToClean = {
                "DataSource.txt",
                "TestDataSource.txt"
        };

        for (String filename : filesToClean) {
            File file = new File(filename);
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("  " + (deleted ? "✅" : "❌") + " Deleted: " + filename);
            }
        }
    }

    private void setupTestData() {
        try {
            // Add test users with static IDs
            User user1 = new User(TEST_USER_ID_1, "Test User 1", 25);
            User user2 = new User(TEST_USER_ID_2, "Test User 2", 30);
            rentalService.addUser(user1);
            rentalService.addUser(user2);

            // Add test bicycles with static IDs
            Bicycle bike1 = new Bicycle(TEST_BICYCLE_ID_1, "Test Trek", 21, 12.5, Color.RED);
            Bicycle bike2 = new Bicycle(TEST_BICYCLE_ID_2, "Test Giant", 18, 11.0, Color.BLUE);
            Bicycle bike3 = new Bicycle(TEST_BICYCLE_ID_3, "Test Specialized", 24, 13.2, Color.BLACK);
            rentalService.addBicycle(bike1);
            rentalService.addBicycle(bike2);
            rentalService.addBicycle(bike3);

            System.out.println("  ✅ Test data added successfully");

        } catch (Exception e) {
            System.out.println("  ❌ Test data setup error: " + e.getMessage());
            throw e; // Let the test fail if setup fails
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test successful bicycle rental")
    void testRentBicycle_Success() {
        System.out.println("🧪 Testing successful bicycle rental...");

        // Verify bicycle exists before renting
        Bicycle bicycleBeforeRent = rentalService.searchBicycle(TEST_BICYCLE_ID_1);
        assertNotNull(bicycleBeforeRent, "Bicycle should exist before rental");
        assertFalse(bicycleBeforeRent.isRented(), "Bicycle should not be rented initially");

        // Verify user exists
        User user = rentalService.searchUser(TEST_USER_ID_1);
        assertNotNull(user, "User should exist");

        // Perform rental
        Bicycle rentedBicycle = rentalService.rentBicycle(TEST_BICYCLE_ID_1, TEST_USER_ID_1);

        assertNotNull(rentedBicycle, "Rented bicycle should not be null");
        assertTrue(rentedBicycle.isRented(), "Bicycle should be marked as rented");
        assertEquals(TEST_BICYCLE_ID_1, rentedBicycle.getId(), "Bicycle ID should match");

        System.out.println("  ✅ Bicycle rental test passed");
    }

    @Test
    @Order(2)
    @DisplayName("Test rental of already rented bicycle")
    void testRentBicycle_AlreadyRented() {
        System.out.println("🧪 Testing rental of already rented bicycle...");

        // First rental
        rentalService.rentBicycle(TEST_BICYCLE_ID_2, TEST_USER_ID_1);

        // Second rental attempt with different user
        Bicycle secondRental = rentalService.rentBicycle(TEST_BICYCLE_ID_2, TEST_USER_ID_2);

        assertNull(secondRental, "Second rental attempt should fail");

        System.out.println("  ✅ Duplicate rental prevention test passed");
    }

    @Test
    @Order(3)
    @DisplayName("Test rental with non-existent bicycle")
    void testRentBicycle_NonExistentBicycle() {
        System.out.println("🧪 Testing rental with non-existent bicycle...");

        long nonExistentBicycleId = 9999L;

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentBicycle(nonExistentBicycleId, TEST_USER_ID_1);
        }, "Should throw exception for non-existent bicycle");

        System.out.println("  ✅ Non-existent bicycle test passed");
    }

    @Test
    @Order(4)
    @DisplayName("Test rental with non-existent user")
    void testRentBicycle_NonExistentUser() {
        System.out.println("🧪 Testing rental with non-existent user...");

        long nonExistentUserId = 9999L;

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentBicycle(TEST_BICYCLE_ID_3, nonExistentUserId);
        }, "Should throw exception for non-existent user");

        System.out.println("  ✅ Non-existent user test passed");
    }

    @Test
    @Order(5)
    @DisplayName("Test successful bicycle return")
    void testEndRental_Success() {
        System.out.println("🧪 Testing successful bicycle return...");

        // First rent the bicycle
        Bicycle rentedBicycle = rentalService.rentBicycle(TEST_BICYCLE_ID_1, TEST_USER_ID_1);
        assertNotNull(rentedBicycle, "Bicycle should be rented successfully");
        assertTrue(rentedBicycle.isRented(), "Bicycle should be marked as rented");

        // Then return it
        boolean returnSuccess = rentalService.endRental(TEST_BICYCLE_ID_1);

        assertTrue(returnSuccess, "Bicycle return should be successful");

        Bicycle returnedBicycle = rentalService.searchBicycle(TEST_BICYCLE_ID_1);
        assertFalse(returnedBicycle.isRented(), "Bicycle should no longer be rented");

        System.out.println("  ✅ Bicycle return test passed");
    }

    @Test
    @Order(6)
    @DisplayName("Test return of non-rented bicycle")
    void testEndRental_NotRented() {
        System.out.println("🧪 Testing return of non-rented bicycle...");

        // Try to return bicycle that is not rented (TEST_BICYCLE_ID_2 is not rented in this test)
        boolean returnSuccess = rentalService.endRental(TEST_BICYCLE_ID_2);

        assertFalse(returnSuccess, "Return should fail for non-rented bicycle");

        System.out.println("  ✅ Non-rented bicycle return test passed");
    }

    @Test
    @Order(7)
    @DisplayName("Test return of non-existent bicycle")
    void testEndRental_NonExistent() {
        System.out.println("🧪 Testing return of non-existent bicycle...");

        long nonExistentBicycleId = 9999L;

        boolean returnSuccess = rentalService.endRental(nonExistentBicycleId);

        assertFalse(returnSuccess, "Return should fail for non-existent bicycle");

        System.out.println("  ✅ Non-existent bicycle return test passed");
    }

    @Test
    @Order(8)
    @DisplayName("Test search existing bicycle")
    void testSearchBicycle_Found() {
        System.out.println("🧪 Testing search existing bicycle...");

        Bicycle found = rentalService.searchBicycle(TEST_BICYCLE_ID_1);

        assertNotNull(found, "Bicycle should be found");
        assertEquals(TEST_BICYCLE_ID_1, found.getId(), "Bicycle ID should match");
        assertEquals("Test Trek", found.getBrand(), "Bicycle brand should match");

        System.out.println("  ✅ Bicycle search test passed");
    }

    @Test
    @Order(9)
    @DisplayName("Test search non-existent bicycle")
    void testSearchBicycle_NotFound() {
        System.out.println("🧪 Testing search non-existent bicycle...");

        long nonExistentId = 9999L;

        Bicycle found = rentalService.searchBicycle(nonExistentId);

        assertNull(found, "Non-existent bicycle should return null");

        System.out.println("  ✅ Non-existent bicycle search test passed");
    }

    @Test
    @Order(10)
    @DisplayName("Test search existing user")
    void testSearchUser_Found() {
        System.out.println("🧪 Testing search existing user...");

        User found = rentalService.searchUser(TEST_USER_ID_1);

        assertNotNull(found, "User should be found");
        assertEquals(TEST_USER_ID_1, found.getId(), "User ID should match");
        assertEquals("Test User 1", found.getName(), "User name should match");

        System.out.println("  ✅ User search test passed");
    }
}