/**
 * @author Trevor Hartman
 * @author Linh Dinh
 *
 * @since version 1.0
 */
import org.apache.commons.codec.digest.Crypt;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        FileInputStream fileStream = new FileInputStream(this.dictionary);
        Scanner scanner = new Scanner(fileStream);
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            for (User user : this.users) {
                String passHash = user.getPassHash();
                if (passHash.contains("$")) {
                    String hash = Crypt.crypt(word, user.getPassHash());
                    if (hash.equals(passHash)){
                        System.out.println("Found password " + word + " for user " + user.getUsername());
                    }
                }
            }
        }
        scanner.close();
    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int)stream.count();
        } catch(IOException ignored) {}
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
        int numLines = getLineCount(shadowFile);
        User[] users = new User[numLines];
        FileInputStream fileStream = new FileInputStream(shadowFile);
        Scanner scanner = new Scanner(fileStream);
        int i = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] element = line.split(":");
            String username = element[0];
            String passHash = element[1];
            User user = new User(username, passHash);
            users[i] = user;
            i++;
        }
        scanner.close();
        return users;

    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Type the path to your shadow file: ");
        String shadowPath = sc.nextLine();
        System.out.print("Type the path to your dictionary file: ");
        String dictPath = sc.nextLine();
        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
