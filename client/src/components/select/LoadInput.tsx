import { Dispatch, SetStateAction, useState, useEffect } from "react";
import "../../styles/main.css";
import "../../styles/index.css";
import { FetchError, formatErrorMessage, loadCSV } from "../../csvService/FetchFuncs";
import { fileEntry } from "./Select";


/**
 * A interface for the props that are passed into AddInput.
 *
 * @params
 * files: the array storing all loaded files
 * setFiles: function to add a new file to the files array
 * setMessage: function to set the message the user sees
 */
interface LoadInputProps {
  files: Array<fileEntry>;
  setFiles: Dispatch<SetStateAction<Array<fileEntry>>>;
  setMessage: Dispatch<SetStateAction<string>>;
}

/**
 * Builds a LoadInput component that contains a textbox where you can submit files
 * and a button which adds the file name in the text box to the dropdown (if the file loads).
 *
 * @param props files array, setFiles function, and setMessage function (see AddInputProps for more details)
 *        as well as a searchVal and setSearchVal function
 * @returns JSX that will show the textbox and a load button
 */
export function LoadInput(props: LoadInputProps) {
  // Unpack our props
  const { files, setFiles, setMessage } = props;

  // Variable used to track the current value in the textbox (file to be loaded)
  const [currentValueLoad, setCurrentValueLoad] = useState<string>("");

  /**
   * Ensures the currentValue state reflects the current value in the text box by updating it each time the
   * user changes the value in the textbox.

   * @param {React.ChangeEvent<HTMLInputElement>} e - The event object
   * that contains information about the change event, including the
   * target element and its new value.
   */
  const updateValueLoad = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCurrentValueLoad(e.target.value)
  };

  /**
   * Handles the loading of a file when the user presses the load file button.
   * Checks for duplicate files, adds the new file to the list of files if it sucessfully
   * loads and is not a duplicate. Makes a call to loadCSV to do this, and updates the message 
   * to reflect whether the file loaded or not.
   */
  const handleLoad = () => {
    // Update the user
    setMessage(`Loading file ${currentValueLoad}...`)

    // Make sure we actually inputted a file to load
    if (currentValueLoad == ""){
      setMessage(`Please input a non-empty file name to load`)
    } else {
      // Created a newFile object for the file
      let newFile : fileEntry = {
        name: currentValueLoad.trim(),
      };
          
      // Check if it's already been loaded, and if so, stop functionality
      const isDuplicate = props.files.some((file) => file.name === currentValueLoad);

      if (isDuplicate) {
        setMessage(`File "${newFile.name}" has already been loaded.`)
        setCurrentValueLoad('');
        return;
      }

      // Otherise, run the functionality to load the file
      loadCSV(newFile.name).then(() => {
        // If we succeed, add the file to loaded files and update the message
        setMessage(`File "${newFile.name}" loaded.`);
        setFiles([...files, newFile]);
      }).catch((error: FetchError) => {
        // If we fail, only update the message
        setMessage(`${formatErrorMessage(error.responseType)}: ${error.message}`);
      });
      
      // Reset the load file textbox
      setCurrentValueLoad('');
    }
  };

  return (
      <div className= "load-components-container">
        <input
            className= "text-box"
            name="text box loading"
            id="text box loading"
            aria-label="text box loading"
            type="text"
            placeholder="Enter a file name"
            value={currentValueLoad}
            onChange={updateValueLoad}
            />
        <button onClick={handleLoad}
                aria-label="load button"> Load
        </button>
      </div>
  );
}