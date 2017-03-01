"use strict";

var xmlHttp = new XMLHttpRequest();
var asc = null
var sp = null

function AutoSuggestControl(oTextbox, oProvider) {
    this.provider = oProvider;
    this.textbox = oTextbox;
    this.init();
}

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

AutoSuggestControl.prototype.typeAhead = function (sSuggestion) {
    if (this.textbox.createTextRange || this.textbox.setSelectionRange) {
        var iLen = this.textbox.value.length;
        this.textbox.value = sSuggestion;
        this.selectRange(iLen, sSuggestion.length);
    }
};

AutoSuggestControl.prototype.autosuggest = function (aSuggestions) {
    if (aSuggestions.length > 0) {
        this.typeAhead(aSuggestions[0]);
    }
};

AutoSuggestControl.prototype.handleKeyUp = function (oEvent) {
    var iKeyCode = oEvent.keyCode;
    if (iKeyCode < 32 || (iKeyCode >= 33 && iKeyCode <= 46) || (iKeyCode >= 112 && iKeyCode <= 123)) {
        // ignore
    } else {
        this.provider.requestSuggestions(this);
    }
};

AutoSuggestControl.prototype.init = function () {
    var oThis = this;
    this.textbox.onkeyup = function (oEvent) {
        if (!oEvent) {
            oEvent = window.event;
        }
        oThis.handleKeyUp(oEvent);
    };
};

AutoSuggestControl.prototype.processSuggestions = function () {
    if (xmlHttp.readyState == 4) {
        var aSuggestions = new Array();
        var s = xmlHttp.responseXML.getElementsByTagName('CompleteSuggestion');
        console.log(s);
        for (var i = 0; i < s.length; i++) {
            var text = s[i].childNodes[0].getAttribute("data");
            aSuggestions[i] = text;
        }
        console.log(aSuggestions);
        asc.autosuggest(aSuggestions);
    }
}


function SuggestionProvider() {
    //any initializations needed go here
}

SuggestionProvider.prototype.requestSuggestions = function (oAutoSuggestControl) {
    var aSuggestions = [];
    var sTextboxValue = oAutoSuggestControl.textbox.value;
    console.log(sTextboxValue);

    if(sTextboxValue) {
        var request = "suggest?query="+encodeURI(sTextboxValue);
        xmlHttp.open("GET", request);
        xmlHttp.onreadystatechange = oAutoSuggestControl.processSuggestions;
        xmlHttp.send(null);
    }
};

window.onload = function () {
    sp = new SuggestionProvider();
    asc = new AutoSuggestControl(document.getElementById("input"), sp);
}
