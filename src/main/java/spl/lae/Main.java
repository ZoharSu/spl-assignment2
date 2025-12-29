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
        InputParser input = new InputParser();
        ComputationNode root;
        LinearAlgebraEngine engine;
        try {
            root = input.parse(args[1]);
            engine = new LinearAlgebraEngine(thread_num);
            engine.run(root);
        } catch (Exception e) {
            OutputWriter.write(e.getMessage(), args[2]);
            return;
        }

        System.out.println(engine.getWorkerReport());
        OutputWriter.write(root.getMatrix(), args[2]);
    }
}
