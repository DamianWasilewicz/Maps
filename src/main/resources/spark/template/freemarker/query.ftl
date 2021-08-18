<#assign content>

    <head>
        <script>
            function toggle() {
                if (document.getElementById("commandChoice").value === "naive_radius"
                    || document.getElementById("commandChoice").value === "radius") {
                    document.getElementById("toggle").innerHTML = "radius:";
                } else if (document.getElementById("commandChoice").value === "naive_neighbors"
                    || document.getElementById("commandChoice").value === "neighbors") {
                    document.getElementById("toggle").innerHTML = "number of neighbors:";
                }
            }

            function hide() {
                if (document.getElementById("searchChoice").value === "--select--") {
                    document.getElementById("Coords").style.display = "none";
                    document.getElementById("Name").style.display = "none";
                } else if (document.getElementById("searchChoice").value === "byName") {
                    document.getElementById("Coords").style.display = "none";
                    document.getElementById("Name").style.display = "";
                } else if (document.getElementById("searchChoice").value === "byCoords") {
                    document.getElementById("Coords").style.display = "";
                    document.getElementById("Name").style.display = "none";
                }
            }

        </script>
    </head>
    <h1 id="querytitle"> STARS QUERY</h1>
    <p>
        <#--    <form id="commandChosen" method="POST" action="/results">-->
        <#--        <input type="radio" id="radius" name="command" value="radius">-->
        <#--        <label for="radius">radius</label>-->
        <#--        <input type="radio" id="neighbors" name="command" value="neighbors">-->
        <#--        <label for="neighbors">neighbors</label><br>-->
        <#--    </form>-->
        <#--    <br>-->
    <form id="requestForm" method="POST" action="/results">
        <#-- stars data options-->
        <div id="starsDataFiles">
            <label for="filepath"> Select file: </label><br>
            <select name="filepath" for="filepath" id="filepath">
                <option value="select">--select--</option>
                <option value="data/stars/ten-star.csv">ten-star.csv</option>
                <option value="data/stars/three-star.csv">three-star.csv</option>
                <option value="data/stars/tie-star.csv">tie-star.csv</option>
                <option value="data/stars/stardata.csv">stardata.csv</option>
            </select>
        </div>
        <#-- stars command options-->
        <div id="CommandType">
            <label for="command"> Select search option: </label><br>
            <select name="command" for="command" id="commandChoice" onchange="toggle()">
                <option value="select">--select--</option>
                <optgroup label="standard">
                    <option value="radius">radius</option>
                    <option value="neighbors">neighbors</option>
                </optgroup>
                <optgroup label="naive">
                    <option value="naive_radius">radius</option>
                    <option value="naive_neighbors">neighbors</option>
                </optgroup>
            </select>
        </div>
        <#-- stars k or r options-->
        <div id="RorK">
            <label for="rk" id="toggle"> </label> <br>
            <textarea name="rk"></textarea><br>
        </div>
        <#-- stars search options-->
        <div id="SearchType">
            <label for="search"> Select search option: </label><br>
            <select name="search" for="search" id="searchChoice" onchange="hide()">
                <option value="select">--select--</option>
                <option value="byName">Search by Name</option>
                <option value="byCoords">Search by Coords</option>
            </select>
        </div>
        <#-- stars coordinates options-->
        <div id="Coords">
            <textarea name="x" id="x" placeholder="x value"></textarea><br>
            <textarea name="y" id="y" placeholder="y value"></textarea><br>
            <textarea name="z" id="z" placeholder="z value"></textarea><br>
        </div>
        <#-- stars name options-->
        <div id="Name">
            <textarea name="nameField" id="nameField" placeholder="name of star"></textarea><br>
        </div>
    <br><br>
        <input id="submitButton" type="submit" value="run query">
        <#--        <input type="textarea" id="filepath" name="filepath" placeholder="enter filepath here">-->
        <#--        <label for="text">Insert arguments here:</label>-->
        <#--        <input type="textarea" id="text" name="argumentsfield" placeholder="enter parameters here">-->
        <#--        <input type="submit">-->

    </form>
    </p>


<#--    <p> by radius-->
<#--    <form method="GET" action="/radius">-->
<#--        <input type="submit">-->
<#--    </form>-->
<#--    </p>-->
    <body id="reurnValues">
    <p> Results:<br>
        ${message}
    </p>
    </body>


</#assign>
<#include "main.ftl">