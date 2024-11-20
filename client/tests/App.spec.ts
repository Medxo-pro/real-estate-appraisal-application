import { expect, test } from "@playwright/test";
import {setupClerkTestingToken, clerk, clerkSetup} from '@clerk/testing/playwright';
import * as dotenv from 'dotenv';
import {test as setup} from "playwright/types/test";
dotenv.config({ path: '.env' });



test.beforeEach(async ({ page }) => {
  setupClerkTestingToken({ page });
  const signInUrl = 'http://localhost:8000/home';
  await page.goto(signInUrl);
  await clerk.loaded({ page });
  const loginButton = page.getByRole("button", { name: "Sign in" })
  await expect(loginButton).toBeVisible();

  // This logs in/out via _Clerk_, not via actual component interaction. But that's OK.
  // (Clerk's Playwright guide has an example of filling the login form itself.)
  await clerk.signIn({
    page,
    signInParams: {
      strategy: "password",
      password: process.env.E2E_CLERK_USER_PASSWORD!,
      identifier: process.env.E2E_CLERK_USER_USERNAME!,
    },
  });
});

async function signInUser({ page }) {
  setupClerkTestingToken({ page });
  const signInUrl = 'http://localhost:8000/home';
  await page.goto(signInUrl);
  await clerk.loaded({ page });

  const loginButton = page.getByRole("button", { name: "Sign in" });
  await expect(loginButton).toBeVisible();

  await clerk.signIn({
    page,
    signInParams: {
      strategy: "password",
      password: process.env.E2E_CLERK_USER_PASSWORD,
      identifier: process.env.E2E_CLERK_USER_USERNAME,
    },
  });
}

// Verifies that the login button is visible on page load.
test("On page load, I see a login button", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await expect(page.getByLabel("Sign-in")).toBeVisible();
  await clerk.signOut({ page });
});

// Ensures the input box and sign-out button are hidden before login and appear after logging in.
test("On page load, I dont see the input box until login", async ({ page }) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();

  await expect(page.getByText("Welcome to DataViewHub")).toBeVisible();
  await clerk.signOut({ page });
});



// Confirms that the submit button ("Retrieve Table") is visible after logging in.
test("On page load, I see a submit button", async ({ page }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await expect(page.getByText("Retrieve")).toBeVisible();
  await clerk.signOut({ page });
});


// Checks if the default message asking to select a table or load is visible after login. DATA.
test("On login and Data, I see the message 'Load files or select one from the dropdown to view it.`", async ({
  page,
}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();
  await page.getByLabel("Data Link").click();
  await expect(page.getByText("Load files or select one from the dropdown to view it.")).toBeVisible();
  await clerk.signOut({ page });
});

// Checks if the default message asking to select a table or load is visible after login. Broadband.
test("On login and Data, I see the message 'Please enter broadband data.`", async ({
                                                                                                               page,
                                                                                                             }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();
  await page.getByLabel("Broadband Link").click();
  await expect(page.getByText("Please enter broadband data.")).toBeVisible();
  await clerk.signOut({ page });
});



// Verifies that selecting an empty table (Empty Table) shows the 'No data available' message.
test("On submit of an empty table (Empty Table), I see the message 'No data available for the selected history`.", async ({
  page,
}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Empty Table");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText("Viewing dataset Empty Table as a Table.")).toBeVisible();
  await clerk.signOut({ page });
});


// Confirms that the selected table (Table A) is displayed after clicking the 'Retrieve Table' button.
test("On table retrieval (Table A), I see the table", async ({
  page,
}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Table A");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByText('Viewing dataset Table A as a Table.')).toBeVisible();
  await clerk.signOut({ page });
});



// Does the whole process of entering the password, logging in, and retrieving a table, multiple times.
test("Combined operations with Login and Sign Out", async ({
  page,
}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Table A");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByLabel('Viewing dataset Table A as a Table.')).toBeVisible();

  await clerk.signOut({ page });
  await signInUser({ page })
  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Table B");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByLabel('Viewing dataset Table B as a Table.')).toBeVisible();
  await clerk.signOut({ page });

});



// Sprint 3.2 Tests


// Does the whole process of entering the password, logging in, and retrieving a table, multiple times.
test("On switching Table A to Table B, I see TableB", async ({
                                                             page,
                                                           }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("table-dropdown")).not.toBeVisible();
  await page.getByLabel("Data Link").click();

  await page.getByLabel('table-dropdown').selectOption("Table A");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByLabel('Viewing dataset Table A as a Table.')).toBeVisible();

  await page.getByLabel('table-dropdown').selectOption("Table B");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();
  await expect(page.getByLabel('Viewing dataset Table B as a Table.')).toBeVisible();
  await clerk.signOut({ page });

});



