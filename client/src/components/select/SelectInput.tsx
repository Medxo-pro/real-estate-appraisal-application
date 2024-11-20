import { Dispatch, SetStateAction, useState, useEffect, useRef } from "react";
import "../../styles/main.css";
import { fileEntry } from "./Select";

interface SelectInputProps {
  files: Array<fileEntry>;
  file: fileEntry | undefined;
  setFile: Dispatch<SetStateAction<fileEntry | undefined>>;
  format: string;
  setFormat: Dispatch<SetStateAction<string>>;
  setMessage: Dispatch<SetStateAction<string>>;
  setSearchVal: Dispatch<SetStateAction<string>>;
}

export function SelectInput(props: SelectInputProps) {
  const [currentValueFile, setCurrentValueFile] = useState<string>("");
  const [currentValueFormat, setCurrentValueFormat] = useState<string>("Table");

  const fileDropdownRef = useRef<HTMLSelectElement | null>(null);
  const formatDropdownRef = useRef<HTMLSelectElement | null>(null);

  useEffect(() => {
    if (props.files.length > 0) {
      setCurrentValueFile(props.files[0].name); // Set initial value to the first file
    }
  }, [props.files]);

  const handleFileChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = e.target.value;
    setCurrentValueFile(selectedValue);
  };

  const handleFormatChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = e.target.value;
    setCurrentValueFormat(selectedValue);
  };

  const handleSubmit = () => {
    if (currentValueFile && currentValueFormat) {
      const selectedFile = props.files.find(file => file.name === currentValueFile);
      props.setSearchVal('');
      props.setFile(selectedFile);
      props.setFormat(currentValueFormat);
      props.setMessage(`Viewing dataset ${currentValueFile} as a ${currentValueFormat}.`);
    }
  };

  const adjustDropdownWidth = (dropdown: HTMLSelectElement) => {
    if (dropdown.options.length > 0 && dropdown.selectedIndex >= 0) {
      const tempSpan = document.createElement('span');
      tempSpan.style.visibility = 'hidden';
      tempSpan.style.whiteSpace = 'nowrap';
      tempSpan.style.font = getComputedStyle(dropdown).font;
      tempSpan.textContent = dropdown.options[dropdown.selectedIndex]?.text; // Use optional chaining

      document.body.appendChild(tempSpan);
      dropdown.style.width = `${tempSpan.offsetWidth + 20}px`; 
      document.body.removeChild(tempSpan);
    }
  };

  useEffect(() => {
    const adjustWidth = () => {
      if (fileDropdownRef.current) {
        adjustDropdownWidth(fileDropdownRef.current);
      }
    };

    adjustWidth(); // Call to adjust width on mount
  }, [currentValueFile]); // Adjust when currentValueFile changes

  useEffect(() => {
    const adjustWidth = () => {
      if (formatDropdownRef.current) {
        adjustDropdownWidth(formatDropdownRef.current);
      }
    };

    adjustWidth(); // Call to adjust width on mount
  }, [currentValueFormat]); // Adjust when currentValueFile changes

  return (
    <div className="dropdown-container">
      I'd like to see  
      <select
        ref={fileDropdownRef}
        className="dropdown file-dropdown"
        name="dropdown"
        id="dropdown"
        aria-label="table-dropdown"
        value={currentValueFile}
        onChange={handleFileChange}
      >
        {props.files.map((item) => (
          <option key={item.name} value={item.name.trim()}> 
            {item.name} 
          </option>
        ))}
      </select>
      formatted as a 
      <select
        ref={formatDropdownRef}
        className="dropdown format-dropdown"
        name="dropdownFormat"
        id="dropdownFormat"
        aria-label="format-dropdown"
        value={currentValueFormat}
        onChange={handleFormatChange}
      >
        <option value="Table">Table</option>
        <option value="Bar Chart">Bar Chart</option>
        <option value="Stacked Bar Chart">Stacked Bar Chart</option>
      </select>
      <button
        className="retrieve-button"
        onClick={handleSubmit}
        aria-label="Retrieve"
      >
        Retrieve
      </button>
    </div>
  );
}
