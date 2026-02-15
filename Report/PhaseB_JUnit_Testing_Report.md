# Phase B – Modular JUnit Testing Report

## 1. Overview

This report documents the **Phase B: Modular JUnit Testing** work for the Arabic Text Editor project. The goals of this phase were:

- Design tests per **architectural layer** (Business, Data, Presentation).
- Keep tests **swappable**, written against interfaces where possible.
- Verify critical behaviours:
  - Search, TF–IDF, import, transliteration (Business layer).
  - Hash integrity and Singleton behaviour (Data layer).
  - Basic GUI logic (Presentation layer).
- Run the full test suite and collect **coverage evidence**.

All tests are located under the root-level `Testing` folder, parallel to `src`, in accordance with the assignment requirements.

Directory layout (relevant part):

- src/
  - bll/
  - dal/
  - dto/
  - pl/
- Testing/
  - business/
  - data/
  - presentation/

---

## 2. Business Layer Tests

Business-layer functionality is accessed through the **IEditorBO** interface and helper classes such as **SearchWord**. Tests live under:

- [Testing/business](Testing/business)

### 2.1 SearchWord Tests

File:
- [Testing/business/SearchWordTest.java](Testing/business/SearchWordTest.java)

Target:
- `bll.SearchWord.searchKeyword(String keyword, List<Documents> docs)`

Key scenarios covered:

- **Positive – keyword found:**
  - Multi-page document list, keyword present in content.
  - Verifies that result list contains a formatted entry for the matching document.
- **Boundary – exactly 3 characters:**
  - Keyword length 3 (minimum allowed by business rule).
  - Ensures no exception and correct match behaviour.
- **Negative – keyword too short (< 3):**
  - Length 2 keyword.
  - Expects `IllegalArgumentException`, validating input guarding.
- **Negative – keyword not found:**
  - Keyword absent from all pages.
  - Expects an empty result list.
- **Negative – empty documents list:**
  - Empty documents input.
  - Expects an empty result list.

These tests collectively exercise the control flow paths used in the white-box analysis for SearchWord.

### 2.2 Import & Transliteration via IEditorBO

File:
- [Testing/business/EditorBOImportAndTransliterateTest.java](Testing/business/EditorBOImportAndTransliterateTest.java)

Targets:
- `bll.EditorBO.importTextFiles(File file, String fileName)`
- `bll.EditorBO.transliterate(int pageId, String arabicText)`

Approach (interface-based / command-like):

- A small inner stub class **FacadeDAOMock** implements `dal.IFacadeDAO` (which extends `IEditorDBDAO`). This stub records which methods are called.
- Tests interact with **IEditorBO** (type) and **EditorBO** (implementation), keeping dependencies expressed via interfaces.

Test cases:

1. **Import behaviour (command-like):**
   - Creates a temporary `.txt` file with known content.
   - Calls `editor.importTextFiles(tempFile, "sample-import.txt")`.
   - Asserts:
     - Import returns `true` when `createFileInDB` in the facade succeeds.
     - `createFileInDB` was called on the facade stub.
     - The file name passed to the facade matches the expected name.
     - The content passed to the facade contains the text written to the temp file.

2. **Transliteration delegation:**
   - Facade stub implements `transliterateInDB` to return `"TRANSLITERATED:" + arabicText`.
   - Calls `editor.transliterate(1, "سلام")` via the **IEditorBO** interface.
   - Asserts that the returned value equals `"TRANSLITERATED:سلام"`, confirming correct delegation.

These tests act as **command-like tests** for the Import and Transliterate behaviours, even though the repository does not include explicit `ImportCommand` / `TransliterateCommand` classes.

### 2.3 TF–IDF Algorithm Tests

File:
- [Testing/data/TFIDFCalculatorTest.java](Testing/data/TFIDFCalculatorTest.java)

Target:
- `dal.TFIDFCalculator.calculateDocumentTfIdf(String document)`

Key tests:

1. **Positive – simple Arabic corpus:**
   - Adds two short Arabic documents to the TF–IDF corpus using `addDocumentToCorpus`.
   - Uses a small query document where the expected average TF–IDF score is computed manually as:
     \[
     expected = \frac{\ln(2/3)}{2}
     \]
   - Asserts the method’s result matches this value within a small tolerance.

2. **Negative – empty document:**
   - Calls `calculateDocumentTfIdf("")` with no content.
   - Asserts a return value of `0.0`, ensuring safe handling of empty input.

3. **Negative – special-characters-only document:**
   - Adds a simple Arabic document to the corpus.
   - Calls `calculateDocumentTfIdf("1234 !!!")`, which becomes empty after preprocessing.
   - Asserts that the result is **not infinite** (`!Double.isInfinite(result)`) and the method does not throw, confirming graceful behaviour for non-Arabic-only input.

Although `TFIDFCalculator` lives in the Data Access layer package (`dal`), these tests validate business-relevant analytics behaviour.