// Tests to see if the bar chart get generated after using clicks on it.
test("On changing table type to bar chart, I see the bar chart", async ({
                                                               page,
                                                             }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Table A");

  await page.getByLabel('format-dropdown').selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Table A as a Bar Chart.')).toBeVisible();
  await expect(page.getByText('Skipped Attributes: Table A')).toBeVisible();
  await clerk.signOut({ page });
});



test("On selecting table with erroneous data, I see the message 'Skipped Attributes'", async ({
                                                                          page,
                                                                        }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Student Record");

  await page.getByLabel('format-dropdown').selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Student Record as a Bar Chart.')).toBeVisible();
  await expect(page.getByText('Skipped Attributes')).toBeVisible();
  await expect(page.getByText('Skipped Attributes: Student Record')).toBeVisible();
  await clerk.signOut({ page });

});


test("On selecting table (Student Record State) with erroneous data, I see the message 'Skipped Attributes' and 'State'", async ({
                                                                                                page,
                                                                                              }) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Plant Record");

  await page.getByLabel('format-dropdown').selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Plant Record as a Bar Chart.')).toBeVisible();
  await expect(page.getByText('Skipped Attributes')).toBeVisible();
  await expect(page.getByText('Type     Water Needs     Sunlight     ')).toBeVisible();
  await expect(page.getByText('Skipped Attributes: Plant Record')).toBeVisible();
  await clerk.signOut({ page });

});


test("On changing table the display format of table multiple times, I see the expected format", async ({
                                                                          page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel('table-dropdown').selectOption("Table A");

  await page.getByLabel('format-dropdown').selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Table A as a Bar Chart.')).toBeVisible();

  await page.getByLabel('format-dropdown').selectOption("Table");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();

  await page.getByLabel('format-dropdown').selectOption("Stacked Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Table A as a Stacked Bar Chart.')).toBeVisible();

  await page.getByLabel('table-dropdown').selectOption("Plant Record");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Plant Record as a Stacked Bar Chart.')).toBeVisible();
  await clerk.signOut({ page });

});


// Sprint 4.1 Tests

test("On login,  I see the load button and text box", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await expect(page.getByLabel('text box loading')).toBeVisible();
  await expect(page.getByLabel('load button')).toBeVisible();
  await clerk.signOut({ page });

});


test("On loading the ten-star server file, I see ten-star in the dropbox", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("students");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("students");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByLabel('students')).toBeVisible();
  await clerk.signOut({ page });

});



 //This test loads multiple server files.
 //Also load mock files in between.
 //Tries them all with different formats (tables, bar charts, stacked bar charts)
 // logs out and in repeatedly.

test("Multi test", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  //Log in and load a server file.
  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("students");
  await page.getByLabel("load button").click();

  //Show the same server file in a table.
  await page.getByLabel("table-dropdown").selectOption("students");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Table.')).toBeVisible();

  //Show the same server file in a bar chart.
  await page.getByLabel("format-dropdown").selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Bar Chart.')).toBeVisible();

  //Show the same server file in stacked bar chart.
  await page.getByLabel("format-dropdown").selectOption("Stacked Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Stacked Bar Chart.')).toBeVisible();

  //Show the same server file in a table again.
  await page.getByLabel("format-dropdown").selectOption("Table");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();

  //Select a mock data file and test its being shown correctly.
  await page.getByLabel("table-dropdown").selectOption("Plant Record");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Plant Record as a Table.')).toBeVisible();

  //Change the format of the newly selected file to Table.
  await page.getByLabel("format-dropdown").selectOption("Table");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByRole('table')).toBeVisible();

  //Loads a new server file.
  await page.getByLabel("text box loading").fill("ten-star");
  await page.getByLabel("load button").click();

  //Show the newly loaded server file in a table.
  await page.getByLabel("table-dropdown").selectOption("ten-star");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset ten-star as a Table.')).toBeVisible();

  //Logs out.
  await clerk.signOut({ page });
  //Log in again.
  await signInUser({ page });


  //Load a new server file and show it.
  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("headers_are_nums");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("headers_are_nums");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset headers_are_nums as a Table.')).toBeVisible();
  await clerk.signOut({ page });

});



test("On searching for a value, I get a table", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("students");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("students");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("1001");
  await page.getByLabel("search button").click();
  await expect(page.getByText('Showing matches found for 1001 in students')).toBeVisible();
  await clerk.signOut({ page });

});


