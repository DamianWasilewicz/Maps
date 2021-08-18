import {useState} from "react/cjs/react.production.min";

function UserDisplayBox(props){
    const [elemUser, setElemUser] = useState([]);
    return (
        <div style = {{width: 650, padding: 15, borderStyle:"black", borderWidth: 1, border:"solid pink 3px"}}>
            <div> [{props.id}] {props.name} checked in from ({props.lat},{props.long}) </div>

        </div> );
}
export default UserDisplayBox;