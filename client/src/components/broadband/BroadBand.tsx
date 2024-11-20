import { useState, useEffect } from "react";
import "../../styles/main.css";
import "../../styles/index.css";
import { BroadBandSearch } from "./SelectInput";
import { ViewData } from "./ViewData";
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

/**
 * Builds a BroadBand component object that provides two textboxes for searching and loading broadband,
 * a button to submit them, a message to keep the user informed, and a table to show the broadband
 *
 * @returns A JSX element that includes two textboxes, a button, text, and a table
 *
 */
export function BroadBand() {

  const [message, setMessage] = useState<string>("Please enter broadband data.")
  const [broadbandData, setBroadbandData] = useState<string[]>([]);

  return (
      <div className="min-h-[95vh] relative">
        <div className="w-full" style={{ width: "100%" }}>
          <div className="select-container" aria-label="Select container">
            <BroadBandSearch  setMessage={setMessage} setBroadbandData={setBroadbandData}/>
            <Message message={message}/>
            <pre>
              {<ViewData setMessage={setMessage} broadbandData={broadbandData}/>}
            </pre>
          </div>
        </div>
      </div>
  );
}