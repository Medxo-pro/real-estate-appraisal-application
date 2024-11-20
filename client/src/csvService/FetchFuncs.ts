// fileFunctions.ts

/**
 * A custom error class for fetch-related errors.
 */
export class FetchError extends Error {
    constructor(public responseType: string, message?: string) {
        super(message);
        this.name = "FetchError";
    }
}

/**
 * Converts an error response type to a user-friendly message.
 * @param errorType - The error response type string (e.g., 'error_file_not_found').
 * @returns A formatted error message (e.g., 'Error file not found').
 */
export function formatErrorMessage(errorType: string): string {
    return errorType
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

// Generic fetch function
const fetchData = async (url: string): Promise<any> => {
    const response = await fetch(url);
    if (!response.ok) {
        const errorData = await response.json();
        throw new FetchError(errorData.response_type, `${response.status} ${response.statusText}`);
    }
    return await response.json();
};

// Function to load a CSV file from the backend
export const loadCSV = async (fileName: string) => {
    await fetchData(`http://localhost:3232/loadcsv?filepath=data/resources/${fileName}.csv`);
};

// Function fetch a loaded CSV file's data
export const fetchViewCSV = async (fileName: string): Promise<string[][]> => {
    const data = await fetchData(`http://localhost:3232/viewcsv?filename=${fileName}`);
    return JSON.parse(data.responseMap.data);
};

// Function fetch a loaded CSV file's data when searched
export const fetchSearchCSV = async (fileName: string, searchVal: string): Promise<string[][]> => {
    const data = await fetchData(`http://localhost:3232/searchcsv?filename=${fileName}&searchKey=${searchVal}`);
    return JSON.parse(data.responseMap.data);
};

// Function fetch a broadband data
export const fetchBroadband = async (state: string, county: string): Promise<string[][]> => {
    county = county.replace(/ /g, "%20");
    const data = await fetchData(`http://localhost:3232/broadband?state=${state}&county=${county}`);
    const responseMap = data.responseMap;
    const twoDArray = [
        ["date", "county code", "broadband", "state & county", "state code"],
        [responseMap.date, responseMap["county code"], responseMap.broadband, responseMap.name, responseMap["state code"]]
    ];
    return twoDArray;
};


