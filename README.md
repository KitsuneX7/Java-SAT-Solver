# Java SAT Solver ⚡

A lightweight, deterministic Boolean Satisfiability (SAT) solver built entirely from scratch in Java. 

This engine parses complex logical expressions, constructs an Abstract Syntax Tree (AST), and evaluates all possible truth assignments to determine if a given propositional logic formula is a tautology, a contradiction, or satisfiable.

## Features
* **Custom Recursive Descent Parser:** Evaluates nested parenthesis and stacked negations without relying on external libraries.
* **Bare-Metal Logic:** Implements custom logic gates for `NOT`, `AND`, `OR`, `IMPLIES`, `BICONDITIONAL`, and `XOR`.
* **Zero Dependencies:** Built purely with standard Java utilities.
* **Bulletproof Edge-Case Handling:** Heavily filtered against syntax errors and invalid inputs.

## Supported Operators
| Symbol | Logic |
| :---: | :--- |
| `!` | NOT |
| `&` | AND |
| `\|` | OR |
| `>` | IMPLIES |
| `=` | DOUBLE CONDITIONAL |
| `/` | XOR |

## How to Run
Simply compile and run the engine in your terminal:
```bash
javac SAT.java Expression.java
java SAT
