import React from 'react';
import "../styles/index.css"
import "../styles/Home.css"

export function Home() {
  return (
    <div className="home-message-container">
      <p>Welcome to DataViewHub, a multi-tool project created by Sana Saab and Mehdi Atmani! To use this application,
        please sign in with your @brown.edu Google Account using the button in the top right. Then, feel free to navigate to the data or 
        broadband endpoints to try out our features! </p>
    </div>
  );
};

export default Home;
