package example.com;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CRC15Analyzer1 {

    private static final int POLY = 0x4599;
    private static final int DATA_LENGTH = 83;
    private final Random random = new Random();

    public static void main(String[] args) {
        new CRC15Analyzer1().run();
    }

    public void run() {

        BitSet frame = createFrame();
        int targetCRC = calculateCRC(frame);

        System.out.println("Oryginalne CRC: 0x" + String.format("%04X", targetCRC));
        printSeparator();

        checkHammingDistance(frame, targetCRC);

        checkOddErrors(frame, targetCRC);

        checkEvenErrors(frame, targetCRC);

        checkBurstErrors(frame, targetCRC);

        checkRandomErrors(frame, targetCRC);
    }


    public int calculateCRC(BitSet data) {
        int crc = 0;
        for (int i = 0; i < DATA_LENGTH; i++) {
            boolean bit = data.get(i);
            boolean msb = ((crc >> 14) & 1) == 1;
            crc <<= 1;
            if (bit ^ msb) {
                crc ^= POLY;
            }
            crc &= 0x7FFF;
        }
        return crc;
    }

    public BitSet createFrame() {
        BitSet frame = new BitSet(DATA_LENGTH);
        for (int i = 0; i < DATA_LENGTH; i++) {
            if (random.nextBoolean()) frame.set(i);
        }
        return frame;
    }


    public void checkHammingDistance(BitSet frame, int targetCRC) {
        printSubHeader("TEST 1: Odległość Hamminga (Błędy 1-5 bitów)");
        for (int k = 1; k <= 5; k++) {
            long[] result = new long[2]; // [kolizje, testy]
            runRecursiveHamming(frame, targetCRC, 0, k, 0, result);
            logResult("Błędy " + k + "-bitowe (pełny)", result[1], result[0]);
        }
    }

    private void runRecursiveHamming(BitSet frame, int target, int start, int k, int current, long[] res) {
        if (current == k) {
            res[1]++;
            if (calculateCRC(frame) == target) res[0]++;
            return;
        }
        for (int i = start; i <= DATA_LENGTH - (k - current); i++) {
            frame.flip(i);
            runRecursiveHamming(frame, target, i + 1, k, current + 1, res);
            frame.flip(i); // Backtracking
        }
    }

    public void checkOddErrors(BitSet frame, int targetCRC) {
        printSubHeader("TEST 3: Błędy nieparzyste (Wagi 7-61)");
        int trials = 100_000;
        for (int k = 7; k <= 61; k += 2) {
            long collisions = 0;
            for (int i = 0; i < trials; i++) {
                List<Integer> idx = getRandomIndices(k);
                flipAll(frame, idx);
                if (calculateCRC(frame) == targetCRC) collisions++;
                flipAll(frame, idx);
            }
            logResult("Błędy " + k + "-bitowe", trials, collisions);
        }
    }


    public void checkEvenErrors(BitSet frame, int targetCRC) {
        printSubHeader("TEST 3: Błędy parzyste (Wagi 6-60)");
        int trials = 100_000;
        for (int k = 6; k <= 60; k += 2) {
            long collisions = 0;
            for (int i = 0; i < trials; i++) {
                List<Integer> idx = getRandomIndices(k);
                flipAll(frame, idx);
                if (calculateCRC(frame) == targetCRC) collisions++;
                flipAll(frame, idx);
            }
            logResult("Błędy " + k + "-bitowe", trials, collisions);
        }
    }

    public void checkBurstErrors(BitSet frame, int targetCRC) {
        printSubHeader("TEST 4: Burst Errors (2-15)");

        for (int len = 2; len <= 15; len++) {
            long tests = 0;
            long collisions = 0;
            int innerBits = len - 2;
            long variations = 1L << innerBits;

            for (int start = 0; start <= DATA_LENGTH - len; start++) {
                for (long v = 0; v < variations; v++) {
                    tests++;
                    frame.flip(start);
                    frame.flip(start + len - 1);
                    for (int b = 0; b < innerBits; b++) {
                        if (((v >> b) & 1) == 1) frame.flip(start + 1 + b);
                    }

                    if (calculateCRC(frame) == targetCRC) collisions++;

                    for (int b = 0; b < innerBits; b++) {
                        if (((v >> b) & 1) == 1) frame.flip(start + 1 + b);
                    }
                    frame.flip(start + len - 1);
                    frame.flip(start);
                }
            }
            logResult("Długość burst: " + len, tests, collisions);
        }
    }

    public void checkRandomErrors(BitSet frame, int targetCRC) {
        printSubHeader("TEST 5: Szum losowy (Błędy o dużej wadze)");
        int trials = 1_000_000;
        int errorWeight = 32;
        long collisions = 0;

        for (int i = 0; i < trials; i++) {
            List<Integer> idx = getRandomIndices(errorWeight);
            flipAll(frame, idx);
            if (calculateCRC(frame) == targetCRC) collisions++;
            flipAll(frame, idx);
        }
        logResult("Szum losowy (32 bity)", trials, collisions);
    }

    private void flipAll(BitSet f, List<Integer> idx) {
        for (int i : idx) f.flip(i);
    }

    private List<Integer> getRandomIndices(int k) {
        List<Integer> all = new ArrayList<>(DATA_LENGTH);
        for (int i = 0; i < DATA_LENGTH; i++) all.add(i);
        Collections.shuffle(all, random);
        return all.subList(0, k);
    }

    private void logResult(String label, long trials, long col) {
        double p = (trials == 0) ? 0 : (double) col / trials * 100;
        System.out.printf("%-30s | Prób: %-10d | Kolizje: %-5d | (%.5f%%) %n",
                label, trials, col, p);
    }

    private void printSubHeader(String t) { System.out.println("\n>>> " + t); }
    private void printSeparator() { System.out.println("------------------------------------------------------------------"); }
}