---

## 3. Data Persistence Layer Tests

Data-layer tests focus on Singletons, hashing behaviour, and DAO interactions. They live under:

- [Testing/data](Testing/data)

### 3.1 Pagination Logic

File:
- [Testing/data/PaginationDAOTest.java](Testing/data/PaginationDAOTest.java)

Target:
- `dal.PaginationDAO.paginate(String fileContent)`

Scenario coverage:

- **Positive – normal content (< 100 chars):**
  - Confirms a single page is produced with the original content and page number 1.
- **Boundary – exactly 100 characters:**
  - Asserts exactly one page is created, with 100 characters.
- **Boundary – 101 characters:**
  - Asserts two pages: first with 100 chars, second with 1 char; page numbers 1 and 2.
- **Negative – null content:**
  - Expects a single empty page (graceful handling of `null`).
- **Negative – empty string:**
  - Expects a single empty page for `""` as input.

These tests correspond to the paths and boundary cases analysed in the pagination CFG from Phase A.

### 3.2 Singleton Database Connection

File:
- [Testing/data/DatabaseConnectionTest.java](Testing/data/DatabaseConnectionTest.java)

Target:
- `dal.DatabaseConnection.getInstance()`

Test:

- **testSingletonPattern:**
  - Calls `DatabaseConnection.getInstance()` twice.
  - Uses `assertSame(instance1, instance2)` to verify that both references point to the same instance, confirming the Singleton property.

### 3.3 Hash Calculator Unit Tests

File:
- [Testing/data/HashCalculatorTest.java](Testing/data/HashCalculatorTest.java)

Target:
- `dal.HashCalculator.calculateHash(String text)`

Tests:

- **Deterministic hashing:**
  - Same input string hashed twice yields identical hash strings.
- **Different input, different hash:**
  - Two distinct input strings produce different hash outputs.

These ensure the hashing algorithm is stable and suitable for use as a content fingerprint.

### 3.4 Hash Metadata Integrity (DB-Level Behaviour)

File:
- [Testing/data/HashMetadataIntegrityTest.java](Testing/data/HashMetadataIntegrityTest.java)

Targets:
- `dal.IEditorDBDAO` / `dal.EditorDBDAO`
- `dal.HashCalculator`

Assignment requirement:

> "Editing a file changes the current session hash but retains the original import hash in the database metadata."

Observed design:

- The `files` table stores a single `fileHash` value per file.
- `createFileInDB` computes a hash using `HashCalculator.calculateHash(content)` and stores it in `fileHash` (original import hash).
- `updateFileInDB` updates page content and analytics but does **not** change `fileHash`.
- A “current session hash” for modified content is computed on the fly using `HashCalculator` and is not stored back into `files.fileHash`.

Test behaviour:

- Uses the **interface type** `IEditorDBDAO dao = new EditorDBDAO();` to keep the test interface-oriented.
- Fetches existing documents from the database via `dao.getFilesFromDB()`.
  - If there are no documents, the test is **skipped** using a JUnit assumption (so it does not fail in an empty database).
- Picks one document and records its stored hash `originalStoredHash` from the `Documents` DTO.
- Computes a **current session hash** for a modified content string using `HashCalculator.calculateHash(modifiedContent)` and asserts that this differs from `originalStoredHash`.
- Calls `dao.updateFileInDB(id, fileName, pageNumber, modifiedContent)` for an appropriate page number.
- Reloads the document metadata and reads `storedHashAfterUpdate`.
- Asserts:
  - `storedHashAfterUpdate` **equals** `originalStoredHash`.

Interpretation:

- The test confirms exactly what the assignment describes:
  - The hash of the edited content (current session hash) changes.
  - The metadata stored in the `files` table retains the original import hash.

---

## 4. Presentation Layer Tests

Presentation-layer tests focus on simple, deterministic logic that supports the GUI. They live under:

- [Testing/presentation](Testing/presentation)

### 4.1 EditorPO Helper Methods

File:
- [Testing/presentation/EditorPOTest.java](Testing/presentation/EditorPOTest.java)

Target:
- `pl.EditorPO` (main GUI class, extends `JFrame`)

Since most of `EditorPO` is event-driven GUI code, the tests use reflection to access private helper methods that are responsible for updating labels and counts.

Tests:

1. **Word count logic:**
   - Uses reflection to invoke `calculateWordCount(String text)`.
   - Input: `"one two three four five"`.
   - Expects a result of `5`.

2. **Line count logic:**
   - Uses reflection to invoke `calculateLineCount(String content)`.
   - Input: `"line1\nline2\nline3"`.
   - Expects a result of `3`.

These tests demonstrate that the Presentation layer is also under test and that core GUI-related computations behave as expected.

---

## 5. Interface-Based / Swappable Design

The assignment emphasises writing tests against interfaces so that implementations can be swapped. This is reflected in multiple places:

