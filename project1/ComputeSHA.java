import java.security.MessageDigest;
import java.util.Formatter;
import java.nio.file.*;

public class ComputeSHA{
    public static void main(String[] args) throws Exception{
        byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
        MessageDigest md = MessageDigest.getInstance("SHA-1"); 
        Formatter formatter = new Formatter();

        for(byte b : md.digest(bytes)){
            formatter.format("%02x", b);
        }

        System.out.println(formatter.toString());
    }
}
