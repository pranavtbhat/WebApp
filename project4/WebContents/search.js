"use strict";

var xmlHttp = new XMLHttpRequest();
var asc = null
var sp = null

function AutoSuggestControl(oTextbox, oProvider) {
    this.cur = -1;
    this.layer = null;
    this.provider = oProvider;
    this.textbox = oTextbox;
    this.init();
}

/* * 
 * Select a range of characters in the text box 
 * */
AutoSuggestControl.prototype.selectRange = function (iStart, iLength) {
    if (this.textbox.createTextRange) {
        // IE
        var oRange = this.textbox.createTextRange();
        oRange.moveStart("character", iStart);
        oRange.moveEnd("character", iLength - this.textbox.value.length);
        oRange.select();
    } else if (this.textbox.setSelectionRange) {
        // CHROME
        this.textbox.setSelectionRange(iStart, iLength);
    }

    this.textbox.focus();
};

/* * 
 * Highlight the next suggestion
 * */
AutoSuggestControl.prototype.nextSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;

    if (cSuggestionNodes.length > 0 && this.cur < cSuggestionNodes.length-1) {
        var oNode = cSuggestionNodes[++this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue; 
    }
};

/* * 
 * Highlight the previous suggestion
 * */
AutoSuggestControl.prototype.previousSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;

    if (cSuggestionNodes.length > 0 && this.cur > 0) {
        var oNode = cSuggestionNodes[--this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue;
    }
};

/* * 
 * Handle key down events in the drop box
 * */
AutoSuggestControl.prototype.handleKeyDown = function (oEvent) {
    switch(oEvent.keyCode) {
        case 38: //up arrow
            this.previousSuggestion();
            break;
        case 40: //down arrow
            this.nextSuggestion();
            break;
        case 13: //enter
            this.hideSuggestions();
            break;
    }
};

/* * 
 * Hide the list of suggestions
 * */
AutoSuggestControl.prototype.hideSuggestions = function () {
    this.layer.style.visibility = "hidden";
};


/* * 
 * Swap the current item
 * */
AutoSuggestControl.prototype.highlightSuggestion = function (oSuggestionNode) {
    for (var i=0; i < this.layer.childNodes.length; i++) {
        var oNode = this.layer.childNodes[i];
        if (oNode == oSuggestionNode) {
            oNode.className = "current"
            oNode.style.backgroundColor = "#3366cc";
            oNode.style.color = "white";
        } else if (oNode.className == "current") {
            oNode.className = "item";
            oNode.style.backgroundColor = "white";
            oNode.style.color = "black";
        }
    }
};

/* * 
 * Create the drop down list of suggestions
 * */
AutoSuggestControl.prototype.createDropDown = function () {
    this.layer = document.createElement("div");
    this.layer.className = "suggestions";
    this.layer.style.visibility = "hidden";
    this.layer.style.width = this.textbox.offsetWidth;
    document.body.appendChild(this.layer);

    var oThis = this;

    function eventHandler (oEvent) {
        var oEvent = oEvent || window.event;
        var oTarget = oEvent.target || oEvent.srcElement;

        if (oEvent.type == "mousedown") {
            oThis.textbox.value = oTarget.firstChild.nodeValue;
            oThis.hideSuggestions();
        }
        else if (oEvent.type == "mouseover") {
            oThis.highlightSuggestion(oTarget);
        } 
        else {
            oThis.textbox.focus();
        }
    };

    this.layer.onmousedown = eventHandler
    this.layer.onmouseup = eventHandler 
    this.layer.onmouseover = eventHandler
};

/* * 
 * Get the left offset of the textbox. Required to position the drop down list
 * */
AutoSuggestControl.prototype.getLeft = function () {

    var oNode = this.textbox;
    var iLeft = 0;

    while(oNode.tagName != "BODY") {
        iLeft += oNode.offsetLeft;
        oNode = oNode.offsetParent;
    }

    return iLeft;
};

/* * 
 * Get the top offset of the textbox. Required to position the drop down list
 * */
AutoSuggestControl.prototype.getTop = function () {

    var oNode = this.textbox;
    var iTop = 0;

    while(oNode.tagName != "BODY") {
        iTop += oNode.offsetTop;
        oNode = oNode.offsetParent; 
    }

    return iTop;
};

/* * 
 * For a given suggestion, insert the completion into 
 * the text box and highlight the suggestion part of the text
 * */
