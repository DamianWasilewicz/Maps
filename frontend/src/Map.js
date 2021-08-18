import React, {useState, useRef, useEffect} from 'react';
import axios from 'axios';
import Canvas from "./Canvas";
import Route from "./Route";
import './Map.css'
import CheckinBox from "./CheckinBox";

function Map() {
    const [startLat, setStartLat] = useState(0);
    const [startLong, setStartLong] = useState(0);
    const [endLat, setEndLat] = useState(0);
    const [endLong, setEndLong] = useState(0);
    const [startRoute, setRoute] = useState([]);
    // const [checkinData, setRecentCheckin] = useState([]);
    // const [userSpecificData, setUserData] = useState([]);


    const [NWLat, setNWLat] = useState(41.8282);
    const [NWLong, setNWLong] = useState(-71.4052);
    const [SELat, setSELat] = useState(41.8212);
    const [SELong, setSELong] = useState(-71.3982);

    let counter = useRef(0);


    const tilesRef = useRef({});
    const tileSize = .005;
    const WIDTH = 500;
    const HEIGHT = 500;


    /**
     * Moved the function here to use the height parameter, gets ratio using formula from conceptual hours
     * and then multiplies it by width of canvas to get lat in terms of canvas coordinates
     * @param lat
     * @returns {number}
     */
    function convertLatToCanvas(lat) {
        let newLat = ((lat - NWLat) / (SELat - NWLat)) * HEIGHT;
        // console.log(convertCanvasToLat(lat))
        return newLat;
    }

    /**
     * Moved the function here to use the WIDTH parameter, gets ratio using formula from conceptual hours
     * and then multiplies it by width of canvas to get long in terms of canvas coordinates
     * @param long
     * @returns {number}
     */
    function convertLongToCanvas(long) {
        //TODO: Stay Sexy king - convert my Long :O -DED
        let newLong = ((long - NWLong) / (SELong - NWLong)) * WIDTH;
        // console.log(convertCanvasToLong(newLong));
        return newLong;
    }

    /**
     * function converting canvas coords to lat float on the map
     * @param canvas
     * @returns {number}
     */
    function convertCanvasToLat(canvas) {
        return ((SELat - NWLat) * (canvas / HEIGHT)) + NWLat;
    }

    /**
     * converts a canvas x to longitude value on the map
     * @param canvas coord
     * @returns {number}
     */
    function convertCanvasToLong(canvas) {
        let long = ((SELong - NWLong) * (canvas / WIDTH)) + NWLong;
        return long;
    }

    /**
     * Caching method for request
     * @param NWCoord
     * @returns {*}
     */
    async function getWays(NWCoord) {
        let tiles = tilesRef.current
        //if the tile is not cached
        if (!(NWCoord in tiles)) {
            //we request the data from the backend server
            let response = await requestWays(NWCoord)
            console.log("Reached after getWays");
            let ways = response.data['way']
            // console.log(ways)
            tilesRef.current.NWCoord = ways;//is this how you update a ref?
            return tilesRef.current.NWCoord;
        } else {
            console.log("Reached key in getWays");
            return tilesRef.current.NWCoord;
        }
    }

    /**
     * Given a tuple of coordinates,
     * make an actual request
     * @param NWCoord
     */
    const requestWays = (NWCoord) => {
        const toSend = {
            NWlat: NWCoord[0],
            NWlong: NWCoord[1],
            SElat: NWCoord[0] - tileSize,
            SElong: NWCoord[1] + tileSize,
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        return (
            axios.post(
                "http://localhost:4567/tile-ways",
                toSend,
                config
            ));

    }

    /**
     * Makes a request for coordinates of node nearest to inputted coordinates
     * @param lat
     * @param long
     * @returns {Q.Promise<void>}
     */
    async function getNearest(lat, long) {
        // debugger;
        let response = await requestNearest(lat, long);
        // debugger;
        let nearestCoords = response.data['nearest'];
        return nearestCoords;
    }

    // //
    // const requestNewRoute = (slat, slon, elat, elon) => {
    //     setStartLat(slat)
    //     setStartLong(slon)
    //     setEndLat(elat)
    //     setEndLong(elon)
    //     requestRoute()
    // }
    async function requestWrapper() {
        let response = await requestRoute();
        let routes = response.data['route'];
        setRoute(routes);
    }

    /**
     * Makes an axios request.
     */
    const requestRoute = () => {
        //TO FIX: At the moment, even though props are updated in textbox and in canvas, the first request is sending 0,0,0,0 - out of date, last cycle.
        // debugger
        const toSend = {
            //TODO: Pass in the values for the data. Follow the format the route expects!
            srclat: startLat,
            srclong: startLong,
            destlat: endLat,
            destlong: endLong
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        //Install and import this!
        //TODO: Fill in 1) location for request 2) your data 3) configuration
        console.log("Printing Request Route counter");
        console.log(counter.current);
        counter.current++;
        return (axios.post(
            "http://localhost:4567/route",
            toSend,
            config
        ));
    }

    /**
     * Makes request for nearest
     */
    const requestNearest = (lat, long) => {
        const toSend = {
            lat: lat,
            long: long,
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        };
        return (axios.post(
            "http://localhost:4567/nearest",
            toSend,
            config
        ));
    }



    return (
        <div className={"Map"}>
            <Canvas
                WIDTH={WIDTH} HEIGHT={HEIGHT} NWlat={NWLat} NWlong={NWLong} SElat={SELat}
                SElong={SELong} setNWLat={setNWLat} setNWLong={setNWLong}
                setSELat={setSELat} setSELong={setSELong}
                latToC={convertLatToCanvas} longToC={convertLongToCanvas}
                CToLat={convertCanvasToLat} CToLong={convertCanvasToLong}
                getWays={getWays} tileSize={tileSize} tiles={tilesRef.current}
                routes={startRoute} getNearest={getNearest} requestWrapperRoute={requestWrapper}
                setRoute={setRoute} setStartLat={setStartLat} setStartLong={setStartLong}
                setEndLat={setEndLat} setEndLong={setEndLong}
            />
            <h1>CS32Earth</h1>
            <Route
                startRoute={startRoute} setStartLat={setStartLat} setStartLong={setStartLong}
                setEndLat={setEndLat} setEndLong={setEndLong} requestWrapperRoute={requestWrapper}
                startLat={startLat} startLong={startLong} endLat={endLat} endLong={endLong}
            />
            <CheckinBox

            />
        </div>
    )
    ;
}

// setStartLat={setStartLat}
// setStartLong={setStartLong} setEndLat={setEndLat} setEndLong={setEndLong}
// startLat={startLat} startLong={startLong} endLat={endLat} endLong={endLong}

export default Map;



