import React, { useState } from 'react';
import TextBox from "./TextBox";
import { AwesomeButton } from "react-awesome-button";
import "react-awesome-button/dist/styles.css";

function Route(props) {

    return (
        <div className="Route">
            <header className="Route-">
                <p>Instructions: double click on the map or select 4 coords or street crossings, then click the button to draw route. Example input (Waterman Street,Prospect Street,Angell Street,Hope Street)</p>
                <TextBox label={'Source latitude'} change={props.setStartLat} value={props.startLat}/>
                <TextBox label={'Source longitude'} change={props.setStartLong} value={props.startLong}/>
                <TextBox label={'Destination latitude'} change={props.setEndLat} value={props.endLat}/>
                <TextBox label={'Destination latitude'} change={props.setEndLong} value={props.endLong}/>
                <AwesomeButton type="primary" onPress={props.requestWrapperRoute}>Find Route</AwesomeButton>
            </header>
        </div>
    );
}

export default Route;