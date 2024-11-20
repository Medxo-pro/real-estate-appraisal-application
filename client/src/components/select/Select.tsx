import { useState, useEffect } from "react";
import "../../styles/main.css";
import { SelectInput } from "./SelectInput";
import { ViewData } from "./ViewData";
import {LoadInput} from "./LoadInput";
import { SearchInput } from "./SearchInput";
import { Message } from "./Message";

/**
 * A file interface to structure the files that have been loaded and are stored in the dropdown
 *
 * @params
 * name: the name of the file
 */
export interface fileEntry {
  name: string;
}

/**
 * A modal tab representing a note
 *
 * @params
 * id: a unique note id
 * title: the title of the note
 * content: main content of a note
 */
export interface Tab {
  id: number;
  title: string;
  content: string;
}

// mocked files
export const mockedFiles = [
  "Table A",
  "Table B",
  "Lost of Data",
  "Student Record",
  "Student Record State",
  "Plant Record",
  "Empty Table",
];

/**
 * Builds a Select component object that provides two textboxes for searching and loading files, a dropdown to
 * select a loaded file, an option to view the file as a table or chart, and searching functionality.
 *
 * @returns A JSX element that includes a dropdown, after selection, display the dataset in tabular form
 *
 */
export function Select() {
  // All loaded files + starting mocked files
  const [files, setFiles] = useState<fileEntry[]>([]);
  useEffect(() => {
    setFiles([
      { name: "Table A" },
      { name: "Table B" },
      { name: "Lost of Data" },
      { name: "Student Record" },
      { name: "Student Record State" },
      { name: "Plant Record" },
      { name: "Empty Table" },
    ]);
  }, []); 

  // The file selected to view
  const [file, setFile] = useState<fileEntry | undefined>();
  // Constant representing the format selected to view the file in
  const [format, setFormat] = useState<string>("");
  // The message to show the user
  const [message, setMessage] = useState<string>("Load files or select one from the dropdown to view it.")
  // The value to search in the file for
  const [searchVal, setSearchVal] = useState<string>("")

  return (
    <div className="min-h-[95vh] relative">
      <div className="w-full" style={{ width: "100%" }}>
        <div className="select-container" aria-label="Select container">
          <div className="load-search-container" aria-label="Load and Search container">
          <LoadInput files={files} setFiles={setFiles} setMessage={setMessage}/>
          <SearchInput file= {file} setSearchVal={setSearchVal} setMessage={setMessage}/>
          </div>
          <SelectInput files={files} file={file} setFile={setFile} format={format} setFormat={setFormat} setMessage={setMessage} setSearchVal={setSearchVal}/>
          <Message message={message}/>
          <pre>
            {file && (<ViewData file={file!} format={format} searchVal={searchVal} setSearchVal={setSearchVal} setMessage={setMessage}/>)}
          </pre>
        </div>
      </div>
    </div>
  );
}