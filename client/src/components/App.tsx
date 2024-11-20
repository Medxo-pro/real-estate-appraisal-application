import "../styles/App.css";
import "../styles/index.css";
import "../styles/main.css";
import "../styles/NavBar.css"

import { SignedIn } from "@clerk/clerk-react";

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './Home';
import {Select} from "./select/Select";
import { BroadBand } from "./broadband/BroadBand";
import {NavBar} from "./NavBar";


/**
 * This is the highest level of Mock which builds the component APP;
 *
 * @return JSX of the entire mock
 *  Note: if the user is loggedIn, the main interactive screen will show,
 *  else it will stay at the screen prompting for log in
 */
function App({}: { children: React.ReactNode; modal: React.ReactNode }) {
  /**
   * A state tracker for if the user is logged in and
   *  a function to update the logged-in state
   */

  document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
      const focusedElement = document.activeElement;
      if (focusedElement){
        if (focusedElement instanceof HTMLButtonElement){
        focusedElement.click();
        } 
      }
    }
  });

  return (
    <Router>
    <div className="App">
      <NavBar />
      <Routes>
        <Route path="/home" element={<Home />} />
        <Route path="/data" element={<SignedIn><Select /></SignedIn>} />
        <Route path="/broadband" element={<SignedIn><BroadBand /></SignedIn>} />
        <Route path="/" element={<Home />} />
      </Routes>
      <div id="modal-root"></div>
    </div>
    </Router>
  );
}

export default App;