AutoSuggestControl.prototype.typeAhead = function (sSuggestion) {
    if (this.textbox.createTextRange || this.textbox.setSelectionRange) {
        var iLen = this.textbox.value.length;
        this.textbox.value = sSuggestion;
        this.selectRange(iLen, sSuggestion.length);
    }
};

/* * 
 * Pick the first suggestion from the list of suggestions for type ahead 
 * */
AutoSuggestControl.prototype.autosuggest = function (aSuggestions, bTypeAhead) {
    if (aSuggestions.length > 0) {
        if (bTypeAhead) {
            this.typeAhead(aSuggestions[0]);
        }
        this.showSuggestions(aSuggestions);
    }
    else{
        this.hideSuggestions();
    }
};

/* * 
 * Trigger the autocomplete functionality for select key up events
 * */
AutoSuggestControl.prototype.handleKeyUp = function (oEvent) {
    var iKeyCode = oEvent.keyCode;
    if (iKeyCode == 8 || iKeyCode == 46) {
        this.provider.requestSuggestions(this, false);
    }
    else if (iKeyCode < 32 || (iKeyCode >= 33 && iKeyCode <= 46) || (iKeyCode >= 112 && iKeyCode <= 123)) {
        // ignore
    } 
    else {
        this.provider.requestSuggestions(this, true);
    }
};

/* * 
 * Initialize the key handler
 * */
AutoSuggestControl.prototype.init = function () {
    var oThis = this;
    this.textbox.onkeyup = function (oEvent) {
        if (!oEvent) {
            oEvent = window.event;
        }
        oThis.handleKeyUp(oEvent);
    };

    this.textbox.onkeydown = function (oEvent) {

        if (!oEvent) {
            oEvent = window.event;
        }

        oThis.handleKeyDown(oEvent);
    };

    this.textbox.onblur = function () {
        oThis.hideSuggestions();
    };

    this.createDropDown();
};

/* * 
 * Parse the XML fetched from the proxy and generate an array of suggestions.
 * Forward the suggestions to the autosuggest function 
 * */
AutoSuggestControl.prototype.processSuggestions = function (oAutoSuggestControl, bTypeAhead) {
    if (xmlHttp.readyState == 4) {
        var aSuggestions = new Array();
        var s = xmlHttp.responseXML.getElementsByTagName('CompleteSuggestion');
        for (var i = 0; i < s.length; i++) {
            var text = s[i].childNodes[0].getAttribute("data");
            aSuggestions[i] = text;
        }
        oAutoSuggestControl.autosuggest(aSuggestions, bTypeAhead);
    }
}

/* * 
 * Parse the XML fetched from the proxy and generate an array of suggestions.
 * Forward the suggestions to the autosuggest function 
 * */
AutoSuggestControl.prototype.showSuggestions = function (aSuggestions) {
    var oDiv = null;
    this.layer.innerHTML = "";

    for (var i=0; i < aSuggestions.length; i++) {
        oDiv = document.createElement("div");
        oDiv.style.class = "item";
        oDiv.style.backgroundColor = "white";
        oDiv.style.color = "black";
        oDiv.appendChild(document.createTextNode(aSuggestions[i]));
        this.layer.appendChild(oDiv);
    }

    this.layer.style.left = this.getLeft() + "px";
    this.layer.style.top = (this.getTop()+this.textbox.offsetHeight) + "px";
    this.layer.style.visibility = "visible";
};


function SuggestionProvider() {
    //any initializations needed go here
}

/* * 
 * Fire a get request to the proxy, and direct the result to processSuggestions 
 * */
SuggestionProvider.prototype.requestSuggestions = function (oAutoSuggestControl, bTypeAhead) {
    function createCallBack() {
        return function() {
            oAutoSuggestControl.processSuggestions(oAutoSuggestControl, bTypeAhead)
        }
    }

    var aSuggestions = [];
    var sTextboxValue = oAutoSuggestControl.textbox.value;

    if(sTextboxValue) {
        var request = "suggest?query="+encodeURI(sTextboxValue);
        xmlHttp.open("GET", request);
        xmlHttp.onreadystatechange = createCallBack()
        xmlHttp.send(null);
    }
};

window.onload = function () {
    sp = new SuggestionProvider();
    asc = new AutoSuggestControl(document.getElementById("input"), sp);
}
