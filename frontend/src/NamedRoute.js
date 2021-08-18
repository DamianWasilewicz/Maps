import React from 'react';
import TextBox from "./TextBox";
import { AwesomeButton } from "react-awesome-button";
import "react-awesome-button/dist/styles.css";

function NamedRoute(props) {
    return(
        <div className="Route">
            <header className="Route-">
                <TextBox label={'Source name1'}/>
                <TextBox label={'Source name2'} />
                <TextBox label={'Destination name1'} />
                <TextBox label={'Destination name2'} />
                <AwesomeButton type="primary">Button</AwesomeButton>
            </header>
        </div>
    );
}
export default NamedRoute;
