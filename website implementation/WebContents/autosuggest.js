// autosuggest.js

function AutoSuggestControl(oTextbox, oProvider){
	this.textbox = oTextbox;
	this.provider = oProvider;
	this.layer = null;
	this.cur = -1;
	this.init();
}

//codes from Text Selection section(part1 of tutorial)
AutoSuggestControl.prototype.selectRange = function (iStart, iLength) {
     if (this.textbox.createTextRange) {
         var oRange = this.textbox.createTextRange(); 
         oRange.moveStart("character", iStart); 
         oRange.moveEnd("character", iLength - this.textbox.value.length); 
         oRange.select();
     } else if (this.textbox.setSelectionRange) {
         this.textbox.setSelectionRange(iStart, iLength);
     } 

     this.textbox.focus(); 
 };


//codes from Updating init() section (part2 of tutorial)
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


//codes from Implementing Type Ahead section (part1 of tutorial)
AutoSuggestControl.prototype.typeAhead = function (sSuggestion) {
     if (this.textbox.createTextRange || this.textbox.setSelectionRange) {
         var iLen = this.textbox.value.length; 
         this.textbox.value = sSuggestion; 
         this.selectRange(iLen, sSuggestion.length);
     }
 };

//codes from updating the Autosuggest functionality section (part2 of tutorial)
AutoSuggestControl.prototype.autosuggest = function(aSuggestions, bTypeAhead){
	if(aSuggestions.length > 0){
	  if(bTypeAhead){
	    //this.typeAhead(aSuggestions[0]);
	  }
	  this.showSuggestions(aSuggestions);
	}
	else{
	  this.hideSuggestions();
	}
};

AutoSuggestControl.prototype.hideSuggestions = function () {
     this.layer.style.visibility = "hidden";
 };
 
 //codes from Adding and Display Suggestions section(part2 of tutorial)
 AutoSuggestControl.prototype.showSuggestions = function (aSuggestions) {

     var oDiv = null;
     this.layer.innerHTML = "";

     for (var i=0; i < aSuggestions.length; i++) {
         oDiv = document.createElement("div");
         oDiv.appendChild(document.createTextNode(aSuggestions[i]));
         this.layer.appendChild(oDiv);
     }

     this.layer.style.left = this.getLeft() + "px";
     this.layer.style.top = (this.getTop()+this.textbox.offsetHeight) + "px";
     this.layer.style.visibility = "visible";
};
 
 
 AutoSuggestControl.prototype.highlightSuggestion = function (oSuggestionNode) {
    
    for (var i=0; i < this.layer.childNodes.length; i++) {
        var oNode = this.layer.childNodes[i];
        if (oNode == oSuggestionNode) {
            oNode.className = "current"
        } else if (oNode.className == "current") {
            oNode.className = "";
        }
    }
};
 
 
 //codes from Adding Key Support section (part2 of tutorial)
AutoSuggestControl.prototype.previousSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;

    if (cSuggestionNodes.length > 0 && this.cur > 0) {
        var oNode = cSuggestionNodes[--this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue;   
    }
}; 
 
//codes from Adding Key Support section (part2 of tutorial)
AutoSuggestControl.prototype.nextSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;

    if (cSuggestionNodes.length > 0 && this.cur < cSuggestionNodes.length-1) {
        var oNode = cSuggestionNodes[++this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue; 
    }
}; 
 
 

//codes from Assigning Event Handlers section (part2 of tutorial)
AutoSuggestControl.prototype.createDropDown = function(){
	this.layer = document.createElement("div");
	this.layer.className = "suggestions";
	this.layer.style.visibility = "hidden";
	this.layer.style.width = this.textbox.offsetWidth;
	var oThis = this;
	this.layer.onmousedown = this.layer.onmouseup = 
	this.layer.onmouseover = function(oEvent){
	  oEvent = oEvent || window.event;
	  oTarget = oEvent.target || oEvent.srcElement;
	  if(oEvent.type == "mousedown"){
	    oThis.textbox.value = oTarget.firstChild.nodeValue;
	    oThis.hideSuggestions();
	  }
	  else if(oEvent.type == "mouseover"){
	    oThis.highlightSuggestion(oTarget);
	  }
	  else{
	    oThis.textbox.focus();
	  }
	};
	document.body.appendChild(this.layer);
}//end of createDropDown

//codes from Positioning the Dropdown List section (part2 of tutorial)
AutoSuggestControl.prototype.getLeft = function () {

     var oNode = this.textbox;
     var iLeft = 0;

     while(oNode.tagName != "BODY") {
         iLeft += oNode.offsetLeft;
         oNode = oNode.offsetParent; 
     }

     return iLeft;
 };

//codes from Positioning the Dropdown List section (part2 of tutorial)
AutoSuggestControl.prototype.getTop = function () {

     var oNode = this.textbox;
     var iTop = 0;

     while(oNode.tagName != "BODY") {
         iTop += oNode.offsetTop;
         oNode = oNode.offsetParent; 
     }

     return iTop;
 };

//codes from Updating the Autosuggest Functionality section
AutoSuggestControl.prototype.handleKeyUp = function (oEvent) {

     var iKeyCode = oEvent.keyCode;

    if (iKeyCode == 8 || iKeyCode == 46) { 
         this.provider.requestSuggestions(this, false);
	 this.provider.sendAjaxRequest(this, false);
     } else if (iKeyCode < 32 || (iKeyCode >= 33 && iKeyCode <= 46) || (iKeyCode >= 112 && iKeyCode <= 123)) {
         //ignore
     } else {
        // this.provider.requestSuggestions(this, true);
	this.provider.sendAjaxRequest(this, true);
     }
 };

//codes from Adding Key Support section
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


