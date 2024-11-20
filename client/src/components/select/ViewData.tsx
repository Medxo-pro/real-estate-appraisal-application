import "../../styles/main.css";
import "../../styles/index.css";
import { mockedFiles, fileEntry } from "./Select";
import { getTable } from "../../mockData";
import { fetchViewCSV, fetchSearchCSV } from "../../csvService/FetchFuncs"
import { Bar } from "react-chartjs-2";
import "chart.js/auto";
import {Dispatch, SetStateAction, useEffect, useState} from "react";

/**
 * A interface for the props that are passed into ViewData.
 *
 * @params
 * file: the file selected to load
 * format: the format to load the file in
 * searchVal: the value to search in the file for
 * setSearchVal: function to update the value to search for
 * setSearchVal: function to update the message to display
 */
interface ViewDataProps {
  file: fileEntry;
  format: string;
  searchVal: string;
  setSearchVal: Dispatch<SetStateAction<string>>;
  setMessage: Dispatch<SetStateAction<string>>;
}

/**
 * Builds a ViewData component that shows the data in the state the user inputs
 *
 * @param file object, format string, and searchVal (see ViewDataProps for more details)
 * @returns JSX that will show the data in the form the viewer requests
 */
export function ViewData(props: ViewDataProps) {
  const {searchVal, setSearchVal, setMessage, file, format} = props;
  // The table to show, as a 2D array
  const [table, setTable] = useState<string[][] | undefined>(undefined);
  // Attributes skipped (used in malformed data)
  const [skippedAttributes, setSkippedAttributes] = useState<Set<string>>(new Set());

  // Use effect hook for fetching data to view
  useEffect(() => {
    const fetchData = async () => {
      setSkippedAttributes(new Set());
      let fetchedTable: string[][] | undefined ;

      if (mockedFiles.includes(file.name)) { //If it's a mocked file
        fetchedTable = getTable(file.name);
        setTable(fetchedTable); //Set the Table hook to display
        return

      } else { // If it's not a mocked file
        try {
          if (props.searchVal === "") { 
            // If we're not searching for anything
            fetchedTable = await fetchViewCSV(file.name);
            if (fetchedTable.length === 0) {
              setMessage(`No data available for ${file.name}`)
            }
            setTable(fetchedTable);
          } else {
            fetchedTable = await fetchSearchCSV(file.name, props.searchVal);
            if (fetchedTable.length === 0) {
              setMessage(`No matches found for ${searchVal} in ${file.name}`);
            } else {
              setMessage(`Showing matches found for ${searchVal} in ${file.name}`);
              setTable(fetchedTable);
            }
          }
        } catch (error) {
          console.error("Error fetching CSV:", error);
        }
      }
    };
    fetchData();
  }, [file, props.searchVal]);

  if (!table) {
    return <h3 aria-label="Empty Table"></h3>;
  }

  // Function that checks for invalid (non-numerical or null) values in charts so users can know
  // which attributes aren't being displayed
  function invalidAdd(table: string[][], Index: number, attribute: string) {
    // Checks that attributes are numeric for chart forms
    table.slice(1).forEach(row => {
      const value = Number(row[Index]);
      if (isNaN(value)) {
          skippedAttributes.add(attribute + " ");
      }
  });
}

  function prepareChartDataFromTable(table: string[][]) {
    const labels = table.slice(1).map(row => row[0]); // Gets first column of labels skipping first element.
    const attributes = table[0].slice(1); // Gets all other attributes like GPA, or age.
    // Sets colors
    const colors = [
      "#FF6F61", "#FF8A3D", "#FFD700", "#6BBE45", "#4F9DFF", "#D35DB0", "#FF6F40", "#C2A366", "#68B6D9", "#A3C1E0",
    ];

    const datasets = attributes.map((attribute, index) => {
      const Index = table[0].indexOf(attribute); // Get the index of the current attribute
      
      // Checks for invalid values
      invalidAdd(table, Index, attribute)

      return {
        label: attribute,
        backgroundColor: colors[index % colors.length],
        data: table.slice(1).map(row => Number(row[Index]))
      };
    });

    return {labels, datasets};
  }

  const {labels, datasets} = prepareChartDataFromTable(table);

  const stackedBarOptions = {
    plugins: {
      title: {
        display: true,
      },
    },
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {stacked: true},
      y: {stacked: true},
    },
  };

  return (
      <div className="view-data">

        {format === 'Bar Chart' ? (
            <div>
              <h3>Skipped Attributes: {file.name}</h3>
              <ul>
                {skippedAttributes}
              </ul>
              <div>
              <Bar
                  data={{
                    labels: labels,
                    datasets: datasets,
                  }}
                  options={{
                    plugins: {
                      legend: {
                        display: true,
                        onClick: (e, legendItem, legend) => {
                          const index = legendItem.datasetIndex;
                          const chart = legend.chart;
                          chart.getDatasetMeta(Number(index)).hidden = !chart.getDatasetMeta(Number(index)).hidden;
                          chart.update();
                        },
                      },
                    },
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                      x: {stacked: false},
                      y: {stacked: false},
                    },

                  }}
                  aria-label={"Bar Chart"}
                  className="bar-chart"              
              />
              </div>
            </div>


        ) : format === 'Stacked Bar Chart' ? (
              <div>
                <h3>Skipped Attributes:</h3>
                <ul>
                  {skippedAttributes}
                </ul>
                <div>
                <Bar data={{
                  labels,
                  datasets
                }}
                     options={stackedBarOptions}
                     aria-label={"Stacked Bar Chart"}
                     className="stacked-bar-chart"

                />
                </div>
              </div>
        ) : (

            <table
                border={1}
                aria-label={file.name}
            >
              <tbody>
              {table.map((row, rowIndex) => (
                  <tr key={rowIndex}>
                    {row.map((cell, cellIndex) => (
                        <td
                            key={cellIndex}
                        >
                          {cell}
                        </td>
                    ))}
                  </tr>
              ))}
              </tbody>
            </table>
        )}
      </div>
  );
}
