# Tag Cloud Generator

## Description
This project implements a **Tag Cloud Generator** in Java, which analyzes a given text file and generates a well-formatted HTML document displaying the N most frequent words in the text. Each word is displayed with a font size proportional to its frequency in the input file, creating an aesthetically pleasing visual representation.

---

## Objectives
- Develop an application to count word occurrences in a text file.
- Sort words by frequency and alphabetically using custom comparators.
- Generate an HTML document to display the tag cloud using CSS styling.
- Implement robust input validation and handle file I/O operations effectively.

---

## Features
### 1. Input and Output
- **Input**:
  - Reads a text file specified by the user.
  - Accepts the number of words (N) to display in the tag cloud.
- **Output**:
  - Generates a well-formed HTML file displaying the N most frequent words.
  - Words are styled with font sizes proportional to their frequency.

### 2. Word Processing
- Words are extracted based on customizable delimiters (e.g., ` \t\n\r,-.!?[]';:/()`).
- Case-insensitive processing ensures consistent word counting.

### 3. Sorting
- Words are sorted in **descending order of frequency** to identify the top N words.
- The selected N words are then sorted **alphabetically** for the final output.

### 4. Styling
- The HTML output references a CSS file (`tagcloud.css`) for styling.
- Font sizes range from `f11` (smallest) to `f48` (largest) based on word frequency.

---

## Technologies Used
- **Java**: Implementation of the core functionality.
- **OSU Components**: SimpleReader, SimpleWriter, Map, and SortingMachine for robust and efficient data handling.
- **HTML/CSS**: Styling and formatting of the tag cloud.

---

## How to Run
### Prerequisites
- Java Development Kit (JDK) installed on your system.
- A text file to use as input.

### Steps
1. Clone the repository:
   ```bash
   git clone [repository URL]
