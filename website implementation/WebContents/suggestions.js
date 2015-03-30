//suggestions.js

function StateSuggestions() {}

StateSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl , bTypeAhead ) {
    var aSuggestions = [];      
    document.getElementById("suggestion").innerHTML = '';
    xmlDoc = xmlHttp.responseXML;
    var suggestionArr = xmlDoc.getElementsByTagName("suggestion");
    for (var a=0; a<suggestionArr.length; a++)
    {
//        var dataAttr = suggestionArr[a].getAttribute("data");
        aSuggestions.push(suggestionArr[a].getAttribute("data"));
    }
    oAutoSuggestControl.autosuggest(aSuggestions, bTypeAhead);
};

StateSuggestions.prototype.sendAjaxRequest = function (oAutoSuggestControl, bTypeAhead) {
    xmlHttp = new XMLHttpRequest(); 
    var _this = this;
    var _textBoxValue = oAutoSuggestControl.textbox.value;
    var urlString = "./" + "suggest?" + "q=" + _textBoxValue;
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4)
            _this.requestSuggestions(oAutoSuggestControl, bTypeAhead);
    };
    xmlHttp.open("GET", urlString);
    xmlHttp.send(null);
};
