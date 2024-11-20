# Project Details

**Project Name**: Connected Website
**Project Description**:

A website for a real estate appraisal application. The application allows users to load and view various datasets (from a specific folder). It is designed to be responsive, functioning effectively on both mobile devices and desktop screens. The key functionalities included

- selecting datasets from a dropdown menu and displaying them in a scrollable table format or bar chart format
- allowing the website to be reasonably accessible on multiple devices
- searching in this data for various values
- screen size adaptation

This project also includes backend functionality...

- for accessing broadband data using mocked and real api calls and caching
- a command line application for loading and searching files in specific columns or the whole file
- a parser to parse data into various objects using creators
- a server which users can use to search files, load files, view files, and make broadband requests

This project involved the use of TypeScript and event programming, testing with playwright, CSS and HTML, and mocking data.

**Team Members and Contributions**:

- **Sana Saab** (cslogin: sgsaab)
- **Mehdi Atmani** (cslogin: matmani)

**Total Estimated Time**: Approximately 45 hours

**Repository Link**: [GitHub Repository](https://github.com/cs0320-f24/server-sguarisma-sgsaab)

# Design Choices

## Frontend

We used a parent class "Select" to manage most of our functionality. This had three subclasses: loadInput, SelectInput, and SelectHistory, each of which have their own functionality:  
loadInput is responsible for loading the files, the load Button, and inputting values to search.
SelectInput: Responsible for the Submit button, the dropdown, and choosing the format of the data.  
ShowInput: Responsible for showing the data table or chart, searching, and viewing.

We used dependency injection to connect all the three component classes in Select.

## Backend

### Parser, searcher, and command line application

- Parser package containing Parser class, classes package (classes that data is parsed into) and
  creators package (used to parse the data). The creators classes inherit from the CreatorFromRow
  interface.
- The Searcher is an object which has once constructor that parses the file given, and three methods
  which search with a colName, colIndex, and no column identifier.
  - We structured the class like this because we wanted to be able to search in one data set multiple
    times without repeatedly parsing, and we wanted to avoid confusion over whether "20" was a
    columnIndex or name. The second concern is why we used "N" and "I" as prefixes to column
    identifiers in my command line application as well
- In our command line application, we made it so the user could search in one file multiple times if
  they wanted to and seperated each piece of information into its own input. we was concerned about
  splitting on spaces or commas because we didn't want there to be issues when someone wanted to search
  for a value with a space/column or search in a column name with a space in it.

### Server and broadband

- Server package...
  - contains a package with all the csvclasses (titled csvfuncs) that do the work
    requested by the endpoints
  - contains a package with all the handlers that handle the endpoints and requests
  - contains a server class which uses dependency injection to pass a shared state to the handlers
    and csv functions
- Datasource package... (the below items are mainly used to make dependency injection possible)
  - Contains the datasource exception class
  - Contains the broadband package, which is home to all classes pertaining to receiving broadband
    data from the census API. This contains...
    - ACSAPIBroadBandDatasource, a class which implements BroadBandDatasource and handles fetching the real data from the API
    - Location, a record used to determine where we're fetching data from
    - BroadBandDatasource, an interface which is implemented by all classes which fetch broadband
      data (real or fake, caching or not) and can be inputted into the broadbandhandler
    - BroadBandData, the record containing the retrieved data that is returned by methods which
      fetch data
    - CachingBroadBandDatasource, a class which implements BroadBandDatasource and acts as a proxy
      between itself and another broadbanddatasource class while implementing caching.

# Errors/Bugs

The following error occurs when running tests in the backend, but the program continues uninterrupted:
Sep 28, 2024 2:32:56 PM org.junit.platform.launcher.core.DefaultLauncher handleThrowable
WARNING: TestEngine with ID 'junit-vintage' failed to discover tests
org.junit.platform.commons.JUnitException: Failed to parse version of junit:junit: 4.13.1
TestCaching passes in java, but not with mvn package (?)

# Tests

We wrote playwright tests for frontend interactions and viewing data, as well as JUnit tests for backend functionality.

# How to

## How to setup and run

Once cloned:

### Setup REPL from root directory

`npm install` — Installs node_modules folder for dependencies

`npx playwright install` — Installs everything needed to run PlayWright

`npm install @clerk/clerk-react` — Installs everything needed for clerk authentication

`npm install react-router-dom` — Installs everything needed for react routing

### Running Frontend

`npm start` — This starts a local server at port 8000 that compiles your code in real time. TODO explain

### Running Backend

`node server\src\main\java\edu.brown.cs.student.main\server\Server.java` — This starts a local server at port 3232 that mocks a very simple backend with one static JSON output. Once it is run, feel free to visit 'http://localhost:3232' to see what gets returned. TODO explain

_You may have to open multiple terminals to run the frontend and backend simultaneously_

### Running tests with Playwright

`npx playwright test` — Runs tests

`npx playwright show-report` — Shows a code breakdown of test progressions

`npx playwright test --ui`— Opens a UI that allows you to watch and trace your (failing) tests live in a browser

`npx playwright codegen <url>` — Opens a URL and generates tests with locators for elements on the page.

### Running the Command Line Processor

- Open the repl as a project in IntelliJ (nagivate to the pom.xml file to open)
- Switch to the SearchCommandProcessor.java file
- run it using the IntelliJ IDE.

### Running the backend Server

- Open the repl as a project in IntelliJ (nagivate to the pom.xml file to open)
- Switch to the Server.java file
- run it using the IntelliJ IDE.
- Click the local host link provided
- Enter the url to make load, view, and search requests (examples can be found in handler files)

# Collaboration

**Collaborators**:
OpenAI. (2023). ChatGPT (September 24 version) [Large language model].
https://chat.openai.com/chat/  
ChatGPT was used to brainstorm test ideas and check our Typescript syntax.  
Also used heavily for help with dropdown size adjustments based on items in them

[Stack Overflow](https://stackoverflow.com/questions/58772186/can-i-exclude-an-individual-test-from-beforeeach-in-junit5)
We used stackoverflow to research excluding specific tests from before each statements.

Livecode and gearup both used to help structure and write code.
