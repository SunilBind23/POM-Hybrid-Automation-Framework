# POM Hybrid Automation Framework
## Structure exactly as defined in interview document

## Project Structure

```
com.AutomationFramework/
├── src/main/java/
│   ├── OrLayer/               ← LAYER 2: Object Repository
│   │   ├── LoginOr.java       → All Login page WebElements (private + getters)
│   │   └── DashboardOr.java   → All Dashboard WebElements (private + getters)
│   │
│   ├── Pages/                 ← LAYER 3: Page Wise Layer
│   │   ├── LoginPage.java     → Login actions (extends LoginOr)
│   │   └── DashboardPage.java → Dashboard actions (extends DashboardOr)
│   │
│   ├── tests/                 ← LAYER 4: Test Case Layer
│   │   └── RunTest.java       → All @Test methods (TC_001 to TC_007)
│   │
│   └── utils/                 ← LAYER 1: Utility/Generic Layer
│       └── WebUtil.java       → All reusable Selenium wrapper methods
│
├── src/main/resources/
│   ├── testdata/
│   │   └── LoginDataForAutomation.xlsx   ← Excel test data
│   ├── config.properties                 ← App URL, browser config
│   └── testng.xml                        ← TestNG suite runner
│
├── reports/                   ← ExtentReport HTML generated here
├── screenshots/               ← Failure screenshots saved here
├── test-output/               ← TestNG default output
└── pom.xml                    ← Maven dependencies
```

---

## The 4 Layers — Explained

### Layer 1: Utility Layer (WebUtil.java)
- Contains ALL reusable generic methods (click, type, wait, scroll, etc.)
- All methods use try-catch for stability and traceability
- Method Overloading used (Polymorphism): frames, waits, scroll methods
- `driver` is **private** → accessed via `getDriver()` (Encapsulation)

### Layer 2: Object Repository (OR) Layer
- One class per page (LoginOr, DashboardOr, etc.)
- All WebElements declared as **private** (Data Hiding)
- **Getter method** for each WebElement
- `PageFactory.initElements(driver, this)` in constructor

### Layer 3: Page Wise Layer
- One class per page (LoginPage, DashboardPage, etc.)
- **Extends OR Layer** (Inheritance) to get WebElements
- Each page functionality = one method
- Calls WebUtil methods (not raw Selenium)

### Layer 4: Test Case Layer (RunTest.java)
- `@BeforeMethod` → Setup (Report + Browser + URL + Page init)
- `@AfterMethod`  → Teardown (quit browser + flush report)
- `@Test`         → Each individual test case
- Reads data from Excel via WebUtil
- Calls Page Layer methods only

---

## OOP Concepts Used

| Concept | Where |
|---------|-------|
| **Encapsulation** | WebDriver private in WebUtil; WebElements private in OR layer |
| **Data Hiding** | All variables private, accessed via getters |
| **Inheritance** | LoginPage extends LoginOr; DashboardPage extends DashboardOr |
| **Polymorphism** | Method overloading in WebUtil (frames, waits, scroll) |

---

## Excel Test Data Format

Create `LoginDataForAutomation.xlsx` with Sheet: `Login`

| TestCaseID | UserName | Password | ExpectedResult |
|------------|----------|----------|----------------|
| TC_001     | Admin    | admin123 | Pass           |
| TC_002     | Admin    | wrong123 | Fail           |
| TC_007     | Admin    | admin123 | Pass           |
| TC_007     | wrong    | wrong    | Fail           |

---

## How to Run

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=RunTest

# Run with specific browser (add @Parameters in testng.xml)
mvn test -Dbrowser=chrome
```

---

## Extent Report
After execution, open: `reports/ExtentReport.html`

Shows:
- Total / Passed / Failed / Skipped count
- Step-by-step logs for each test
- Failure screenshots attached
- Pie chart summary
- System info (OS, Browser, etc.)
