# # CRC-15 Property Analysis (CAN Bus)

## 📘 Project Description

This project presents an analysis of the **CRC-15 algorithm** properties used in the CAN bus communication protocol.

The main objective was to experimentally verify the algorithm’s ability to detect different types of transmission errors in an **83-bit data frame**.

All simulations were implemented in **Java**, using the `BitSet` class to perform bit-level operations.

---

## 🎯 Laboratory Objective

* Verification of CRC-15 mathematical properties
* Evaluation of Hamming distance
* Analysis of error detection capabilities:

    * Single-bit errors
    * Multi-bit errors
    * Odd-weight errors
    * Even-weight errors
    * Burst errors
    * Random noise errors

---

## ⚙️ Methodology

The experiment simulation process included:

1. Generating a random reference data frame (83 bits)
2. Calculating the correct CRC-15 checksum
3. Introducing controlled errors by flipping selected bits
4. Recalculating CRC and checking whether mismatches were detected

The core of the implementation is a polynomial division algorithm based on an LFSR mechanism.

---

## 🧪 Implemented Tests

### 1. Hamming Distance Test

* Exhaustive analysis of all error combinations from **1 to 5 bits**
* Result: **0 collisions**
* Confirms guaranteed detection up to 5 erroneous bits

---

### 2. Odd-Weight Error Detection

* Random simulations (100,000 trials per case)
* Error weights from **7 to 61 bits**
* Result: **100% detection**
* Due to the polynomial containing factor *(x + 1)*

---

### 3. Even-Weight Error Detection

* Random simulations for error weights **6–60 bits**
* Small number of collisions observed
* Detection effectiveness remains statistically high

---

### 4. Burst Error Detection

* Analysis of contiguous error packets (length 2–15 bits)
* Exhaustive positional testing
* Result: **0 collisions**
* Confirms guaranteed detection for burst length ≤ polynomial degree

---

### 5. Random Noise Simulation

* 1,000,000 trials
* Random errors affecting 32 bits
* Collision rate ≈ **0.0065%**
* Matches theoretical probability for 15-bit CRC

---

## 📊 Key Findings

* CRC-15 detects all errors up to **5 bits** (Hamming distance = 6)
* Guarantees detection of all burst errors up to **15 bits**
* Fully detects all **odd-weight errors**
* Rare collisions appear only for high even-weight or random errors
* Statistical effectiveness aligns with theoretical CRC limits

---

## 🚗 Practical Application

CRC-15 is an optimal solution for **CAN bus systems**, providing:

* High transmission reliability
* Strong error detection capability
* Low checksum overhead (15 bits)

This balance makes it ideal for automotive and embedded communication networks.

---

## 🛠️ Technologies Used

* Java
* BitSet
* LFSR polynomial division simulation

---

## ▶️ How to Run



---

## 📚 References

* BOSCH — *CAN Specification 2.0*, 1991
* Koopman & Chakravarty — CRC Polynomial Selection (DSN 2004)
* Zimmermann & Schmidgall — *CAN Bus Fundamentals*
* Tanenbaum — *Computer Networks*
* Peterson & Brown — *Cyclic Codes for Error Detection*

---

## 👨‍💻 Authors

* Usevalad Chyhir
* Dmytro Kupetskyi


