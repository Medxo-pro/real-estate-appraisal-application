import {Dispatch, SetStateAction, useEffect, useState} from "react";
import "../../styles/main.css";
import "../../styles/index.css";


/**
 * A interface for the props that are passed into AddInput.
 *
 * @params
 * setMessage: function to set the message the user sees
 * setBroadBand: the array storing the requested state and county
 */
interface BroadBandProps {
  setMessage: Dispatch<SetStateAction<string>>;
  setBroadbandData: Dispatch<SetStateAction<string[]>>;
}

/**
 * Builds a BroadbandSearch component that contains a two textboxes where you can submit the state and county
 * you'd like to retrieve broadband for a button which submits them.
 *
 * @param props files setMessage function and setBroadBandData function (see BroadBandProps for more details)
 *        as well as a searchVal and setSearchVal function
 * @returns JSX that will show the textbox and a load button
 */
export function BroadBandSearch(props: BroadBandProps) {
  // Unpack our props
  const {setMessage } = props;

  // Variable used to track the current value in the textbox (file to be loaded)
  const [currentState, setCurrentState] = useState<string>("");
  const [currentCounty, setCurrentCounty] = useState<string>("");

  /**
   * Ensures the currentState state reflects the current value in the text box by updating it each time the
   * user changes the value in the textbox.

   * @param {React.ChangeEvent<HTMLInputElement>} e - The event object
   * that contains information about the change event, including the
   * target element and its new value.
   */
  const updateState = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCurrentState(e.target.value)
  };


  /**
   * Ensures the currentCounty state reflects the current value in the text box by updating it each time the
   * user changes the value in the textbox.

   * @param {React.ChangeEvent<HTMLInputElement>} e - The event object
   * that contains information about the change event, including the
   * target element and its new value.
   */
  const updateCounty = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCurrentCounty(e.target.value)
  };

  /**
   * Handles the loading of a file when the user presses the load file button.
   * Checks for duplicate files, adds the new file to the list of files if it sucessfully
   * loads and is not a duplicate. Makes a call to loadCSV to do this, and updates the message
   * to reflect whether the file loaded or not.
   */
  const handleLoad = () => {
    // Make sure we actually inputted a State to look up.
    if (currentState === "" && currentCounty === ""){
      console.log(1)
      setMessage(`Please input non-empty State and County fields`);
      return
    } else if (currentState == ""){
      setMessage(`Please input a non-empty State`);
      setCurrentCounty('');
      return
    } else if (currentCounty == ""){
      setMessage(`Please input a non-empty County`)
      setCurrentState('');
      return
    } else {
      setMessage(`Looking up broadband data for country ${currentCounty} in state ${currentState}...`)
      props.setBroadbandData([currentState, currentCounty])
      setCurrentState('');
      setCurrentCounty('');
    }
  };

  return (
      <div className= "broadband-components-container">
        <input
            className= "text-box"
            name="text box state"
            id="text box state"
            aria-label="text box state"
            type="text"
            placeholder="Enter a State"
            value={currentState}
            onChange={updateState}
        />
        <input
            className= "text-box"
            name="text box county"
            id="text box county"
            aria-label="text box county"
            type="text"
            placeholder="Enter a County"
            value={currentCounty}
            onChange={updateCounty}
        />
        <button onClick={handleLoad} className="broadband-button"
                aria-label="broadband button"> Look up
        </button>
      </div>
  );
}