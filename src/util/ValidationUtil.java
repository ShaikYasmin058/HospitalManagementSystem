package util;

import java.util.Scanner;

public class ValidationUtil {

    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        return isNonEmpty(name) && name.matches("[A-Za-z\\s]{2,50}");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age < 151;
    }

    public static String readRequiredString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (isNonEmpty(value)) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
