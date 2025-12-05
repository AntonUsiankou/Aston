package org.ausiankou.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ausiankou.dao.UserDao;
import org.ausiankou.dao.UserDaoImpl;
import org.ausiankou.exception.UserServiceException;
import org.ausiankou.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUtil {
    public static class UserConsoleService {

        private static final Logger logger = LogManager.getLogger(UserConsoleService.class);
        private final UserDao userDao = new UserDaoImpl();
        private final Scanner scanner = new Scanner(System.in);

        public void start() {
            logger.info("Starting User Service application");

            while (true) {
                printMenu();
                int choice = readInt();

                if (choice == 8) {
                    logger.info("Exiting application");
                    break;
                }

                processChoice(choice);
            }

            scanner.close();
        }

        private void printMenu() {
            System.out.println("\n=== User Service Menu ===");
            System.out.println("1. Create new user");
            System.out.println("2. Find user by ID");
            System.out.println("3. Find all users");
            System.out.println("4. Update user");
            System.out.println("5. Delete user");
            System.out.println("6. Find user by email");
            System.out.println("7. Find users by name");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
        }

        private int readInt() {
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next();
            }
            return scanner.nextInt();
        }

        private String readString() {
            scanner.nextLine(); // Очистка буфера
            return scanner.nextLine();
        }

        private void processChoice(int choice) {
            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> findUserByEmail();
                    case 7 -> findUsersByName();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (UserServiceException e) {
                System.out.println("Error: " + e.getMessage());
                logger.error("Operation failed: {}", e.getMessage(), e);
            }
        }

        private void createUser() {
            System.out.print("Enter name: ");
            String name = readString();
            System.out.print("Enter email: ");
            String email = readString();
            System.out.print("Enter age: ");
            int age = readInt();

            if (userDao.findByEmail(email).isPresent()) {
                System.out.println("Email already exists!");
                return;
            }

            User user = new User(name, email, age);
            User savedUser = userDao.save(user);
            System.out.println("User created: " + savedUser);
        }

        private void findUserById() {
            System.out.print("Enter user ID: ");
            long id = readInt();

            userDao.findById(id)
                    .ifPresentOrElse(
                            user -> System.out.println("Found: " + user),
                            () -> System.out.println("User not found")
                    );
        }

        private void findAllUsers() {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found");
            } else {
                System.out.println("Total users: " + users.size());
                users.forEach(System.out::println);
            }
        }

        private void updateUser() {
            System.out.print("Enter user ID to update: ");
            long id = readInt();

            Optional<User> userOpt = userDao.findById(id);
            if (userOpt.isEmpty()) {
                System.out.println("User not found");
                return;
            }

            User user = userOpt.get();
            System.out.println("Current: " + user);

            System.out.print("New name (Enter to skip): ");
            String name = readString();
            if (!name.isEmpty()) user.setName(name);

            System.out.print("New email (Enter to skip): ");
            String email = readString();
            if (!email.isEmpty()) {
                Optional<User> existing = userDao.findByEmail(email);
                if (existing.isPresent() && !existing.get().getId().equals(id)) {
                    System.out.println("Email already taken!");
                    return;
                }
                user.setEmail(email);
            }

            System.out.print("New age (Enter to skip): ");
            String ageStr = readString();
            if (!ageStr.isEmpty()) {
                try {
                    user.setAge(Integer.parseInt(ageStr));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid age");
                }
            }

            User updated = userDao.update(user);
            System.out.println("Updated: " + updated);
        }

        private void deleteUser() {
            System.out.print("Enter user ID to delete: ");
            long id = readInt();

            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                System.out.print("Delete user " + user.get().getName() + "? (yes/no): ");
                if (readString().equalsIgnoreCase("yes")) {
                    userDao.delete(id);
                    System.out.println("User deleted");
                }
            } else {
                System.out.println("User not found");
            }
        }

        private void findUserByEmail() {
            System.out.print("Enter email: ");
            String email = readString();

            userDao.findByEmail(email)
                    .ifPresentOrElse(
                            user -> System.out.println("Found: " + user),
                            () -> System.out.println("User not found")
                    );
        }

        private void findUsersByName() {
            System.out.print("Enter name: ");
            String name = readString();

            List<User> users = userDao.findByName(name);
            if (users.isEmpty()) {
                System.out.println("No users found");
            } else {
                users.forEach(System.out::println);
            }
        }
    }
}
