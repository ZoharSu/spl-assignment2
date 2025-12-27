package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: lae <number of threads> <input path> <output path>");
            return;
        }

        int thread_num = Integer.parseInt(args[0]);
        // String input = args[2], output = args[3];
        InputParser input = new InputParser();
        ComputationNode root;
        try { root = input.parse(args[1]);
        } catch (Exception e) {
            // TODO: error message
            return;
        }

        LinearAlgebraEngine engine = new LinearAlgebraEngine(thread_num);
        engine.run(root);
        OutputWriter.write(root.getMatrix(), args[2]);
    }
}