- Business layer:
  - Tests use the `IEditorBO` interface as the primary type (e.g., `IEditorBO editor = new EditorBO(mockDao);`).
  - The façade dependency is injected via the `IFacadeDAO` interface, allowing the `FacadeDAOMock` stub to be substituted for the real DAO.
- Data layer:
  - Hash metadata test uses `IEditorDBDAO dao = new EditorDBDAO();`, keeping the test defined on the interface.
- DTOs (`Documents`, `Pages`) are accessed only through public getters, so internal structure can change without breaking tests.

This satisfies the **“swappable via interfaces”** requirement for Phase B.

---

## 6. Command Pattern Clarification

The assignment mentions testing the `execute()` methods of `ImportCommand`, `ExportCommand`, and `TransliterateCommand`. However, the **provided repository** does **not** include concrete classes with these names.

Instead:

- Import and transliteration behaviours are implemented directly in the **Business Layer** through the `IEditorBO` / `EditorBO` methods:
  - `importTextFiles(File file, String fileName)`
  - `transliterate(int pageId, String arabicText)`
- GUI actions call these methods via the business façade rather than via separate command classes.

Therefore, in this project version:

> The provided codebase does not contain concrete ImportCommand, ExportCommand, or TransliterateCommand classes. Instead, command-like behaviour is implemented directly through the Business Layer (IEditorBO). Therefore, execute-like logic has been tested via IEditorBO methods.

This note will be included in the written report so the instructor can see that the design of the **actual** codebase was respected rather than assuming non-existent classes.

---

## 7. Test Execution and Coverage

### 7.1 Tests Executed

The following JUnit classes are part of the Phase B test suite and currently all **pass**:

- Data layer:
  - [Testing/data/PaginationDAOTest.java](Testing/data/PaginationDAOTest.java)
  - [Testing/data/DatabaseConnectionTest.java](Testing/data/DatabaseConnectionTest.java)
  - [Testing/data/TFIDFCalculatorTest.java](Testing/data/TFIDFCalculatorTest.java)
  - [Testing/data/HashCalculatorTest.java](Testing/data/HashCalculatorTest.java)
  - [Testing/data/HashMetadataIntegrityTest.java](Testing/data/HashMetadataIntegrityTest.java)
- Business layer:
  - [Testing/business/SearchWordTest.java](Testing/business/SearchWordTest.java)
  - [Testing/business/EditorBOImportAndTransliterateTest.java](Testing/business/EditorBOImportAndTransliterateTest.java)
- Presentation layer:
  - [Testing/presentation/EditorPOTest.java](Testing/presentation/EditorPOTest.java)

A combined run of these classes currently reports:

- **28 tests passed, 0 failed.**

### 7.2 How Tests Are Run (for reproducibility)

In VS Code:

1. Open the **Testing** view (beaker icon in the Activity Bar).
2. Ensure the root `Testing` folder is detected as a JUnit test source.
3. Run all tests:
   - Click the "Run All Tests" button or run the specific test classes listed above.
4. The tree view will show green checkmarks next to each class and individual test method.

These views were used to capture screenshots for the report.

### 7.3 Coverage Evidence

Code coverage was collected by running the same set of tests in **coverage mode**. In VS Code:

1. Enable coverage in the Testing view (e.g., `Testing` → `Enable Code Coverage`).
2. Run the test suite again.
3. Open key source files to see line-level coverage overlays, for example:
   - [src/dal/PaginationDAO.java](src/dal/PaginationDAO.java)
   - [src/bll/SearchWord.java](src/bll/SearchWord.java)
   - [src/bll/EditorBO.java](src/bll/EditorBO.java)
   - [src/pl/EditorPO.java](src/pl/EditorPO.java)
4. Covered lines appear in green; uncovered lines (if any) in red. These overlays were used to provide coverage screenshots.

---

## 8. Phase B Summary

Mapping back to the Phase B requirements:

- **Separate testing package per layer:**
  - `Testing/business`, `Testing/data`, `Testing/presentation` mirror the 3-layer architecture.
- **Swappable / interface-based tests:**
  - Tests use `IEditorBO`, `IFacadeDAO`, and `IEditorDBDAO` interfaces as primary types.
- **Business layer logic & commands:**
  - Search (SearchWord), TF–IDF, import, and transliteration behaviours are tested via `IEditorBO` and helper classes.
- **Data persistence & hashing integrity:**
  - Hash algorithm and hash metadata behaviour are verified.
  - Database connection follows the Singleton pattern and is tested as such.
- **Presentation layer:**
  - GUI helper logic for word and line counts in `EditorPO` is covered by tests.
- **Execution & coverage:**
  - All Phase B tests pass (28/28), and coverage views confirm that the major business, data, and presentation paths under analysis are exercised.

Together, these elements provide a **complete, interface-oriented JUnit test suite** for Phase B, consistent with the given assignment specification.
