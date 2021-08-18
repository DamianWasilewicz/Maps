import React, {useEffect, useState} from 'react';
import UserDisplayBox from "./UserDisplayBox";
import axios from "axios";

function CheckinBox() {
    const [checkinData, setRecentCheckin] = useState([]);
    const [userSpecificData, setUserData] = useState([]);
    const [getCurrUser, setCurrUser] = useState(0);
    let mapCopy = [];

    /**
     * Request specific user data based on state update
     */
    useEffect(() => {
        requestUserCheckins(getCurrUser)
    }, [getCurrUser]);

    /**
     * Makes request to the server to get recent user checkins
     */
    const requestCheckins = () => {
        // debugger
        console.log("calling request checkins")
        const toSend = {};
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        };
        axios.post(
            "http://localhost:4567/checkin",
            toSend,
            config
        ).then(response => {
            console.log(response);
            let checkins = response.data['checkin'];
            console.log("Checkins " + checkins);
            // checkinMap.current = checkins;
            setRecentCheckin(checkins)
            // return response.json()
        })
            .catch(function (error) {
                console.log(error);
            })
    }

    /**
     * Makes request for user-specific data to be called onClick by checkin elements
     */
    const requestUserCheckins = (userId) => {
        const toSend = {
            userId: userId
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        };
        axios.post(
            "http://localhost:4567/userCheckins",
            toSend,
            config
        )
            .then(response => {
                // console.log(response.data['userCheckins']);
                //TODO: Go to the Main.java in the server from the stencil, and find what variable you should put here.
                setUserData(response.data['userCheckins']);
                console.log("testing if passed" + checkinData)

            })
            .catch(function (error) {
                console.log(error);
            })
    }

    function mapCheckinData() {
        for (let elem in checkinData) {
            console.log("Ran iteration" + elem);
            return <UserDisplayBox name={elem.name} id={elem.id} lat={elem.lat}
                                   long={elem.long} click={setCurrUser(elem.id)}/>
        }
    }

    function mapUserCheckinData() {
        for (let elem in getCurrUser) {
            return <UserDisplayBox name={elem.name} id={elem.id} lat={elem.lat} long={elem.long} click={clear}/>
        }
    }

    //ideally this will call the query to delete user data to adhere by gdpr regulation
    function clear() {

    }

    /**
     * Request recent user checkin data based on interval
     */
    useEffect(() => {
        const interval = setInterval(() => {
            requestCheckins();
        }, 3000);
        // setInterval(requestCheckins, 3000);//update user checkins every 3 seconds
    }, []);


    //set component with scrolling functionality
    return (
        <div>
            <div style={{
                overflowY: "auto",
                overflowX: "auto",
                height: 400,
                width: 800,
                borderStyle: "solid",
                position: "relative"
            }}>
                {mapCheckinData()}
            </div>
            <div style={{
                overflowY: "auto",
                overflowX: "auto",
                height: 200,
                width: 800,
                borderStyle: "solid",
                position: "relative"
            }}>
                {mapUserCheckinData()}
            </div>
        </div>

    );
}

export default CheckinBox;