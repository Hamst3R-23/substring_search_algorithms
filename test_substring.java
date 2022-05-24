package vlad;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class test_substring {
    public static void main(String[] args) throws Exception {

        Path path = Paths.get("text.txt"); //put your data to text.txt
        byte[] data;
        data = Files.readAllBytes(path);
        String source = new String(data);
        String sample = "ga"; //we are searching this

        System.out.println("Sample: '" + sample + "'");
        System.out.println("Boyer-Moore algorithm: " + substring.boyer_moore(source, sample));
        System.out.println("Rabin-Karp algorithm: " + substring.rabin_karp(source, sample));
        System.out.println("Knuth-Morris-Pratt algorithm: " + substring.knuth_morris_pratt(source, sample));
        System.out.println("State machine algorithm: " + substring.state_machine(source, sample));
    }
}