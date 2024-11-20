import { Dispatch, SetStateAction } from "react";
import { useState, useEffect } from "react";
import "../styles/App.css";
import "../styles/index.css";
import "../styles/main.css";

/**
 * An interface for logged-in state for mock.
 *
 * @params
 * isLoggedIn: true if the user is logged in, false otherwise
 * setIsLoggedIn: to update the state of isLoggedIn
 */
interface loginProps {
  isLoggedIn: boolean;
  setIsLoggedIn: Dispatch<SetStateAction<boolean>>;
}

/**
 * Builds a component that manage the login button and end-user's logged in state.
 *
 * @param props to access logged-in state (see interface loginProps for more details)
 * @returns JSX to let user know they can sign out if they are logged in
 *  or log-in if they are not logged in
 */
export function LoginButton(props: loginProps) {
  /**
   * Function to manage authentication;
   *  if the user is logged in, the user's log-in state will update to not logged in
   *  if the user is not logged in, the user's log-in state will update to logged in
   *
   * @returns whether they are logged in or not
   */
  const [password, setPassword] = useState("");
  const [placeHolder, setplaceHolder] = useState("Enter password");

  
  const authenticate = () => {
    const pass = "aaa"
    if (password == pass){
      const newValue = !props.isLoggedIn;
      props.setIsLoggedIn(newValue);
      return newValue;
    }
    else if (password == ""){
      setplaceHolder("Password field is empty.")
    }
    else if (password != ""){
      setPassword("")
      setplaceHolder("Password is incorrect.")
    }
  };

  const handleSignOut = () => {
    setPassword("");
    authenticate();
  };

  if (props.isLoggedIn) {
    return (
      <button className="sign-out-button" aria-label="Sign Out" onClick={handleSignOut} >
        Sign out
      </button>
    );
  } else {
    return (
      <div className= "login-container">
      <span>
        <input
        className="password-box"
        type="password"
        aria-label="password"
        placeholder= {placeHolder}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
        <button aria-label="Login" onClick={authenticate}>
          Login
        </button>
      </span>
      </div>
    );
  }
}
