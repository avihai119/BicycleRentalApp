import com.hit.dao.BicycleDaoFileImpl;
import com.hit.dao.RentalDaoFileImpl;
import com.hit.dao.UserDaoFileImpl;
import com.hit.dm.*;
import com.hit.service.RentalService;
import com.hit.dm.Color;

public class Main {
    public static void main(String[] args) {

        BicycleDaoFileImpl bicycleDao = new BicycleDaoFileImpl();
        UserDaoFileImpl userDao = new UserDaoFileImpl();
        RentalDaoFileImpl rentalDao = new RentalDaoFileImpl();

        Bicycle bike1 = new Bicycle(101, "Trek", 18, 10.5, Color.BLUE);
        Bicycle bike2 = new ElectricBicycle(102, "Specialized", 7, 13.2, Color.RED, 500, 60, true);
        User user1 = new User(1, "John Doe", 30);

        bicycleDao.save(bike1);
        bicycleDao.save(bike2);
        userDao.save(user1);

        RentalService rentalService = new RentalService(bicycleDao, userDao, rentalDao);

        System.out.println("--- Before Renting ---");
        bike1.displayInfo();
        System.out.println();
        bike2.displayInfo();

        System.out.println("\n--- Renting Bicycle ---");
        Bicycle rentedBicycle = rentalService.rentBicycle(101, 1);
        if (rentedBicycle != null) {
            System.out.println("Bicycle with ID " + rentedBicycle.getId() + " rented successfully.");
        } else {
            System.out.println("Rental failed: Bicycle is already rented.");
        }

        System.out.println("\n--- Trying to Rent Already Rented Bicycle ---");
        Bicycle secondRentalAttempt = rentalService.rentBicycle(101, 1);
        if (secondRentalAttempt == null) {
            System.out.println("Second rental failed: Bicycle is already rented.");
        }

        System.out.println("\n--- Ending Rental ---");
        Rental rental = rentalDao.findActiveRentalByBicycleId(101);
        if (rental != null) {
            rentalService.endRental(rental.getRentalId());
            System.out.println("Rental with ID " + rental.getRentalId() + " ended successfully.");
        }

        System.out.println("\n--- Deleting Rental ---");
        Rental deletedRental = rentalDao.search(1);
        if (deletedRental != null) {
            rentalDao.delete(deletedRental);
            System.out.println("Rental with ID " + deletedRental.getRentalId() + " deleted successfully.");

            Rental searchAfterDelete = rentalDao.search(deletedRental.getRentalId());
            if (searchAfterDelete == null) {
                System.out.println("Confirmed: Rental no longer exists in the system.");
            } else {
                System.out.println("Error: Rental still found after deletion.");
            }

            System.out.println("\n--- Trying to Delete the Same Rental Again ---");
            try {
                rentalDao.delete(deletedRental);
            } catch (IllegalStateException e) {
                System.out.println("Expected exception caught: " + e.getMessage());
            }
        } else {
            System.out.println("No rental found to delete.");
        }
    }
}