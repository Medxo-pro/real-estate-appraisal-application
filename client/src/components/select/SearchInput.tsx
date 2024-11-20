import { Dispatch, SetStateAction, useState, useEffect } from "react";
import { mockedFiles, fileEntry } from "./Select";
import "../../styles/main.css";
import "../../styles/index.css";

/**
 * A interface for the props that are passed into SearchInput.
 *
 * @params
 * setSearchVal: function to update the value to search for
 * setMessage: function to set the message the user sees
 * file: the current file being viewed
 */
interface SearchInputProps {
  file: fileEntry | undefined
  setSearchVal: Dispatch<SetStateAction<string>>;
  setMessage: Dispatch<SetStateAction<string>>;
}

/**
 * Builds a SearchInput component that contains a textbox where you can write what you'd like to search
 * and a button which submits this search value and conducts the search.
 *
 * @param props setSearchVal function and setMessage function, as well as the file being viewed
 *  (see SearchInputProps for more details) 
 * @returns JSX that will show the textbox and a search button
 */
export function SearchInput(props: SearchInputProps) {
  // Unpack our props
  const {file, setSearchVal, setMessage} = props;

  // Variable used to track the current value in the search textbox (item to be searched for)
  const [currentValueSearch, setCurrentValueSearch] = useState<string>("");

  /**
   * Ensures the currentValueSearch state reflects the current value in the search 
   * text box by updating it each time the user changes the value in the textbox.

   * @param {React.ChangeEvent<HTMLInputElement>} e - The event object
   * that contains information about the change event, including the
   * target element and its new value.
   */
  const updateValueSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCurrentValueSearch(e.target.value)
  };

  /**
   * Function to run when search button is pressed. Updates the value to search for the parent function
   * and updates the message for the user.
   */
  const handleSearch = () => {
    if (!file){
      setMessage(`Please select a file to search in.`)
    } else if (!currentValueSearch){
      setMessage(`Please input a non-empty value to search for.`)
    } else if (mockedFiles.includes(file.name)) {
      setMessage(`Cannot search in data already provided. You must load and select your own file to search in.`)
    } else {
      setSearchVal(currentValueSearch)
      setMessage(`Searching for ${currentValueSearch}...`)
      setCurrentValueSearch("")
    }
  };

  return (
      <div className= "search-components-contanier">
        <input
            className= "text-box"
            name="text box search"
            id="text box search"
            aria-label="text box search"
            type="text"
            placeholder="Enter a search value"
            value={currentValueSearch}
            onChange={updateValueSearch}
        />
        <button onClick={handleSearch}
                aria-label="search button"> Search
        </button>
      </div>
  );
}