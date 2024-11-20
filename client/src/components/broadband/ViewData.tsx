import "../../styles/main.css";
import "../../styles/index.css";
import "chart.js/auto";
import {Dispatch, SetStateAction, useEffect, useState} from "react";
import {fetchBroadband} from "../../csvService/FetchFuncs";

/**
 * A interface for the props that are passed into ViewData.
 *
 * @params
 * setMessage: function to edit message the user sees
 * broadbandData: an array storing the state and county the user wants to search
 */
interface ViewDataProps {
  setMessage: Dispatch<SetStateAction<string>>;
  broadbandData: string[];
}

/**
 * Builds a ViewData component that shows the data in the state the user inputs
 *
 * @param file object, format string, and searchVal (see ViewDataProps for more details)
 * @returns JSX that will show the data in the form the viewer requests
 */
export function ViewData(props: ViewDataProps) {
  const { setMessage, broadbandData } = props; // Destructure here if you need setMessage
  const [table, setTable] = useState<string[][]>([]);

  useEffect(() => {

    if (!broadbandData || broadbandData.length === 0 || !broadbandData[0] || !broadbandData[1]) {
      return;
    }
    const fetchData = async () => {
      try {
        const data = await fetchBroadband(broadbandData[0], broadbandData[1]);
        setMessage(`Broadband for country ${broadbandData[1]} in state ${broadbandData[0]} loaded.`)
        setTable(data);
      } catch (error) {
        console.error("Error fetching CSV:", error);
        setMessage(`Error, could not load broadband data. ${error}`);
      }
    };

    fetchData();
  }, [broadbandData]);

  return (
      <div className="view-data">
        <table border={1} aria-label="Broadband Data Table">
          <tbody>
          {table.map((row, rowIndex) => (
              <tr key={rowIndex}>
                {row.map((cell, cellIndex) => (
                    <td key={cellIndex}>{cell}</td>
                ))}
              </tr>
          ))}
          </tbody>
        </table>
      </div>
  );
}
