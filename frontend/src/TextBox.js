import React from 'react';
function TextBox(props) {
    const updateText = (event) => {
        props.change(event.target.value);
    }
    return (
        <div className="textBox">
            <label>{props.label}</label><br/>
            <input type={'text'} onChange={updateText} value={props.value} ></input>
        </div>
    );
}

export default TextBox;