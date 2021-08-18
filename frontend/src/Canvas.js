import React, {useEffect, useRef, useState} from 'react';


//map state -
function Canvas(props) {
    const canvasRef = useRef();
    let zoomedCounter = useRef(0);
    let counter = useRef(0);
    let nearestFirstCoords = useRef([]);
    let nearestSecondCoords = useRef([]);
    let mouseDownLat = useRef(0);
    let mouseDownLong = useRef(0);
    let canvas;
    let ctx;
    let scrollFactor = .001;
    let ongoing = false;
    let maxNumZoomIn = 1;
    let maxNumZoomOut = -1;
    let boolean = false;
    let panningGate = false;
    let mouseUpGate = false;


    /**
     * Draw border for the canvas panel
     * This of this as a constructor
     */
    useEffect(() => {
        console.log("Starting drawMap");
        drawMap();
        drawBorder();
    }, []);


    useEffect(() => {
        console.log("Reached coordinate drawMap")
        drawMap()
    }, [props.SElong])

    /**
     * Draw routes when a request is made via the textbox panel
     */
    useEffect(() => {
        //forEach takes in every element of list route, which should be a way, and
        //passes it in as a parameter to drawRoute implicitly
        console.log("Redrawing shortest path");
        // drawMap(); //redraw our map to clear arcs
        drawShortestRoute(); //draw  new shortest route
        // drawBorder(); //
        // props.routes.forEach(drawRoute)
    }, [props.routes])

    const handleMouseDown = event => {
        console.log("Registered Mouse Down");
        mouseDownLat.current = props.CToLat(event.pageY);
        mouseDownLong.current = props.CToLong(event.pageX);
    }

    const handleMouseUp = event => {
        console.log("Registered Mouse Up");
        let newX = props.CToLong(event.pageX);
        let newY = props.CToLat(event.pageY);

        let deltaX = mouseDownLong.current - newX;
        let deltaY = mouseDownLat.current - newY;
        let newNWlat = props.NWlat + deltaY;
        let newNWlong = props.NWlong + deltaX;
        let newSElat = props.SElat + deltaY;
        let newSElong = props.SElong + deltaX;
        props.setNWLong(newNWlong);
        props.setSELat(newSElat);
        props.setSELong(newSElong);
        props.setNWLat(newNWlat);
    }

    /**
     * Method that handles zooming. Get how much the user
     * zoomed in/out by using deltaY; this will update
     * the zoom factor by some amount (decrease value if zooming out,
     * increase value if zooming in) - this value will be used to update
     * the NW and SE boxing values stored in Route using the
     * update state functions passed in as props
     */
    const handleZoom = event => {
        // debugger
        let scrollDirection = event.deltaY;
        if (scrollDirection > 0 && zoomedCounter.current <= maxNumZoomIn) {
            zoomedCounter.current += 1;
            props.setNWLat(props.NWlat - scrollFactor);
            props.setNWLong(props.NWlong + scrollFactor);
            props.setSELat(props.SElat + scrollFactor);
            props.setSELong(props.SElong - scrollFactor);
            // drawMap();
            // drawShortestRoute();
            // drawBorder();
        } else if (scrollDirection < 0 && zoomedCounter.current >= maxNumZoomOut) {
            zoomedCounter.current -= 1;
            props.setNWLat(props.NWlat + scrollFactor);
            props.setNWLong(props.NWlong - scrollFactor);
            props.setSELat(props.SElat - scrollFactor);
            props.setSELong(props.SElong + scrollFactor);
            // drawMap();
            // drawShortestRoute();
            // drawBorder();
        }

    }


    /**
     * Helper function for drawing border around map
     */
    function drawBorder() {
        canvas = canvasRef.current;
        ctx = canvas.getContext('2d');
        ctx.strokeStyle = 'black';
        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(props.WIDTH, 0);
        ctx.moveTo(props.WIDTH, 0);
        ctx.lineTo(props.WIDTH, props.HEIGHT);
        ctx.moveTo(props.WIDTH, props.HEIGHT);
        ctx.lineTo(0, props.HEIGHT);
        ctx.moveTo(0, props.HEIGHT);
        ctx.lineTo(0, 0);
        ctx.stroke();
        ctx.closePath();
    }

    /**
     *  Given a set of northwest coordinates, fetch ways from getWays method (which either
     *  calls request ways or uses cache
     * @param tileCoords
     * @returns {Q.Promise<void>}
     */
    async function drawTile(tileCoords) {
        try {
            let ways = await props.getWays(tileCoords);
            ways.forEach(drawWays);//TODO: why for each is showing an error?
        } catch (err) {
            console.log("Error" + err);
        }
    }

    /**
     * Draws a set of ways associated with a tile. Converts coordinates from the ways
     * using the conversion coordinates, draws using lineto
     * @param ways
     */
    function drawWays(ways) {
        canvas = canvasRef.current;
        ctx = canvas.getContext('2d');
        ctx.beginPath();
        ctx.strokeStyle = "black"
        if (ways[2][1] == "unclassified" || ways[2][1] == "") {
            ctx.strokeStyle = "grey"
        }
        if (ways[2][1] == "highway") { //TODO: fix this with the sql
            ctx.strokeStyle = "blue"
        }
        let node1Lat = props.latToC((ways[0][0]));
        if (node1Lat < 0) {
            node1Lat = 0;
        }
        let node1Long = props.longToC(ways[0][1]);
        if (node1Long < 0) {
            node1Long = 0;
        }
        let node2Lat = props.latToC(ways[1][0]);
        if (node2Lat > props.HEIGHT) {
            node2Lat = props.HEIGHT;
        }
        let node2Long = props.longToC(ways[1][1]);
        if (node2Long > props.WIDTH) {
            node2Long = props.WIDTH;
        }
        ctx.moveTo(node1Long, node1Lat);//notice we moveTo each time.
        ctx.lineTo(node2Long, node2Lat);
        ctx.stroke();
        ctx.closePath()
        //TODO:later can play with color of line based on way.type.
        //Hey nadav you sexy dawg ;3 hope you're well king
        //DAMIAN you're a king, and oh darling this code is oozing sex
    }


    /**
     * Function called to draw map upon loading. Calculate bounds of nested for loop to iterate through
     * proper amount of tiles.
     */
    const drawMap = () => {
        console.log("Drawing Map");
        canvasRef.current.getContext('2d').clearRect(0, 0, props.WIDTH, props.HEIGHT)
        //TODO - iterate through appropriate tiles - figure out bounds for iteration
        //the idea is to find the tile that the NWcoordinate of our canvasView is at, then
        //iterate east and south and draw all the ways in all the tiles that are within our canvasView bound.
        const latOffset = props.NWlat % props.tileSize;
        const lonOffset = props.NWlong % props.tileSize;
        const nwtileLat = (props.NWlat - latOffset) + props.tileSize;
        const nwtileLong = (props.NWlong - lonOffset) - props.tileSize;
        // let counter = 0;
        //draw all ways in tiles within view
        for (let i = nwtileLat; i >= props.SElat; i -= props.tileSize) { //lat decreasing as we go south
            for (let j = nwtileLong; j <= props.SElong; j += props.tileSize) { //lon increases going east
                //JAVASCRIPT help,. how do we make sure that the method is called before we try and draw?
                // console.log(i);
                // console.log(j);
                drawTile([i, j]);
                // counter++;
            }
        }
        //redraw border just in case
        drawBorder();
    }


    /**
     * Function drawing the route between the selected coordinates.
     */
    const drawShortestRoute = () => {
        console.log("checkpoint: drawShortestRoute");
        canvas = canvasRef.current;
        ctx = canvas.getContext('2d');
        ctx.beginPath();
        ctx.strokeStyle = 'red';
        ctx.lineWidth = 4;
        let ways = props.routes; //TODO: is there a reason it would rerender?
        for (let index in ways) {
            // <ListItem key={way[0].toString()} value={way} />
            let node1Lat = props.latToC(parseFloat(ways[index][0]));
            console.log("1" + ways[index][0]);
            let node1Long = props.longToC(parseFloat(ways[index][1]));
            console.log("2" + ways[index][1]);
            let node2Lat = props.latToC(parseFloat(ways[index][2]));
            console.log("3" + ways[index][2]);
            let node2Long = props.longToC(parseFloat(ways[index][3]));
            console.log("4" + ways[index][3]);
            ctx.moveTo(node1Long, node1Lat);//notice we moveTo each time.
            ctx.lineTo(node2Long, node2Lat);
            console.log("drawing route");
        }
        ctx.stroke();
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 0.5;
        ctx.closePath();
    }


    /**
     * Function drawing the route between the selected coordinates.
     */
    async function clickHandler(event) {//seperate events to on drag, on click, set a boolean to indicate if something is dragging
        console.log("clicking!")
        // console.log("counter is =" + counter.current)
        // debugger;
        //first retrieve the location of the click on the map
        // debugger;

        //convert click location to lat lon points on the map
        let lat = props.CToLat(event.pageY);
        let long = props.CToLong(event.pageX);
        let Clat = props.latToC(lat);
        let Clong = props.longToC(long);
        console.log("lat " + lat)
        console.log("long " + long)
        //if first double Click, then we select a point and render a circle around that point
        if (boolean == false) {
            console.log("cp1!")
            //TODO: first clear the previous path
            //store the selection
            console.log("Start lat");
            console.log(lat);
            console.log("Start long");
            console.log(long);
            nearestFirstCoords.current = [];
            nearestSecondCoords.current = [];
            //indicate the point has been selected by drawing a circle around it
            drawMap();
            canvas = canvasRef.current;
            ctx = canvas.getContext('2d');
            let nearestCoords = await props.getNearest(lat, long);
            // debugger;
            nearestFirstCoords.current = nearestCoords;
            let cLat = props.latToC(nearestCoords[0]);
            let cLong = props.longToC(nearestCoords[1]);
            ctx.beginPath();
            ctx.strokeStyle = 'red';
            ctx.arc(cLong, cLat, 10, 0, 2 * Math.PI);
            ctx.stroke();
            ctx.closePath();
            ctx.strokeStyle = 'black';
            boolean = true;
        }
        // from the prev point to the curr.
        else if (boolean == true) {
            // else if (counter.current % 2 == 1) {
            //         debugger
            console.log("cp2!")
            console.log("End lat");
            console.log(lat);
            console.log("End long");
            console.log(long);
            //store the variables
            //now make the request to draw the route - should render automatically
            let nearestCoords = await props.getNearest(lat, long);
            nearestSecondCoords.current = nearestCoords;
            let cLat = props.latToC(nearestCoords[0])
            let cLong = props.longToC(nearestCoords[1]);
            props.setStartLat(nearestFirstCoords.current[0]);
            props.setStartLong(nearestFirstCoords.current[1]);
            props.setEndLat(nearestSecondCoords.current[0]);
            props.setEndLong(nearestSecondCoords.current[1]);
            props.requestWrapperRoute();
            canvas = canvasRef.current;
            ctx = canvas.getContext('2d');
            ctx.beginPath();
            ctx.strokeStyle = 'red';
            ctx.arc(cLong, cLat, 10, 0, 2 * Math.PI);
            ctx.stroke();
            ctx.closePath();
            boolean = false;

        }

    }




    return (
        <div>
            <canvas onWheel={(event) => {
                event.preventDefault();
                if (!ongoing) {
                    console.log("Entered Zoom if condition");
                    ongoing = true;
                    setTimeout(() => {
                        console.log("Entered timeout");
                        handleZoom(event);
                        setTimeout(() => {
                            ongoing = false;
                        }, 2000);
                    }, 2000);
                }
            }} onDoubleClick={(event) => {
                console.log("double clicking!")
                clickHandler(event);
            }}
                    onMouseDown={(event) => {
                        if(!panningGate) {
                            panningGate = true;
                            setTimeout(() => {
                                handleMouseDown(event);
                                setTimeout(() => {
                                    panningGate = true;
                                }, 2000);
                            }, 2000);
                        }
                    }}
                    onMouseUp={(event) => {
                        if(!mouseUpGate) {
                            mouseUpGate = true;
                            setTimeout(() => {
                                handleMouseUp(event);
                                setTimeout(() => {
                                    mouseUpGate = false;
                                }, 2000);
                            }, 2000);
                        }
                    }}
                    ref={canvasRef} width={props.WIDTH} height={props.HEIGHT}>
            </canvas>
        </div>
    );


}

export default Canvas;