import React, { useEffect, useState } from 'react';
import "../styles/NavBar.css"

import { SignedIn,
    SignedOut,
    SignInButton,
    SignOutButton,
  } from "@clerk/clerk-react";
  
/**
 * Builds a navigation bar component for the top of the screen.
 *
 * @returns JSX for the navigation bar
 */
export function NavBar() {

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const timer = setTimeout(() => {
            setLoading(false);
        }, 1000);

        return () => clearTimeout(timer); 
    }, []);

    return (
        <header>
          <div className="nav-bar" aria-label='Navigation Bar'>
            <div className="title" aria-label='title'>
                <h1 aria-label="Data View Hub">📊 DataViewHub 📈 </h1>
            </div>
            <div className= "links">
                <nav>
                    <ul className="nav-links">
                        <li><a href="home" className="nav-link" aria-label='Home Link'>Home</a></li>
                        <SignedIn>
                            <li><a href="data" className="nav-link" aria-label='Data Link'>Data</a></li>
                            <li><a href="broadband" className="nav-link" aria-label='Broadband Link'>Broadband</a></li>
                        </SignedIn>
                    </ul>
                </nav>
                {loading ? (
                        <button className="loading-button" disabled aria-label="Sign-in">Loading</button>
                    ) : (
                        <>
                            <SignedOut>
                                <SignInButton />
                            </SignedOut>
                            <SignedIn>
                                <SignOutButton />
                            </SignedIn>

                        </>
                    )}
            </div>
          </div>
        </header>
    );
}