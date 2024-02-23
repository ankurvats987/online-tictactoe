import java.util.Random;

public class RandNameGenerator {
    private static final String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    static String getName() {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            int randomIndex = new Random().nextInt(charSet.length());

            char randomChar = charSet.charAt(randomIndex);
            name.append(randomChar);
        }

        return name.toString();
    }
    
}