test("On searching for a non-existent value, I do not get a table but an error message", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("students");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("students");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("Bob");
  await page.getByLabel("search button").click();
  await expect(page.getByLabel("No matches found for bob in students")).toBeVisible();
  await clerk.signOut({ page });

});


// Alternating between successful and unsuccessful searches for Mock and Server files.
test("Multi Searching", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("students");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("students");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset students as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("Bob");
  await page.getByLabel("search button").click();
  await expect(page.getByText("No matches found for bob in students")).toBeVisible();


  await page.getByLabel("text box loading").fill("ten-star");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("ten-star");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText("Viewing dataset ten-star as a Table.")).toBeVisible();
  await page.getByLabel("text box search").fill("Barnard's Star");
  await page.getByLabel("search button").click();
  await expect(page.getByText("Showing matches found for Barnard's Star in ten-star")).toBeVisible();

  // Keep server data with unsuccessful search:
  await page.getByLabel("text box search").fill("Does not exist");
  await page.getByLabel("search button").click();
  await expect(page.getByText('No matches found for Does not exist in ten-star')).toBeVisible();
  await clerk.signOut({ page });

});


test("Testing Search on mocked data", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("table-dropdown").selectOption("Table A");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Table A as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("Test search argument");
  await page.getByLabel("search button").click();
  await expect(page.getByText('Cannot search in data already provided. You must load and select your own file to search in.')).toBeVisible();
  await clerk.signOut({ page });

});


test("Testing Search on mocked data and then switching to server data", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("table-dropdown").selectOption("Table A");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset Table A as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("Test search argument");
  await page.getByLabel("search button").click();
  await expect(page.getByText('Cannot search in data already provided. You must load and select your own file to search in.')).toBeVisible();

  await page.getByLabel("text box loading").fill("ten-star");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("ten-star");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset ten-star as a Table.')).toBeVisible();

  await page.getByLabel("text box search").fill("Sol");
  await page.getByLabel("search button").click();
  await expect(page.getByText("Showing matches found for Sol in ten-star")).toBeVisible();
  await clerk.signOut({ page });
});


test("Testing Search on server data with tables", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Data Link").click();
  await page.getByLabel("text box loading").fill("income_by_race");
  await page.getByLabel("load button").click();
  await page.getByLabel("table-dropdown").selectOption("income_by_race");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset income_by_race as a Table.')).toBeVisible();

  await page.getByLabel('format-dropdown').selectOption("Bar Chart");
  await page.getByLabel("Retrieve").click();
  await expect(page.getByText('Viewing dataset income_by_race as a Bar Chart.')).toBeVisible();

  await page.getByLabel("text box search").fill("Asian");
  await page.getByLabel("search button").click();
  await expect(page.getByText("Showing matches found for Asian in income_by_race")).toBeVisible();
  await clerk.signOut({ page });
});

// Sprint 4.2 BroadBand testing


test("Testing Broadband searching with valid input", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Broadband Link").click();
  await page.getByLabel("text box state").fill("Florida");
  await page.getByLabel("text box county").fill("Broward County");

  await page.getByLabel("broadband button").click();

  await expect(page.getByText('Broadband for country Broward County in state Florida loaded.')).toBeVisible();
  await expect(page.getByLabel("Broadband Data Table")).toBeVisible();
  await expect(page.getByRole("table")).toBeVisible();
  await clerk.signOut({ page });
});


test("Testing Broadband searching with invalid input", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Broadband Link").click();
  await page.getByLabel("text box state").fill("Invalid");
  await page.getByLabel("text box county").fill("Invalid");

  await page.getByLabel("broadband button").click();

  await expect(page.getByText('Error, could not load broadband data. FetchError: 404 Not Found')).toBeVisible();
  await clerk.signOut({ page });
});


test("Testing Broadband searching valid State, invalid County", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Broadband Link").click();
  await page.getByLabel("text box state").fill("Florida");
  await page.getByLabel("text box county").fill("");

  await page.getByLabel("broadband button").click();

  await expect(page.getByText('Please input a non-empty County')).toBeVisible();
  await clerk.signOut({ page });
});

test("Testing Broadband searching valid County, invalid State", async ({page,}) => {
  await expect(page.getByLabel("Sign Out")).not.toBeVisible();
  await expect(page.getByLabel("dropdown")).not.toBeVisible();

  await page.getByLabel("Broadband Link").click();
  await page.getByLabel("text box state").fill("");
  await page.getByLabel("text box county").fill("Broward County");

  await page.getByLabel("broadband button").click();

  await expect(page.getByText('Please input a non-empty State')).toBeVisible();
  await clerk.signOut({ page });
});